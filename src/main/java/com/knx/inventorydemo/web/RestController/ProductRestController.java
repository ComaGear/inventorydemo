package com.knx.inventorydemo.web.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.entity.Vendor;
import com.knx.inventorydemo.exception.ProductValidationException;

@RestController
@RequestMapping("/product")
public class ProductRestController {

    @Autowired
    ProductService productService;
    
    @GetMapping("/{id}")
    public Map<String, String> getProduct(@PathVariable String id){

        HashMap<String, ProductMeta> hashMap = new HashMap<String, ProductMeta>();

        hashMap.put("9971", new ProductMeta().setId("9971").setName("Apollo Cake Chocolate 24pcs"));
        hashMap.put("9972", new ProductMeta().setId("9972").setName("Apollo Cake Original 24pcs"));
        hashMap.put("8891", new ProductMeta().setId("8891").setName("Nabati Strawberry"));
        hashMap.put("9991", new ProductMeta().setId("9991").setName("No30gg Star 60pcs"));

        HashMap<String, String> returnMap = new HashMap<String, String>();
        
        if(hashMap.containsKey(id)) {
            returnMap.put("id", hashMap.get(id).getId());
            returnMap.put("name", hashMap.get(id).getName());
        }

        return !returnMap.isEmpty() ? returnMap : null;
    }

    @PostMapping(path = "/", produces = "application/json")
    public @ResponseBody void addProduct(@RequestBody(required = true) ObjectNode product){

        JsonNode productNode = product.get("product");
        ProductMeta productMeta = new ProductMeta();
        productMeta.setId(productNode.get("id").asText())
            .setName(productNode.get("name").asText())
            .setDefaultUom(productNode.has("uom") ? productNode.get("uom").asText() : ProductMeasurement.DEFAULT_UOM)
            .setVendor(new Vendor().setName(productNode.get("vendor_name").asText()))
            .setActivity(productNode.get("active").asText().equals("true"));

        ProductMeasurement measurement = null;
        if(!productNode.get("measurement").isNull()){
            measurement = new ProductMeasurement();
            measurement.setUOM_name(productNode.get("name").asText())
                .setProductId(productMeta.getId())
                .setAnotherBarcode(productNode.has("barcode") ? productNode.get("barcode").asText() : null)
                .setMeasurement(Float.parseFloat(productNode.get("measure").asText()))
                .setRelativeId(productNode.has("relative_id") ? productNode.get("relative_id").asText()
                     : measurement.getProductId() + "-" + measurement.getUOM_name());
        }


        if(productMeta.getId() == null || productMeta.getId().isEmpty()) throw new ProductValidationException("product id is null", productMeta);

        if(measurement == null){
            productService.addNewProduct(productMeta);
        } else {
            productService.addNewProduct(productMeta, measurement);
        }
    }

    @PutMapping(path = "/{id}")
    public void updateProduct(@PathVariable String id, @RequestBody(required = true) ObjectNode objectNode){
        
        if()
    } 

    @PutMapping(path = "/increaseFrequently", consumes = "application/json")
    public void indexImprove(@RequestBody String id){
          
    }

    // @ExceptionHandler(ProductValidationException.class)
    // public 
}
