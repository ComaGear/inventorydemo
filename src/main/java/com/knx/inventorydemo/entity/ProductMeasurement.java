package com.knx.inventorydemo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductMeasurement {

    public static final String DEFAULT_UOM = "unit";
    
    @JsonProperty("product_id")
    private String productId;
    private float measurement;
    @JsonProperty("relative_id")
    private String relativeId;
    @JsonProperty("barcode")
    private String anotherBarcode;
    private String UOM;
    private String updateRule;
    private String salesChannel;

    // public boolean check() {
    //     if (UOM_name == null)
    //         throw new NullPointerException("ProductMeta's UOM_name is null");
    //     if (measurement <= 0)
    //         throw new NullPointerException("ProductMeta's measurement is null");
    //     return true;
    // }

    public String getSalesChannel() {
        return salesChannel;
    }

    public ProductMeasurement setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
        return this;
    }

    public String getAnotherBarcode() {
        return anotherBarcode;
    }

    public ProductMeasurement setAnotherBarcode(String anotherBarcode) {
        this.anotherBarcode = anotherBarcode;
        return this;
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

    public String getUOM() {
        return UOM;
    }

    public ProductMeasurement setUOM(String UOM_name) {
        this.UOM = UOM_name;
        return this;
    }

    public static boolean valid(ProductMeasurement productMeasurement){
        return true;
        //TODO 
        
    }

}
