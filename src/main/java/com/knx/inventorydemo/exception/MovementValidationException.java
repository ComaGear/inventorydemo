package com.knx.inventorydemo.exception;

import java.util.HashMap;
import java.util.List;

public class MovementValidationException extends RuntimeException {

    public static final String UNEXISTED_PRODUCT_ID = "unexistProductIds";
    public static final String UNACTIVE_PRODUCT_ID = "unActiveProductIds";
    public static final String UNEXISTED_MEASUREMENT = "unexistMeasurements";

    private HashMap<String, HashMap<String, List<String>>> nonValidMap;


    public HashMap<String, HashMap<String, List<String>>> getNonValidMap() {
        return nonValidMap;
    }

    public void setNonValidMap(HashMap<String, HashMap<String, List<String>>> nonValidMap) {
    }
}
