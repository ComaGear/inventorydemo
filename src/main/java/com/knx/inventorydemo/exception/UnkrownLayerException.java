package com.knx.inventorydemo.exception;


public class UnkrownLayerException extends RuntimeException{
    public UnkrownLayerException(String message){
        super(message);
    }
}
