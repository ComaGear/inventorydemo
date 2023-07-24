package com.knx.inventorydemo.exception;

import java.util.List;

public class MeasurementValidationException extends RuntimeException {

    private List<String> setUnexistMeasurements;

    public void setSetUnexistMeasurements(List<String> setUnexistMeasurements) {
        this.setUnexistMeasurements = setUnexistMeasurements;
    }

    public List<String> getUnexistProductIds() {
        return setUnexistMeasurements;
    }
    
}
