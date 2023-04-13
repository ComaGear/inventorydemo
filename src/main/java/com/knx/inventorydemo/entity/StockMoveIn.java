package com.knx.inventorydemo.entity;

public class StockMoveIn extends ProductMovement {
    private String vendorName;
    // private 

    public String getVendorName() {
        return vendorName;
    }

    public StockMoveIn setVendorName(String vendorName) {
        this.vendorName = vendorName;
        return this;
    }
}
