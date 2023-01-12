package com.knx.inventorydemo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.mapper.ProductMeasurementMapper;
import com.knx.inventorydemo.mapper.ProductMetaMapper;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

public class ProductService {
    
    @Autowired
    ProductMetaMapper productMetaMapper;
    
    @Autowired
    ProductMeasurementMapper productMeasurementMapper;

    @Autowired
    ProductStockingMapper productStockingMapper;


    /**
     * this method create added product meta with custom measurements.
     * @param productMeta
     * @param productMeasurement
     */
    public void addNewProduct(ProductMeta productMeta, ProductMeasurement productMeasurement){

        if(productMeta.getDefaultUom() == null) {
            productMeta.setDefaultUom(productMeasurement.getUOM_name());
        }
        if(productMeta.getId() == null || productMeta.getName() == null) throw new NullPointerException();

        // implement
        productMetaMapper.addNewProduct(productMeta);
        productMeasurementMapper.addMeasureTo(productMeasurement.getLayer(), productMeasurement);
        
    }

    /**
     * this method create added productMeta and provide default uom.
     */
    public void addNewProduct(ProductMeta productMeta){
        addNewProduct(productMeta, new ProductUOM()
            .setRelativeId("")
            .setMeasurement(1f)
            .setUOM_name("UNIT")
            .setProductId(productMeta.getId()
        ));
    }
       

    public List<ProductMeta> getAllProductMeta(){
        return productMetaMapper.getAll();
    }

    /**
     * add a new product's measurment to specify layer of sales channel.
     * 
     */
    public void addNewMeasurementToProduct(ProductMeta product, ProductMeasurement measurement) {
        // TODO: add new measurement inserting specify on layer.
    }

    /**
     * 
     * @param id
     * @return product meta find in database.
     */
    public ProductMeta getProductMetaById(String id) {
        return null;
        // TODO: getting one product meta by id
    }

    public void findAllCustomMeasurementByProductId(String id) {
    }

}
