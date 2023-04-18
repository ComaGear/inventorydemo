package com.knx.inventorydemo.exception;

import java.util.LinkedList;
import java.util.List;

public class ProductUnactivityException extends RuntimeException{
    
    private List<String> unactivityProductList; 

    /**
     * @return a list of unactivity product. identify which product need update to active.
     */
    public List<String> getUnactivityProductList() {
        return unactivityProductList;
    }

    public void setUnactivityProductList(List<String> unactivityProductList) {
        this.unactivityProductList = unactivityProductList;
    }

    public ProductUnactivityException(String message){
        super(message);
        this.unactivityProductList = new LinkedList<String>();
    }

    public ProductUnactivityException(){
        super();
    }


}
