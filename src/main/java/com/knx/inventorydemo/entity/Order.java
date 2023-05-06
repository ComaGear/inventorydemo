package com.knx.inventorydemo.entity;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    
    private String orderId;
    private ArrayList<StockMoveOut> productMovements;
    private String channel;
    private String status;
    private Date date;
    
    // private String[] array = {"unsigned, pending, completed, cancelled, return"};

    public Integer getSize() {
        return this.productMovements.size();
    }

    public String getChannel() {
        return channel;
    }

    public Order setChannel(String channel) {
        this.channel = channel;
        return this;
    }
    
    public String getOrderId() {
        return orderId;
    }

    public Order setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Order setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Order setDate(Date date) {
        this.date = date;
        return this;
    }


    public ArrayList<StockMoveOut> getMovements(){
        return productMovements;
    }

    public Order pushMovement(StockMoveOut moves){
        if(moves == null) throw new NullPointerException("moves is null");
        if(moves.getProductId().isEmpty() || moves.getProductId().equals("")
            || moves.getQuantity() == 0) throw new IllegalArgumentException("moves's product id is emptry");

        if(this.productMovements == null) productMovements = new ArrayList<>();

        this.productMovements.add(moves);
        return this;
    }

    public boolean hasMovement(){
        return !productMovements.isEmpty();
    }

    public Order setAnalysed(boolean b){
        this.status = "Analysed";
        return this;
    }
}
