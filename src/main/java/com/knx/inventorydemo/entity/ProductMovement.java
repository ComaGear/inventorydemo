package com.knx.inventorydemo.entity;

public class ProductMovement {

    private String relativeId;
    private String productId;
    private String usedUOM;
    private double quantity;
    private boolean originMeas;
    private boolean isStockIn;
    private boolean isSales;

    public String getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(String relativeId) {
        this.relativeId = relativeId;
    }


    public boolean isSales() {
        return isSales;
    }

    public void setSales(boolean isSales) {
        this.isSales = isSales;
    }

    public boolean isStockIn() {
        return isStockIn;
    }

    public void setStockIn(boolean isStockIn) {
        this.isStockIn = isStockIn;
    }

    public boolean isOriginMeas() {
        return originMeas;
    }

    public void setOriginMeas(boolean originMeas) {
        this.originMeas = originMeas;
    }

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
