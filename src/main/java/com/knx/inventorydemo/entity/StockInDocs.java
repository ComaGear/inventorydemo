package com.knx.inventorydemo.entity;

import java.util.ArrayList;
import java.util.Date;

public class StockInDocs {
    private String docsId;
    private ArrayList<ProductMovement> movements;
    private Date date;

    public boolean hasMovement(){
        if(movements == null) return false;
        return !movements.isEmpty();
    }

    public int getSize(){
        return movements.size();
    }
    
    public String getDocsId() {
        return docsId;
    }
    public StockInDocs setDocsId(String docsId) {
        this.docsId = docsId;
        return this;
    }
    public ArrayList<ProductMovement> getMovements() {
        return movements;
    }
    public StockInDocs pushMoveIn(ProductMovement movement) {
        if(movement == null) throw new NullPointerException("movement is null");
        if(movement.getProductId() == null || movement.getProductId().isEmpty()) 
            throw new IllegalArgumentException("movement is not completed");

        if(movements == null) this.movements = new ArrayList<ProductMovement>();
        this.movements.add(movement);
        return this;
    }
    public Date getDate() {
        return date;
    }
    public StockInDocs setDate(Date date) {
        this.date = date;
        return this;
    }
}
