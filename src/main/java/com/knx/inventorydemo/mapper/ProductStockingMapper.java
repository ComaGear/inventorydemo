package com.knx.inventorydemo.mapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.knx.inventorydemo.entity.ProductStocking;
import com.knx.inventorydemo.entity.Stocking;

public interface ProductStockingMapper {

    // this a opinion is query stock_avaible by stocking minus stocking_on_hold. not store as one column in table.
    public Map<String, Double> bulkGetStockAvailableByProductId(List<String> productIds);

    public int updateStockingOnHold(List<Stocking> stockings);

    // not implement yet
    public int updateStockingFromHoldToStock(List<String> productIds);

    public void init();

    public void createStockingByProductIds(List<String> productIds);

    public void BulkGetStockingOnHoldByProductIds(List<String> productIds);

    public List<ProductStocking> bulkGetStockingByProductIds(List<String> productIds);

    public void deleteByProductIds(LinkedList<String> productIds);
}
