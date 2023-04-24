package com.knx.inventorydemo.mapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface ProductStockingMapper {

    public Map<String, Double> bulkGetStockAvailableByProductId(List<String> productIds);

    public int updateStockingOnHold(Map<String, Double> stockings);

    public int updateStockingFromHoldToStock(List<String> productIds);

    public void init();

    public void createStockingByProductIds(LinkedList<String> linkedList);

    public void deleteByProductIds(LinkedList<String> linkedList);
}
