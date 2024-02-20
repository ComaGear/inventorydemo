package com.knx.inventorydemo.web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.web.RestController.entity.ProductMetaMeasurementsDTO;

@Controller
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    @Autowired
    private MeasurementService measurementService;

    @GetMapping(path = "/{id}")
    public String getProduct(@PathVariable String id, Model model){

        if(id == null || id.isEmpty()) throw new IllegalArgumentException("query product detail id should not be null.");

        ProductMeta productMetaById = productService.getProductMetaById(id);
        List<ProductMeasurement> measurements = measurementService.findAllCustomMeasurementByProductId(productMetaById.getId());
    
        ProductMetaMeasurementsDTO productMetaMeasurementsDTO = new ProductMetaMeasurementsDTO(productMetaById);
        productMetaMeasurementsDTO.setMeasurements(measurements);

        model.addAttribute("productMeasDTO", productMetaMeasurementsDTO);

        return "product";
    }

    @PostMapping(path = "/")
    public RedirectView createProduct(@RequestBody ProductMetaMeasurementsDTO productMetaMeasurements){

        ProductMeta meta = productMetaMeasurements;

        if(productMetaMeasurements.getMeasurements() == null || productMetaMeasurements.getMeasurements().isEmpty()){
            productService.addNewProduct(meta);
            ProductMeta repositoryProductMeta = productService.getProductMetaById(meta.getId());
            ProductMetaMeasurementsDTO repositoryProductMetaMeasurementDto = new ProductMetaMeasurementsDTO(repositoryProductMeta);
            List<ProductMeasurement> repositoryMeasurements = measurementService.findAllCustomMeasurementByProductId(repositoryProductMetaMeasurementDto.getId());
            repositoryProductMetaMeasurementDto.setMeasurements(repositoryMeasurements);
            
            return new RedirectView("product/" + meta.getId());
        }

        // getting default relative id set by user.
        String defaultRelativeUomId = productMetaMeasurements.getDefaultUom();
        List<ProductMeasurement> measurements = productMetaMeasurements.getMeasurements();
        ProductMeasurement defaultProductMeasurement = null;
        for(ProductMeasurement measure : measurements){
            measure.setSalesChannel(ProductUOM.LAYER); // TODO : temporary fix problem. please looking back design notebook how better improve it.A
            if(measure.getRelativeId().equals(defaultRelativeUomId)) defaultProductMeasurement = measure;
        }
        productService.addNewProduct(meta, defaultProductMeasurement);

        ProductMeta repositoryProductMeta = productService.getProductMetaById(meta.getId());
        ProductMetaMeasurementsDTO repositoryProductMetaMeasurementDto = new ProductMetaMeasurementsDTO(repositoryProductMeta);
        List<ProductMeasurement> repositoryMeasurements = measurementService.findAllCustomMeasurementByProductId(repositoryProductMetaMeasurementDto.getId());
        repositoryProductMetaMeasurementDto.setMeasurements(repositoryMeasurements);
        
        return new RedirectView("product/" + meta.getId());
    }

    @GetMapping(path = "/create")
    public String createProduct(){

        return "product";
    }
}
