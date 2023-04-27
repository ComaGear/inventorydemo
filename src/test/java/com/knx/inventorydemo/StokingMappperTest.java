package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.entity.ProductStocking;
import com.knx.inventorydemo.entity.Stocking;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

@SpringBootTest
public class StokingMappperTest {

    private static final String PRODUCT_STRING = "12345";
    @Autowired
    private ProductStockingMapper stockingMapper;
    private LinkedList<String> linkedList = new LinkedList<String>();
    
    @BeforeEach
    public void prepareEntity(){
        linkedList.add(PRODUCT_STRING);
        stockingMapper.createStockingByProductIds(linkedList);
    }

    @Test
    public void stockingRepositoryGivenHashMapAsParameterShouldSuccess(){
        List<Stocking> list = new LinkedList<Stocking>();
        Stocking stocking = new Stocking(PRODUCT_STRING, 1d);
        list.add(stocking);
        
        stockingMapper.updateStockingOnHold(list);
    }

    @Test
    public void stockingOnHoldNotIsZero(){
        List<String> list = new LinkedList<String>();
        list.add(PRODUCT_STRING);
        List<ProductStocking> stockings = stockingMapper.bulkGetStockingByProductIds(list);

        if(stockings.get(0).getProductId().equals(PRODUCT_STRING))
            assertNotEquals(stockings.get(0).getStockOnHold(), null);
    }

    @AfterEach
    public void afterEach(){
        stockingMapper.deleteByProductIds(linkedList);
    }
}
