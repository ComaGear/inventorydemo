package com.knx.inventorydemo.entity;

public class StockMoveOut extends ProductMovement {
    private String salesChannel;
    private String OrderId;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public StockMoveOut setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
        return this;
    }
}
