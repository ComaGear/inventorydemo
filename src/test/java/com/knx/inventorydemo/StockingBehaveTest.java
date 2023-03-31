package com.knx.inventorydemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.StockingService;

@SpringBootTest
public class StockingBehaveTest {
    @Autowired
    StockingService stockingService;

    @Test
    public void checkQueuePuttedListOfMovementWhenClean(){
        // first putting to clean queue
    }

    @Test 
    public void checkQueuePuttedListOfMovementWhenContent(){

    }

    @Test
    public void rollBackWhenforceCalculateInsertDatabaseError(){

    }

    @Test
    public void calculateToOriginStateMeasurement(){

    }
}
