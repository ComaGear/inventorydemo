package com.knx.inventorydemo.web.RestController.entity;

import java.util.List;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;


public class ProductMetaMeasurementsDTO extends ProductMeta{

    private List<ProductMeasurement> measurements;
    
    public List<ProductMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<ProductMeasurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString(){
        return new StringBuilder()
            .append("[")
            .append("Product ID : " + getId() + ", ")
            .append("Product NAME : " + getName() + ", ")
            .append("Product DEFAULT : " + getDefaultUom() + ", ")
            .append("Measurement Product ID : " + measurements.get(0).getProductId() + ", ")
            .append("Measurement measure Size : " + measurements.get(0).getMeasurement() + ", ")
            .append("Measurement Relative ID : " + measurements.get(0).getRelativeId() + ", ")
            .append("Measurement UOM Name: " + measurements.get(0).getUOM() + ", ")
            .append("Measurement Barcode : " + measurements.get(0).getAnotherBarcode() + ", ")
            .append("Measurement Update Rule : " + measurements.get(0).getUpdateRule() + ", ")
            .toString();
    }

    public ProductMetaMeasurementsDTO(){
        super();
    }

    public ProductMetaMeasurementsDTO(ProductMeta meta) {
        this.setId(meta.getId());
        this.setName(meta.getName());
        this.setActivity(meta.isActivity());
        this.setVendor(meta.getVendor());
        this.setDefaultUom(meta.getDefaultUom());
    }
}
