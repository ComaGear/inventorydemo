package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
public class StockingBehaveTest {

    private static final String UNIT = "UNIT";
    private static final String ORIGIN = "merchant";
    @Autowired
    StockingService stockingService;
    @Autowired
    ProductService productService;

    ProductMeta product;

    @BeforeAll
    public void setupProductEntity(){
        product = new ProductMeta()
            .setId("9667")
            .setName("Nabati Chocolate 40g x 10pcs")
            .setDefaultUom(UNIT);
        productService.addNewProduct(product);

        ProductMeasurement productMeasOriginChannel = new ProductMeasurement();
            productMeasOriginChannel.setProductId(product.getId())
                .setRelativeId("9667-CTN")
                .setUOM_name("CTN")
                .setSalesChannel(ORIGIN);

    }

    @Test
    public void checkQueuePuttedListOfMovementWhenClean(){
        // first putting to clean queue

        Order order = new Order()
            .setOrderId("20230105WEW")
            .setChannel(ORIGIN);

        order.pushMovement(new StockMoveOut()
                .setRelativeId(order.getOrderId())
                .setProductId(product.getId())
                .setDate(Date.valueOf(LocalDate.now()))
                .setQuantity(2)
                .setUsedUOM(UNIT)
                .setSalesChannel(order.getChannel()))
            .pushMovement(new StockMoveOut()
                .setRelativeId(order.getOrderId())
                .setProductId(product.getId())
                .setDate(Date.valueOf(LocalDate.now()))
                .setQuantity(5)
                .setUsedUOM(UNIT)
                .setSalesChannel(order.getChannel()));
        
        stockingService.pushMovement(order);

        List<String> list = new LinkedList<>();
        list.add(order.getOrderId());
        List<ProductMovement> returnList = stockingService.fetchMovesQueueMovementByRelativeId(list);

        boolean equals = order.getMovements().size() == returnList.size();
        assertTrue(equals);
    }

    @Test 
    public void checkQueuePuttedListOfMovementWhenContent(){

    }

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
