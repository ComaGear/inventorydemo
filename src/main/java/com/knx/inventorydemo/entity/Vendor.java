package com.knx.inventorydemo.entity;

public class Vendor {
    
    private String name;
    private String contactNumber;
    private int deliveryInvenval;

    public Vendor(String name, String contactNumber, int deliveryInvenval) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.deliveryInvenval = deliveryInvenval;
    }

    public String getName() {
        return name;
    }
    public Vendor setName(String name) {
        this.name = name;
        return this;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public Vendor setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        return this;
    }
    public int getDeliveryInvenval() {
        return deliveryInvenval;
    }
    public Vendor setDeliveryInvenval(int deliveryInvenval) {
        this.deliveryInvenval = deliveryInvenval;
        return this;
    }

    
}
