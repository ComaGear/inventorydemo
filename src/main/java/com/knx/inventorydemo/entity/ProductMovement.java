package com.knx.inventorydemo.entity;

public class ProductMovement {

    private String productId;
    private String usedUOM;
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

    public String getUsedUOM() {
        return usedUOM;
    }

    public void setUsedUOM(String usedUOM) {
        this.usedUOM = usedUOM;
    }

}
