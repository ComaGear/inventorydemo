package com.knx.inventorydemo.Service;

import org.springframework.beans.factory.annotation.Autowired;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.exception.UnkrownLayerException;
import com.knx.inventorydemo.mapper.ProductMeasurementMapper;

public class MeasurementService {

    @Autowired
    private ProductMeasurementMapper productMeasurementMapper;

    public static boolean checkLayerExists(String layer) {
        return false;
    }

    public void findAllCustomMeasurementByProductId(String id) {
    }

    /**
     * add a new product's measurment to specify layer of sales channel.
     * @param product should contains at least product's id.
     */
    public void addNewMeasurementToProduct(ProductMeta product, ProductMeasurement measurement) {

        if(product.getId() == null || product.getId() == "") throw new NullPointerException("product meta id is null");
        if(measurement.getMeasurement() <= 0) throw new IllegalArgumentException("Measurement's measure value should not less than zero");
        if(measurement.getLayer() != ProductUOM.LAYER) {
            if(measurement.getRelativeId() == null || measurement.getRelativeId() == "") throw new NullPointerException("measurement's relative id is null");
        }

        // relativing 
        if(measurement.getProductId() == null || measurement.getProductId() == "") {
            measurement.setProductId(product.getId());
        }
        if(!MeasurementService.checkLayerExists(measurement.getLayer())) throw new UnkrownLayerException(measurement.getLayer() + " is not exists");
        
        productMeasurementMapper.addMeasureTo(measurement.getLayer(), measurement);
    }

    public static void init() {
        
    }
    
}
