package com.knx.inventorydemo;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockInDocs;
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;

@SpringBootTest
public class StockingTests {

    @Autowired
    ProductService productService;
    @Autowired
    MeasurementService measurementService;
    
    private ProductMeta productMeta9971;
    private ProductMeta productMeta1121;

    private ProductMeasurement measureCTNfor9971;

    @BeforeEach
    public void prepareEntity(){
        
        productMeta9971 = new ProductMeta();
        productMeta9971.setId("9971").setName("nabati").setDefaultUom("UNIT");
        productMeta1121 = new ProductMeta();
        productMeta1121.setId("1121").setName("apollo cake").setDefaultUom("UNIT");

        measureCTNfor9971 = new ProductMeasurement();
        measureCTNfor9971.setProductId(productMeta9971.getId()).setUOM_name("CTN").setMeasurement(6);
        

        Date date = new Date(System.currentTimeMillis());
        StockMoveOut moveOut9971UNITby5 = (StockMoveOut) new StockMoveOut().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(5).setUsedUOM("UNIT");
        StockMoveOut moveOut9971CTNby2 = (StockMoveOut) new StockMoveOut().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(2).setUsedUOM("CTN");
        StockMoveOut moveOut1121UNITby200 = (StockMoveOut) new StockMoveOut().setProductId(productMeta1121.getId()).setDate(date)
            .setQuantity(200).setUsedUOM("UNIT");

        StockMoveIn moveIn9971UNITby200 = (StockMoveIn) new StockMoveIn().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(200).setUsedUOM("UNIT");
        StockMoveIn moveIn1121UNITby500 = (StockMoveIn) new StockMoveIn().setProductId(productMeta1121.getId()).setDate(date)
            .setQuantity(500).setUsedUOM("UNIT");
        StockMoveIn moveIn9971CTNby2 = (StockMoveIn) new StockMoveIn().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(2).setUsedUOM("CTN");
            
        
        Order orderO1212MERCHANTz2 = new Order().setOrderId("O1212").setChannel("MERCHANT").setDate(date);
        orderO1212MERCHANTz2.pushMovement(moveOut9971UNITby5);
        moveOut9971UNITby5.setOrderId(orderO1212MERCHANTz2.getOrderId());
        orderO1212MERCHANTz2.pushMovement(moveOut1121UNITby200);
        moveOut1121UNITby200.setOrderId(orderO1212MERCHANTz2.getOrderId());
        
        Order orderO2211MERCHANTz1 = new Order().setOrderId("o2211").setChannel("MERCHANT").setDate(date);
        orderO2211MERCHANTz1.pushMovement(moveOut9971CTNby2);
        moveOut9971CTNby2.setOrderId(orderO2211MERCHANTz1.getOrderId());
        
        StockInDocs docsI1112z3 = new StockInDocs().setDocsId("I1112").setDate(date);
        docsI1112z3.pushMoveIn(moveIn9971UNITby200);
        moveIn9971UNITby200.setDocsId(docsI1112z3.getDocsId()).setItemRowOfDocs(1);
        docsI1112z3.pushMoveIn(moveIn9971CTNby2);
        moveIn9971CTNby2.setDocsId(docsI1112z3.getDocsId()).setItemRowOfDocs(2);
        docsI1112z3.pushMoveIn(moveIn1121UNITby500);
        moveIn1121UNITby500.setDocsId(docsI1112z3.getDocsId()).setItemRowOfDocs(3);
        
    }

    @Test
    public void creatingProduct(){
        productService.addNewProduct(productMeta9971);
        measurementService.addNewMeasurementToProduct(productMeta9971, measureCTNfor9971);
        productService.addNewProduct(productMeta1121);
    }

    @Test 
    public void insertOrderToRepository(){
        
    }

    @Test
    public void insertExistOrderWithRejectReturn(){

    }

    @Test
    public void updateModifiedOrderToRepository(){
        
        //a duplicate order but new moveOut ensure inserted

        //a duplicate order but got moveOut has been removed ensure removed

        //a duplicate order exists moveOut with reject return ensure correct size.
    }

    @Test
    public void removeAllMoveOutByOrderShouldEnsureAllRemoved(){

    }

    @Test
    public void insertDocsMoveInToRepository(){

    }
}
