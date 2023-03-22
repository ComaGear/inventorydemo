package com.knx.inventorydemo.entity;

import java.util.Date;

public class ProductMovement {

    private String relativeId;
    private String productId;
    private String usedUOM;
    private Date date;
    private double quantity;

    // not in database
    private boolean originMeas;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(String relativeId) {
        this.relativeId = relativeId;
    }

    public boolean isOriginMeas() {
        return originMeas;
    }

    public void setOriginMeas(boolean originMeas) {
        this.originMeas = originMeas;
    }

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

    public String getUsedUOM() {
        return usedUOM;
    }

    public void setUsedUOM(String usedUOM) {
        this.usedUOM = usedUOM;
    }

    
    public class relativeAndProductId{
        private String relativeId;
        private String ProductId;

        public String getRelativeId() {
            return relativeId;
        }
        public void setRelativeId(String relativeId) {
            this.relativeId = relativeId;
        }
        
        public String getProductId() {
            return ProductId;
        }
        public void setProductId(String productId) {
            ProductId = productId;
        }
    }

}
