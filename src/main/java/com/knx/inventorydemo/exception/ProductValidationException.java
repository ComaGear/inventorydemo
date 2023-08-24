package com.knx.inventorydemo.exception;

import com.knx.inventorydemo.entity.ProductMeta;

public class ProductValidationException extends IllegalArgumentException {

    private String message;
    private ProductMeta productMeta;

    public String getMessage() {
        return message;
    }

    public ProductMeta getProductMeta() {
        return productMeta;
    }

    public ProductValidationException(String message, ProductMeta productMeta){

        super(message);
        this.message = message;
        this.productMeta = productMeta;
    }
}
