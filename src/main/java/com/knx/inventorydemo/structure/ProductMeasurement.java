package com.knx.inventorydemo.structure;

public class ProductMeasurement {
    
    public String productId;
    

    public float measurement;
    public String relativeId;
    public String UOM_name;
    public String updateRule;

    public boolean check(){
        if(UOM_name == null) throw new NullPointerException("ProductMeta's UOM_name is null");
        if(measurement <= 0) throw new NullPointerException("ProductMeta's measurement is null");
        return true;
    }

    public String getLayer(){
        return "";
    }

    public String getProductId() {
        return productId;
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

    public String getUOM_name() {
        return UOM_name;
    }

    public ProductMeasurement setUOM_name(String uOM_name) {
        UOM_name = uOM_name;
        return this;
    }

}
