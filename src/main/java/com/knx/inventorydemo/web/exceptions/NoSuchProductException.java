package com.knx.inventorydemo.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "no such product")
public class NoSuchProductException extends RuntimeException {
    
    private String id;

    public String getId() {
        return id;
    }

    public NoSuchProductException(String id){
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "getting product id " + id + " is not existed.";
    }
}
