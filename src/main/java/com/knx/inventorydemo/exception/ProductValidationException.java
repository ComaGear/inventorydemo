package com.knx.inventorydemo.exception;

import java.util.List;

public class ProductValidationException extends RuntimeException {

    private List<String> unExistProductIds;
    
    public void setUnexistProductIds(List<String> unExistProductIds){
        this.unExistProductIds = unExistProductIds;
    }

    public List<String>  getUnexistProductIds() {
        return unExistProductIds;
    }
    
}
