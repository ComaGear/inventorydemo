package com.knx.inventorydemo.entity;

import java.util.Date;

public class ProductMovement {

    private String relativeId;
    private String productId;
    private String usedUOM;
    private Date date;
    private double quantity;
    private String salesChannel;

    // not in database
    private boolean originMeas;

    public String getSalesChannel() {
        return salesChannel;
    }

    public ProductMovement setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public ProductMovement setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getRelativeId() {
        if(relativeId == null) return productId + "-" + usedUOM;
        return relativeId;
    }

    public ProductMovement setRelativeId(String relativeId) {
        this.relativeId = relativeId;
        return this;
    }

    public boolean isOriginMeas() {
        return originMeas;
    }

    public ProductMovement setOriginMeas(boolean originMeas) {
        this.originMeas = originMeas;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public ProductMovement setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public double getQuantity() {
        return quantity;
    }

    public ProductMovement setQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getUsedUOM() {
        return usedUOM;
    }

    public ProductMovement setUsedUOM(String usedUOM) {
        this.usedUOM = usedUOM;
        return this;
    }

    // @Override
    // public int compareTo(ProductMovement o) {

    //     if(this.relativeId == null || relativeId.isEmpty()) return -1;

    //     if(this.relativeId.equals(o.getRelativeId())) {
    //         if(this.productId.equals(o.getProductId())) return 0;
    //         return this.productId.compareTo(o.getProductId());
    //     }

    //     return relativeId.compareTo(o.getRelativeId());
    // }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append("Product ID: " + getProductId() + ", ");
        stringBuilder.append("Relative ID: " + getRelativeId() + ", ");
        stringBuilder.append("Quantity: " + getQuantity() + ", ");
        stringBuilder.append("UOM: " + getUsedUOM() + ", ");
        stringBuilder.append("Date: "+ getDate());
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    

}
