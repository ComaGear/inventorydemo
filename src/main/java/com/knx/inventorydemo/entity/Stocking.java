package com.knx.inventorydemo.entity;

public class Stocking{
    private String productId;
    private double quantity;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(double oQuanity){
        this.quantity += oQuanity;
    }

    public Stocking(String productId, double quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
