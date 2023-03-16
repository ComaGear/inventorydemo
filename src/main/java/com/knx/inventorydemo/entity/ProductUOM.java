package com.knx.inventorydemo.entity;

public class ProductUOM extends ProductMeasurement {

    public static final String LAYER = "merchant";

    @Override
    public String getSalesChannel() {
        return LAYER;
    }
}
