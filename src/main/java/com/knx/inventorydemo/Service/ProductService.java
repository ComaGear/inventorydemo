package com.knx.inventorydemo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.exception.UnkrownLayerException;
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
     * 
     * @param id
     * @return product meta find in database.
     */
    public ProductMeta getProductMetaById(String id) {
        return null;
        // TODO: getting one product meta by id
    }

    public List<ProductMeta> findAllProductMetaBySimilarlyStrList(List<String> strings){

        //combine strings to a string with arround %s%
        StringBuilder stringBuilder = new StringBuilder();
        strings.forEach(str -> {
            stringBuilder.append("%");
            stringBuilder.append(str);
            stringBuilder.append("%");
        });

        return productMetaMapper.getProductByStr(stringBuilder.toString());
    }

    /**
     * initialized database
     */
    public void init() {
        productMetaMapper.init();
    }

}
