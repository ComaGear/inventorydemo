package com.knx.inventorydemo.entity;

public class ProductUOM extends ProductMeasurement {

    @Override
    public String getLayer() {
        return "MERCHANT";  
    }
}
