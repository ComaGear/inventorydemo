package com.knx.inventorydemo.entity;

public class ProductMeasurement {
    
    public String productId;
    public float measurement;
    public String relativeId;
    public String UOM_name;
    public String updateRule;
    public String layer;

    public boolean check(){
        if(UOM_name == null) throw new NullPointerException("ProductMeta's UOM_name is null");
        if(measurement <= 0) throw new NullPointerException("ProductMeta's measurement is null");
        return true;
    }

    public String getProductId() {
        return productId;
    }

    public ProductMeasurement setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getLayer() {
        return layer;
    }

    public ProductMeasurement setLayer(String layer) {
        this.layer = layer;
        return this;
    }

    public float getMeasurement() {
        return measurement;
    }

    public ProductMeasurement setMeasurement(float measurement) {
        this.measurement = measurement;
        return this;
    }

    public String getRelativeId() {
        return relativeId;
    }

    public ProductMeasurement setRelativeId(String relativeId) {
        this.relativeId = relativeId;
        return this;
    }

    public String getUOM_name() {
        return UOM_name;
    }

    public ProductMeasurement setUOM_name(String UOM_name) {
        this.UOM_name = UOM_name;
        return this;
    }

}
