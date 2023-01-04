package com.knx.inventorydemo.structure;

public class ProductMeasurement {
    
    private String name;
    private Float measurement;
    private String ProductId;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Float measurement) {
        this.measurement = measurement;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

}
