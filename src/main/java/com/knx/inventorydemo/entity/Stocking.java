package com.knx.inventorydemo.entity;

public class Stocking{
    private String productId;
    private double quantity;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(double oQuanity){
        this.quantity += oQuanity;
    }

    public Stocking(String productId, double quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Qauntity of sell out and trade in by StockMoveIn and StockMoveOut was not suitable for calculate directly by Stock stocking behave.
     * before update the stock, use this static method turn to stocking behave.
     * 
     * @param {@code}moves instance of ProductMovement refered
     * @return a Double of quantity updating to stock.
     */
    public static double prepareStocking(ProductMovement moves){
        if(moves instanceof StockMoveOut){
            StockMoveOut moveOut = (StockMoveOut) moves;
            return 0 - moveOut.getQuantity();
        }
        if(moves instanceof StockMoveIn){
            StockMoveIn moveIn = (StockMoveIn) moves;
            return moveIn.getQuantity();
        }
        return moves.getQuantity();
    }
}
