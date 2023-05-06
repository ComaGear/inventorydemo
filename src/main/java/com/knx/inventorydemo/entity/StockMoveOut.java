package com.knx.inventorydemo.entity;

public class StockMoveOut extends ProductMovement implements Comparable<StockMoveOut> {
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StockMoveOut){
            StockMoveOut oMoveOut = (StockMoveOut) obj;
            return this.getOrderId().equals(oMoveOut.getOrderId())
                && this.getRelativeId().equals(oMoveOut.getRelativeId())
                && this.getSalesChannel().equals(oMoveOut.getSalesChannel());
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(StockMoveOut oMoveOut) {

        if(this.getOrderId().equals(oMoveOut.getOrderId())
            && this.getRelativeId().equals(oMoveOut.getRelativeId())
            == true) return 0; 

        if(oMoveOut == null || oMoveOut.getOrderId().isEmpty()) return -1;
        if(this == null || this.getOrderId().isEmpty()) return 1;

        if(this.getOrderId().compareTo(oMoveOut.getOrderId()) == 0){

            String o1RelativeId = null;
            if(this.getRelativeId() == null || this.getRelativeId().isEmpty()) // || !this.getRelativeId().contains("-")
                o1RelativeId = this.getProductId() + "-" + this.getUsedUOM();
            else o1RelativeId = this.getRelativeId();

            String o2RelativeId = null;
            if(oMoveOut.getRelativeId() == null || oMoveOut.getRelativeId().isEmpty()) // || !oMoveOut.getRelativeId().contains("-")
                o2RelativeId = oMoveOut.getProductId() + "-" + oMoveOut.getUsedUOM();
            else o2RelativeId = oMoveOut.getRelativeId();

            return o1RelativeId.compareTo(o2RelativeId);
        }

        return this.getOrderId().compareTo(oMoveOut.getOrderId());
    }

    public void prepareStocking() {
        double i = 0 - this.getQuantity();
        this.setQuantity(i);
    }
}
