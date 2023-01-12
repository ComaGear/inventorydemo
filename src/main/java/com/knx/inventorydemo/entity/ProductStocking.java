package com.knx.inventorydemo.entity;

/**
 *  
 **/
public class ProductStocking {

    private String productId;
    private double min_quantity;
    private double stockAvailable;
    private double stockOnHold;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getStockOnHold() {
        return stockOnHold;
    }

    public void setStockOnHold(double stockOnHold) {
        this.stockOnHold = stockOnHold;
    }

    public double getMin_quantity() {
        return min_quantity;
    }

    public void setMin_quantity(double min_quantity) {
        this.min_quantity = min_quantity;
    }

    public double getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(double stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

}
