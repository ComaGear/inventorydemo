package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.Service.StockMovementService;
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
    @Autowired
    StockMovementService stockMovementService;
    
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
        measureCTNfor9971.setProductId(productMeta9971.getId()).setUOM_name("CTN").setMeasurement(6)
            .setRelativeId(productMeta9971.getId() + "-" + "CTN").setSalesChannel("MERCHANT");

        // unactivity product
        productMeta1133 = new ProductMeta();
        productMeta1133.setId("1133").setName("lb3cl").setDefaultUom("UNIT").setActivity(false);
        

        Date date = new Date(System.currentTimeMillis());
        StockMoveOut moveOut9971UNITby5 = (StockMoveOut) new StockMoveOut().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(5).setUsedUOM("UNIT").setSalesChannel("MERCHANT");
        moveOut9971UNITby5.setRelativeId(moveOut9971UNITby5.getProductId() + "-" + moveOut9971UNITby5.getUsedUOM());

        StockMoveOut moveOut9971CTNby2 = (StockMoveOut) new StockMoveOut().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(2).setUsedUOM("CTN").setSalesChannel("MERCHANT");
        moveOut9971CTNby2.setRelativeId(moveOut9971CTNby2.getProductId() + "-" + moveOut9971CTNby2.getUsedUOM());
        StockMoveOut moveOut1121UNITby200 = (StockMoveOut) new StockMoveOut().setProductId(productMeta1121.getId()).setDate(date)
            .setQuantity(200).setUsedUOM("UNIT").setSalesChannel("MERCHANT");
        moveOut1121UNITby200.setRelativeId(moveOut1121UNITby200.getProductId() + "-" + moveOut1121UNITby200.getUsedUOM());

        StockMoveIn moveIn9971UNITby200 = (StockMoveIn) new StockMoveIn().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(200).setUsedUOM("UNIT").setSalesChannel("MERCHANT");
        moveIn9971UNITby200.setRelativeId(moveIn9971UNITby200.getProductId() + "-" + moveIn9971UNITby200.getUsedUOM());
        StockMoveIn moveIn1121UNITby500 = (StockMoveIn) new StockMoveIn().setProductId(productMeta1121.getId()).setDate(date)
            .setQuantity(500).setUsedUOM("UNIT").setSalesChannel("MERCHANT");
        moveIn1121UNITby500.setRelativeId(moveIn1121UNITby500.getProductId() + "-" + moveIn1121UNITby500.getUsedUOM());
        StockMoveIn moveIn9971CTNby2 = (StockMoveIn) new StockMoveIn().setProductId(productMeta9971.getId()).setDate(date)
            .setQuantity(2).setUsedUOM("CTN").setSalesChannel("MERCHANT");
        moveIn9971CTNby2.setRelativeId(moveIn9971CTNby2.getProductId() + "-" + moveIn9971CTNby2.getUsedUOM());

        StockMoveOut moveOut1133UNITby1 = (StockMoveOut) new StockMoveOut().setProductId(productMeta1133.getId()).setDate(date)
        .setQuantity(1).setUsedUOM("UNIT").setSalesChannel("MERCHANT");
            
        
        orderO1212MERCHANTz2 = new Order().setOrderId("O1212").setChannel("MERCHANT").setDate(date);
        orderO1212MERCHANTz2.pushMovement(moveOut9971UNITby5);
        moveOut9971UNITby5.setOrderId(orderO1212MERCHANTz2.getOrderId());
        orderO1212MERCHANTz2.pushMovement(moveOut1121UNITby200);
        moveOut1121UNITby200.setOrderId(orderO1212MERCHANTz2.getOrderId());
        
        orderO2211MERCHANTz1 = new Order().setOrderId("O2211").setChannel("MERCHANT").setDate(date);
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
        productService.addNewProduct(productMeta1133);
    }

    @Test 
    public void insertOrderAndDocsToRepository(){
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
            log.info("moveOuts adding with : " + id);
            moveOuts.add(id);
        }
        for(StockMoveOut moveOut : orderO2211MERCHANTz1.getMovements()) {
            String id = moveOut.getOrderId() + "-" + moveOut.getRelativeId() + "-" + moveOut.getQuantity() + "-" + moveOut.getUsedUOM();
            log.info("moveOuts adding with : " + id);
            moveOuts.add(id);
        }
        for(Order order : orderRecord){
            for(StockMoveOut moveOut : order.getMovements()){
                String id = moveOut.getOrderId() + "-" + moveOut.getRelativeId() + "-" + moveOut.getQuantity() + "-" + moveOut.getUsedUOM();
                // log.debug("iterating to " + id);
                assertTrue(moveOuts.contains(id), "it does contained from repository " + id);
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
        stockingService.pushMovement(orderO2211MERCHANTz1);
        stockingService.updateToRepository();

        List<String> orderIds = new LinkedList<String>();
        orderIds.add(orderO2211MERCHANTz1.getOrderId());
        List<Order> orderRecord = stockingService.getOrderRecord(orderIds);
        assertTrue(orderRecord.get(0).getSize().equals(orderO2211MERCHANTz1.getSize()),
            "updateToRepository method has not insert a same order's movement record again to repository.");
    }

    @Test
    public void updateModifiedOrderAndDocsToRepository(){

        Date date = new Date(System.currentTimeMillis());
        
        //a duplicate order but new moveOut ensure inserted
        StockMoveOut moveOut9971CTNby1 = (StockMoveOut) new StockMoveOut().setProductId(productMeta9971.getId())
            .setQuantity(1).setUsedUOM("CTN").setDate(date).setSalesChannel("MERCHANT");
        moveOut9971CTNby1.setRelativeId(moveOut9971CTNby1.getProductId() + "-" + moveOut9971CTNby1.getUsedUOM());
    
        orderO1212MERCHANTz2.pushMovement(moveOut9971CTNby1);
        moveOut9971CTNby1.setOrderId(orderO1212MERCHANTz2.getOrderId());
        stockingService.pushMovement(orderO1212MERCHANTz2);
        stockingService.updateToRepository();

        ArrayList<String> orderIds = new ArrayList<String>();
        orderIds.add(orderO1212MERCHANTz2.getOrderId());
        List<Order> orderRecord = stockingService.getOrderRecord(orderIds);
        boolean exists = false;
        for(StockMoveOut moveOut : orderRecord.get(0).getMovements()){
            if(exists == false) exists = moveOut.getRelativeId().equals(moveOut9971CTNby1.getRelativeId());
        }
        assertTrue(exists, "a order inserted before to repository, but inserting again with a new moveOut updated is success");


        //a duplicate order but got moveOut has been removed ensure removed
        orderO1212MERCHANTz2.removeMovementByRelativeId(moveOut9971CTNby1.getRelativeId());
        stockingService.pushMovement(orderO1212MERCHANTz2);
        stockingService.updateToRepository();

        orderIds.clear();
        orderIds.add(orderO1212MERCHANTz2.getOrderId());
        orderRecord = stockingService.getOrderRecord(orderIds);
        boolean notExist = false;
        for(StockMoveOut moveOut : orderRecord.get(0).getMovements()){
            if(exists == false) exists = moveOut.getRelativeId().equals(moveOut9971CTNby1.getRelativeId());
        }
        assertFalse(notExist, "a order has removed moveOut then updating repository also removed.");
    }

    @Test
    public void gettingMoveOutAndMoveInWithDateCondition(){

        String id = productMeta9971.getId();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(orderO1133MERCHANTz1.getDate());

        calendar.add(Calendar.DATE, -5);
        Date startDate = calendar.getTime();
 
        calendar.add(Calendar.DATE, 10);
        Date endDate = calendar.getTime();

        LinkedList<String> moveIdList = new LinkedList<String>();
        moveIdList.add(id);
        List<ProductMovement> allMoveByProductId = stockMovementService.allMoveByProductId(moveIdList, startDate, endDate);

        String productId = allMoveByProductId.get(0).getProductId();
        assertEquals(id, productId);
    }

    @Test
    public void receiveMoveOutOnly(){
        String id = productMeta9971.getId();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(orderO1133MERCHANTz1.getDate());

        calendar.add(Calendar.DATE, -5);
        Date startDate = calendar.getTime();
 
        calendar.add(Calendar.DATE, 10);
        Date endDate = calendar.getTime();

        LinkedList<String> moveIdList = new LinkedList<String>();
        moveIdList.add(id);
        List<ProductMovement> allMoveOutByProductIds = stockMovementService.allMoveOutByProductIds(moveIdList, startDate, endDate);

        Iterator<ProductMovement> iterator = allMoveOutByProductIds.iterator();
        boolean isAllMoveOut = true;
        while(iterator.hasNext()){
            ProductMovement next = iterator.next();
            if(next instanceof StockMoveOut){
                StockMoveOut moveOut = (StockMoveOut) next;
                if(moveOut.getOrderId() == null) isAllMoveOut = false;
            } else {
                isAllMoveOut = false;
            }
        }
        assertTrue(isAllMoveOut);
    }

    @Test
    public void removeAllMoveOutByOrderAndMoveInByDocsShouldEnsureAllRemoved(){
        // a list of order removing from test repository inserted by above.
        LinkedList<Order> toDeleteOrder = new LinkedList<Order>();
        toDeleteOrder.add(orderO1133MERCHANTz1);
        toDeleteOrder.add(orderO1212MERCHANTz2);
        toDeleteOrder.add(orderO2211MERCHANTz1);
        
        stockingService.removeMoveOuts(toDeleteOrder);
        
        // a list of stockInDocs removing from test repository inserted by above.
        LinkedList<StockInDocs> toDeleteDocs = new LinkedList<StockInDocs>();
        toDeleteDocs.add(docsI1112z3);
        
        stockingService.removeMoveIns(toDeleteDocs);
    }

    @Test
    public void removeAllProductMetaMeasureStocking(){
        productService.delete(productMeta1121);
        productService.delete(productMeta1133);
        productService.delete(productMeta9971);
    }
}
