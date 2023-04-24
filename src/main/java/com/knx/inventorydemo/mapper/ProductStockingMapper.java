package com.knx.inventorydemo.mapper;

import java.util.List;
import java.util.Map;

public interface ProductStockingMapper {

    public Map<String, Double> bulkGetStockAvailableByProductId(List<String> productIds);

    public int updateStockingOnHold(Map<String, Double> moves);

    public int updateStockingFromHoldToStock(List<String> productIds);

    public void init();
}
