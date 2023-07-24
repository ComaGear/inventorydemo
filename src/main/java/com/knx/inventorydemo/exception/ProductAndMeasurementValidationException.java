package com.knx.inventorydemo.exception;

import java.util.List;

public class ProductAndMeasurementValidationException extends RuntimeException {

    private List<String> unexistProductIds;
    private List<String> unexistMeasurements;

    public List<String> getSetUnexistMeasurements() {
        return unexistMeasurements;
    }
    
    public List<String> getSetUnexistProductIds() {
        return unexistProductIds;
    }

    public void setUnexistProductIds(List<String> unexistProductIds) {
        this.unexistProductIds = unexistProductIds;
    }

    public void setUnexistMeasurements(List<String> unexistMeasurements) {
        this.unexistMeasurements = unexistMeasurements;
    }

    public boolean hasError() {
        return unexistProductIds != null || unexistMeasurements != null;
    }
}
