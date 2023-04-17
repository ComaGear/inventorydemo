package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedList;
import java.util.Map;
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

    //TODO: test for update inserted product meta and change product id also change measure's relation
    //TODO: delete product meta with all measurement relate it.
    //TODO: change measurement relative id.
    //TODO: test code for checking product activity both get activity and unactivity.

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
    public void obtainMeasurementByBulkGetShouldSuccess(){
        LinkedList<String> relativeIds = new LinkedList<String>();
        String m1 = "2232001-a";
        relativeIds.add(m1);
        String m2 = "2232001";
        relativeIds.add(m2);

        Map<String, ProductMeasurement> returnList = measurementService.getProductMeasByRelativeIdWithChannel(relativeIds, "online");

        assertEquals(returnList.get(m1).getProductId(), "9667");
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
