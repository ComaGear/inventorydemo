package com.knx.inventorydemo.entity;

public class StockMoveOut extends ProductMovement {
    private String salesChannel;

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }
}
