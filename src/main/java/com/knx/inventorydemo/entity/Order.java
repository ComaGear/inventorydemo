package com.knx.inventorydemo.entity;

import java.util.Date;
import java.util.Set;

public class Order {
    
    private String orderId;
    private Set<ProductMovement> productMovements;
    private String channel;
    private String status;
    private Date date;
    
    // private String[] array = {"unsigned, pending, completed, cancelled, return"};

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public Set<ProductMovement> getMovements(){
        return productMovements;
    }

    public boolean hasMovement(){
        return !productMovements.isEmpty();
    }

    public Order setAnalysed(boolean b){
        this.status = "Analysed";
        return this;
    }
}
