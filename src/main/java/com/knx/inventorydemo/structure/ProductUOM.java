package com.knx.inventorydemo.structure;

public class ProductUOM extends ProductMeasurement {

    @Override
    public String getLayer() {
        return "MERCHANT";  
    }
}
