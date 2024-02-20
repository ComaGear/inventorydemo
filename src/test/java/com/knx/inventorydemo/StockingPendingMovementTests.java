package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.Service.StockingService;
import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockMoveOut;

@SpringBootTest
public class StockingPendingMovementTests {


    static Logger log = LoggerFactory.getLogger(StockingPendingMovementTests.class);
    private static final String UNIT = "UNIT";
    private static final String ORIGIN = "merchant";
    @Autowired
    StockingService stockingService;
    @Autowired
    ProductService productService;

    ProductMeta product;
    private Order multiMovesOrder;
    private Order singleMovesOrder;

    @BeforeEach
    public void setupProductEntity(){
        product = new ProductMeta()
            .setId("9667")
            .setName("Nabati Chocolate 40g x 10pcs")
            .setDefaultUom(UNIT).setActivity(true);
        productService.addNewProduct(product);

        ProductMeasurement productMeasOriginChannel = new ProductMeasurement();
            productMeasOriginChannel.setProductId(product.getId())
                .setRelativeId("9667-CTN")
                .setUOM("CTN")
                .setSalesChannel(ORIGIN);

        multiMovesOrder = new Order()
                .setOrderId("20230105WEW")
                .setChannel(ORIGIN);
    
        multiMovesOrder.pushMovement((StockMoveOut) new StockMoveOut()
                .setOrderId(multiMovesOrder.getOrderId())
                .setProductId(product.getId())
                .setDate(Date.valueOf(LocalDate.now()))
                .setQuantity(2)
                .setUsedUOM(UNIT)
                .setSalesChannel(multiMovesOrder.getChannel()))
            .pushMovement((StockMoveOut) new StockMoveOut()
                .setOrderId(multiMovesOrder.getOrderId())
                .setProductId(product.getId())
                .setDate(Date.valueOf(LocalDate.now()))
                .setQuantity(5)
                .setUsedUOM(UNIT)
                .setSalesChannel(multiMovesOrder.getChannel()));

        singleMovesOrder = new Order()
            .setOrderId("220230505UE")
            .setChannel(ORIGIN);
        
        singleMovesOrder.pushMovement((StockMoveOut) new StockMoveOut()
                .setOrderId(singleMovesOrder.getOrderId())
                .setProductId(product.getId())
                .setDate(Date.valueOf(LocalDate.now()))
                .setQuantity(2)
                .setUsedUOM(UNIT)
                .setSalesChannel(singleMovesOrder.getChannel()));

    }

    @Test
    public void checkQueuePuttedListOfMovementWhenClean(){
        // first putting to clean queue
        stockingService.clearPushedMovements();
        
        stockingService.pushMovement(multiMovesOrder);

        List<String> list = new LinkedList<>();
        list.add(multiMovesOrder.getOrderId());
        List<ProductMovement> returnList = stockingService.fetchMovesQueueMovementByOrderDocsIds(list);
        
        boolean equals = multiMovesOrder.getMovements().size() == returnList.size();
        log.info("order's movements size is " + multiMovesOrder.getMovements().size());
        log.info("returnList size is " + returnList.size());

        assertTrue(equals);
    }

    @Test
    public void singleMoveButFetchMultiOrderId(){
        stockingService.clearPushedMovements();

        List<String> relativeIds = new LinkedList<String>();

        //add a not exist order string
        relativeIds.add("220230505EE");
        relativeIds.add("2202305052E");

        stockingService.pushMovement(singleMovesOrder);
        relativeIds.add(singleMovesOrder.getOrderId());

        List<ProductMovement> returnList = stockingService.fetchMovesQueueMovementByOrderDocsIds(relativeIds);

        assertEquals(returnList.size(), singleMovesOrder.getMovements().size());
    }

    @Test
    public void multiOrderPushedGetSameSize(){
        stockingService.clearPushedMovements();

        List<String> relativeIds = new LinkedList<String>();

        stockingService.pushMovement(singleMovesOrder);
        stockingService.pushMovement(multiMovesOrder);
        relativeIds.add(singleMovesOrder.getOrderId());
        relativeIds.add(multiMovesOrder.getOrderId());

        List<ProductMovement> returnList = stockingService.fetchMovesQueueMovementByOrderDocsIds(relativeIds);

        int size = 0;
        size = singleMovesOrder.getMovements().size() + size;
        size = multiMovesOrder.getMovements().size() + size;

        assertEquals(size, returnList.size());
    }

    @Test 
    public void checkQueuePuttedListOfMovementWhenContent(){

        stockingService.clearPushedMovements();

        List<String> relativeIds = new LinkedList<String>();

        stockingService.pushMovement(singleMovesOrder);
        stockingService.pushMovement(multiMovesOrder);
        relativeIds.add(singleMovesOrder.getOrderId());

        List<ProductMovement> returnList = stockingService.fetchMovesQueueMovementByOrderDocsIds(relativeIds);

        assertEquals(singleMovesOrder.getMovements().size(), returnList.size());

    }


    // TODO: test for bulkGetRecordSizeOfOrderByOrderId mapper method

    public void putOrdersAndForceCalculateUpdateDatabase(){
    }

    public void getSuccessDeleteSpecifyOrderInMovementQueue(){

    }

    public void deleteDatabaseRecordOfMovementSpecifyByOrder(){

    }

    @Test
    public void rollBackWhenforceCalculateInsertDatabaseError(){

    }

    @Test
    public void calculateToOriginStateMeasurement(){

    }
}
