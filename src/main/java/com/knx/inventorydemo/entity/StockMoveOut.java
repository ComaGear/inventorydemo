package com.knx.inventorydemo.entity;

public class StockMoveOut extends ProductMovement {
    private String salesChannel;

    public String getSalesChannel() {
        return salesChannel;
    }

    public StockMoveOut setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
        return this;
    }
}
