package com.knx.inventorydemo.entity;

public class ProductMeasurement {

    private String productId;
    private float measurement;
    private String relativeId;
    private String UOM_name;
    private String updateRule;
    private String layer;
   

    public boolean check() {
        if (UOM_name == null)
            throw new NullPointerException("ProductMeta's UOM_name is null");
        if (measurement <= 0)
            throw new NullPointerException("ProductMeta's measurement is null");
        return true;
    }

    public String getProductId() {
        return productId;
    }

    public String getUpdateRule() {
        return updateRule;
    }

    public void setUpdateRule(String updateRule) {
        this.updateRule = updateRule;
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
