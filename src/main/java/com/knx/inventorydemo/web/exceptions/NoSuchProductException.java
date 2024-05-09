package com.knx.inventorydemo.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "no such product")
public class NoSuchProductException extends RuntimeException {
    
    private String id;
    private String message;
    public static String REASON = "no such product";
    public static String ERROR_ID = "NO_SUCH_PRODUCT";

    public NoSuchProductException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getId() {
        return id;
    }

    public NoSuchProductException(String id){
        this.id = id;
    }

    @Override
    public String getMessage() {
        if(message != null || !message.isEmpty()) return this.message;
        return "getting product id " + id + " is not existed.";
    }
}
