package com.knx.inventorydemo.mapper;

import java.util.List;

import com.knx.inventorydemo.structure.ProductMeasurement;

public interface ProductMeasurementMapper {

    public int addMeasureTo(String layer, ProductMeasurement measure);

    public List<ProductMeasurement> getProductMeasByProductId(String id);
    
    public void measInit(String layer);

    public void updateRuleInit(String layer);
}
