package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.ProductService;

@SpringBootTest
public class sqlTempTableDevTest {

    @Autowired
    ProductService productService;

    Logger log = LoggerFactory.getLogger(getClass());
    
    @Test
    public void lookupUnExistProduct(){

        // LocalTime startTime = LocalTime.now();

        String id = "9999";

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(id);
        List<String> returnIds = productService.lookupUnexistProduct(arrayList);

        // LocalTime endTime = LocalTime.now();
        // log.info("spend time was " + endTime.minusSeconds(startTime.getSecond()).getSecond());
        assertEquals(id, returnIds.get(0));
        assertEquals(returnIds.size(), 1);
    }
}
