package com.knx.inventorydemo.entity;

import java.util.List;

public class Order {
    
    private String orderId;
    private List<ProductMovement> productMovements;
    private String channel;
    private String status;

    private String[] array = {"unsigned, pending, completed, cancelled, return"};

    public List<ProductMovement> getMovements(){
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
