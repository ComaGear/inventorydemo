package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.util.MessageSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.Service.StockingService;
import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockInDocs;
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.exception.ProductUnactivityException;

@SpringBootTest
public class StockingTests {

    @Autowired
    ProductService productService;
    @Autowired
    MeasurementService measurementService;
    @Autowired
    StockingService stockingService;
    
    private ProductMeta productMeta9971;
    private ProductMeta productMeta1121;
    private ProductMeasurement measureCTNfor9971;

    private ProductMeta productMeta1133;

    private Order orderO1212MERCHANTz2;
    private Order orderO2211MERCHANTz1;
    private StockInDocs docsI1112z3;
    private Order orderO1133MERCHANTz1;

    Logger log = LoggerFactory.getLogger(getClass());


    @BeforeEach
    public void prepareEntity(){
        
        productMeta9971 = new ProductMeta();
        productMeta9971.setId("9971").setName("nabati").setDefaultUom("UNIT").setActivity(true);
        productMeta1121 = new ProductMeta();
        productMeta1121.setId("1121").setName("apollo cake").setDefaultUom("UNIT").setActivity(true);

        measureCTNfor9971 = new ProductMeasurement();
        measureCTNfor9971.setProductId(productMeta9971.getId()).setUOM_name("CTN").setMeasurement(6);

        // unactivity product
        productMeta1133 = new ProductMeta();
        productMeta1133.setId("1133").setName("lb3cl").setDefaultUom("UNIT").setActivity(false);
        

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

            StockMoveOut moveOut1133UNITby1 = (StockMoveOut) new StockMoveOut().setProductId(productMeta1133.getId()).setDate(date)
            .setQuantity(1).setUsedUOM("UNIT");
            
        
        orderO1212MERCHANTz2 = new Order().setOrderId("O1212").setChannel("MERCHANT").setDate(date);
        orderO1212MERCHANTz2.pushMovement(moveOut9971UNITby5);
        moveOut9971UNITby5.setOrderId(orderO1212MERCHANTz2.getOrderId());
        orderO1212MERCHANTz2.pushMovement(moveOut1121UNITby200);
        moveOut1121UNITby200.setOrderId(orderO1212MERCHANTz2.getOrderId());
        
        orderO2211MERCHANTz1 = new Order().setOrderId("o2211").setChannel("MERCHANT").setDate(date);
        orderO2211MERCHANTz1.pushMovement(moveOut9971CTNby2);
        moveOut9971CTNby2.setOrderId(orderO2211MERCHANTz1.getOrderId());

        orderO1133MERCHANTz1 = new Order().setOrderId("O1133").setChannel("MERCHANT").setDate(date);
        orderO1133MERCHANTz1.pushMovement(moveOut1133UNITby1);
        moveOut1133UNITby1.setOrderId(orderO1133MERCHANTz1.getOrderId());
        
        docsI1112z3 = new StockInDocs().setDocsId("I1112").setDate(date);
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
        stockingService.pushMovement(orderO1212MERCHANTz2);
        stockingService.pushMovement(orderO2211MERCHANTz1);
        stockingService.pushMoveIns(docsI1112z3);
        stockingService.updateToRepository();

        List<String> orderIds = new LinkedList<String>();
        orderIds.add(orderO1212MERCHANTz2.getOrderId());
        orderIds.add(orderO2211MERCHANTz1.getOrderId());
        List<Order> orderRecord = stockingService.getOrderRecord(orderIds);

        List<String> moveOuts = new LinkedList<String>();
        for(StockMoveOut moveOut : orderO1212MERCHANTz2.getMovements()) {
            String id = moveOut.getOrderId() + "-" + moveOut.getRelativeId() + "-" + moveOut.getQuantity() + "-" + moveOut.getUsedUOM();
            moveOuts.add(id);
        }
        for(StockMoveOut moveOut : orderO2211MERCHANTz1.getMovements()) {
            String id = moveOut.getOrderId() + "-" + moveOut.getRelativeId() + "-" + moveOut.getQuantity() + "-" + moveOut.getUsedUOM();
            moveOuts.add(id);
        }
        for(Order order : orderRecord){
            for(StockMoveOut moveOut : order.getMovements()){
                String id = moveOut.getOrderId() + "-" + moveOut.getRelativeId() + "-" + moveOut.getQuantity() + "-" + moveOut.getUsedUOM();
                // log.debug("iterating to " + id);
                assertTrue(moveOuts.contains(id), "it does contained from repository" + id);
            }
        }


        List<String> docsIds = new LinkedList<String>();
        docsIds.add(docsI1112z3.getDocsId());
        List<StockInDocs> docsRecord = stockingService.getDocsRecord(docsIds);

        List<String> MoveIns = new LinkedList<String>();
        for(StockMoveIn moveIn : docsI1112z3.getMovements()) {
            String id = moveIn.getDocsId() + "-" + moveIn.getRelativeId() + "-" + moveIn.getQuantity() + "-" + moveIn.getUsedUOM();
            MoveIns.add(id);
        }
        for(StockInDocs docs : docsRecord){
            for(StockMoveIn moveIn : docs.getMovements()){
                String id = moveIn.getDocsId() + "-" + moveIn.getRelativeId() + "-" + moveIn.getQuantity() + "-" + moveIn.getUsedUOM();
                // log.debug("iterating to " + id);
                assertTrue(MoveIns.contains(id), "it does contained from repository" + id);
            }
        }
    }

    @Test
    public void tryPushingUnactivityProductMovement(){
        try{
            stockingService.pushMovement(orderO1133MERCHANTz1);
        } catch(ProductUnactivityException e){
            List<String> unactivityProductList = e.getUnactivityProductList();
            assertEquals(productMeta1133.getId(), unactivityProductList.get(0));
        }
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
