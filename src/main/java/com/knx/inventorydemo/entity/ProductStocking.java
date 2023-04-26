package com.knx.inventorydemo.entity;

/**
 *  
 **/
public class ProductStocking {

    private String productId;
    private double stockAvailable;
    private double stockOnHold;
    private double stock;

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

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

    public double getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(double stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

}
