package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;

// @Transactional
// @ContextConfiguration(classes = PersistenceConfig.class)
@SpringBootTest
public class ProductMetaAndMeasureTests {
    
    @Autowired
    ProductService productService;

    @Autowired
    MeasurementService measurementService;

    ProductMeta product;
    ProductMeasurement productMeas;

    private ProductMeta productChild;
    private ProductMeasurement productMeasChild;

    private Logger logger = Logger.getLogger(this.getClass().toString());

    @BeforeEach
    public void creatingATestingProductMeta(){
        product = new ProductMeta()
            .setId("9667")
            .setName("Nabati Chocolate 40g x 10pcs")
            .setDefaultUom("UNIT");
            
        productChild = new ProductMeta()
            .setId("8988")
            .setName("Nabati Cheese 40g x 10pcs")
            .setDefaultUom("UNIT");

        productMeas = new ProductMeasurement()
            .setProductId(product.getId())
            .setMeasurement(1)
            .setRelativeId("2232001")
            .setUOM_name(null)
            .setSalesChannel("online");

        productMeasChild = new ProductMeasurement()
            .setProductId(productChild.getId())
            .setMeasurement(1)
            .setSalesChannel("online")
            .setUOM_name(null);
    }

    @Test
    public void addOneProductAndMerchantUOM(){
        productService.addNewProduct(product);

        assertEquals("Nabati Chocolate 40g x 10pcs", productService.getProductMetaById(product.getId()).getName()); 
    }

    @Test
    public void addOneCustomMeasurement(){
        measurementService.addNewMeasurementToProduct(product, productMeas);

        measurementService.findAllCustomMeasurementByProductId(productMeas.getSalesChannel(), product.getId());
    }

    @Test
    public void addChildSkuToExistsSkuWithUpdateParent() throws IllegalAccessException{
        productService.addNewProduct(productChild);

        String newSku = measurementService.addChildSkuToExistsSku(productMeasChild, productMeas.getRelativeId());
        productMeas.setRelativeId(newSku);

        assertEquals(productMeas.getRelativeId() + "-a", measurementService.findAllCustomMeasurementByProductId(
            productMeas.getSalesChannel(), productMeas.getProductId()).get(0).getRelativeId());
    }

    // @Test
    // public void changeUpdateRuleToCustomLayerByRelativeId(){
    //     productMeas.setUpdateRule("4t");
    //     measurementService.updateMeasUpdateRule(productMeas.getRelativeId()
    //         ,null
    //         ,"4t"
    //         ,productMeas.getLayer());
    // }

    

    public void settingNewUpdateRule(){
    }

    @Test
    public void gettingMeasuresByRelativeId(){

    }
}