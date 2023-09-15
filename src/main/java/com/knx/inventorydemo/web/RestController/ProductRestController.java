package com.knx.inventorydemo.web.RestController;

import java.util.ArrayList;
import java.util.Map;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.entity.Vendor;
import com.knx.inventorydemo.exception.ProductValidationException;

@RestController
@RequestMapping("/api/product")
public class ProductRestController {

    @Autowired
    ProductService productService;

    @Autowired
    MeasurementService measurementService;
    
    @GetMapping(path = "/{id}", consumes = "application/json")
    public ProductMeta getProduct(@PathVariable String id){

        // ObjectNode node = mapper.createObjectNode();

        // HashMap<String, ProductMeta> hashMap = new HashMap<String, ProductMeta>();

        // hashMap.put("9971", new ProductMeta().setId("9971").setName("Apollo Cake Chocolate 24pcs"));
        // hashMap.put("9972", new ProductMeta().setId("9972").setName("Apollo Cake Original 24pcs"));
        // hashMap.put("8891", new ProductMeta().setId("8891").setName("Nabati Strawberry"));
        // hashMap.put("9991", new ProductMeta().setId("9991").setName("No30gg Star 60pcs"));

        // HashMap<String, String> returnMap = new HashMap<String, String>();
        
        // if(hashMap.containsKey(id)) {
        //     returnMap.put("id", hashMap.get(id).getId());
        //     returnMap.put("name", hashMap.get(id).getName());
        // }

        // return !returnMap.isEmpty() ? returnMap : null;

        ProductMeta productMetaById = productService.getProductMetaById(id);
        return productMetaById;
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addProduct(@RequestBody(required = true) ObjectNode product){

        JsonNode productNode = product.get("product");
        ProductMeta productMeta = new ProductMeta();
        productMeta.setId(productNode.get("id").asText())
            .setName(productNode.get("name").asText())
            .setDefaultUom(productNode.has("default_uom") ? productNode.get("default_uom").asText() : ProductMeasurement.DEFAULT_UOM)
            .setVendor(productNode.has("vendor_name") ?  new Vendor().setName(productNode.get("vendor_name").asText()) : null)
            .setActivity(productNode.has("activity") ? productNode.get("activity").asBoolean() : false);

        ProductMeasurement measurement = null;
        if(productNode.has("measurement") && !productNode.get("measurement").isNull()){
            measurement = new ProductMeasurement();
            JsonNode measurementNode = productNode.get("measurement");
            measurement.setUOM_name(measurementNode.get("name").asText())
                .setProductId(productMeta.getId())
                .setAnotherBarcode(measurementNode.has("barcode") ? measurementNode.get("barcode").asText() : null)
                .setMeasurement(Float.parseFloat(measurementNode.get("measure").asText()))
                .setSalesChannel(ProductUOM.LAYER)
                .setRelativeId(measurementNode.has("relative_id") ? measurementNode.get("relative_id").asText()
                     : measurement.getProductId() + "-" + measurement.getUOM_name());
        }


        if(productMeta.getId() == null || productMeta.getId().isEmpty()) throw new ProductValidationException("product id is null", productMeta);

        if(measurement == null){
            productService.addNewProduct(productMeta);
        } else {
            productService.addNewProduct(productMeta, measurement);
        }
        return ResponseEntity.ok("\"create it\"");
    }

    @PutMapping(path = "/{id}")
    public void updateProduct(@PathVariable String id, @RequestBody(required = true) ObjectNode objectNode){
        
        JsonNode productNode = objectNode.get("product");

        ProductMeta productMeta = new ProductMeta();
        productMeta.setId(id)
            .setName(productNode.get("name").asText())
            .setActivity(productNode.has("active") ? productNode.get("active").asText().equals("true") : false)
            .setDefaultUom(productNode.has("default_uom") ? productNode.get("default_uom").asText() : null)
            .setVendor(productNode.has("vendor") ? new Vendor().setName(productNode.get("vendor").asText()) : null);

        ProductMeta.valid(productMeta);

        ProductMeta productMetaById = productService.getProductMetaById(id);
        if(!productMetaById.getDefaultUom().equals(productMeta.getDefaultUom())){

            String relativeId = productMeta.getId() + "-" + productMeta.getDefaultUom();

            ProductMeasurement measurement = null;
            if(productNode.has("measurement")){

                JsonNode measurementNode = productNode.get("measurement");

                ArrayList<String> list = new ArrayList<String>();
                list.add(relativeId);
                Map<String, ProductMeasurement> productMeasByRelativeIds = measurementService.getProductMeasByRelativeIds(list);


                if(!productMeasByRelativeIds.containsKey(relativeId)){

                    // measurement is not existed, create it.
                    measurement = new ProductMeasurement();
                    measurement.setProductId(productMeta.getId())
                        .setMeasurement(Float.parseFloat(measurementNode.get("measure").asText()))
                        .setAnotherBarcode(measurementNode.has("barcode") ? measurementNode.get("barcode").asText() : null)
                        .setSalesChannel(ProductUOM.LAYER)
                        .setUOM_name(measurementNode.get("uom").asText());

                    ProductMeasurement.valid(measurement);

                    measurementService.addNewMeasurementToProduct(productMeta, measurement);
                }
            }
        }
        productService.update(productMeta);
    } 

    @PutMapping(path = "/increaseFrequently", consumes = "application/json")
    public void indexImprove(@RequestBody String id){
          
    }

    // @ExceptionHandler(ProductValidationException.class)
    // public 
}
