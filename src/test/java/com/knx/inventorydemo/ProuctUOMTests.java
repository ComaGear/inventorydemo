package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;

@SpringBootTest
public class ProuctUOMTests {
    
    @Autowired
    ProductService productService;

    ProductMeta product;
    ProductMeasurement measurement;

    @BeforeAll
    public void creatingATestingProductMeta(){
        product = new ProductMeta()
            .setId("9667")
            .setName("Nabati 40g x 10pcs")
            .setDefaultUom("UNIT");

        measurement = new ProductMeasurement()
            .setProductId(product.getId())
            .setMeasurement(1)
            .setRelativeId("2232001")
            .setUOM_name(null)
            .setLayer("online");
    }

    @Test
    public void addOneProductAndMerchantUOM(){
        productService.addNewProduct(product);

        assertEquals("Nabati 40g x 10pcs", productService.getProductMetaById(product.getId()).getName()); 
    }

    @Test
    public void addChildSkuToExistsSku(){
        
    }

    @Test
    public void changeUpdateRuleToCustomLayerByRelativeId(){

    }

    @Test
    public void addOneCustomMeasurement(){

        productService.addNewMeasurementToProduct(product, measurement);

        productService.findAllCustomMeasurementByProductId(product.getId());
    }

    public void settingNewUpdateRule(){
    }

    @Test
    public void gettingMeasuresByRelativeId(){

    }
}
