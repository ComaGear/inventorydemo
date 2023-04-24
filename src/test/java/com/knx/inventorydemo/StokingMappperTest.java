package com.knx.inventorydemo;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import com.knx.inventorydemo.mapper.ProductStockingMapper;

public class StokingMappperTest {
    /**
     *
     */
    private static final String PRODUCT_STRING = "12345";
    @Autowired
    private ProductStockingMapper stockingMapper;
    private LinkedList<String> linkedList = new LinkedList<String>();
    
    @BeforeAll
    public void prepareEntity(){
        linkedList.add(PRODUCT_STRING);
        stockingMapper.createStockingByProductIds(linkedList);
    }

    public void stockingRepositoryGivenHashMapAsParameterShouldSuccess(){
        HashMap<String, Double> hashMap = new HashMap<String, Double>();
        hashMap.put(PRODUCT_STRING, 1d);
        
        stockingMapper.updateStockingOnHold(hashMap);
    }

    @AfterAll
    public void afterAll(){
        stockingMapper.deleteByProductIds(linkedList);
    }
}
