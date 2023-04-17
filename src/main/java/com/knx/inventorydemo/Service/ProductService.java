package com.knx.inventorydemo.Service;

import java.util.HashMap;
import java.util.List;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.mapper.ProductMetaMapper;

public class ProductService {
    
    ProductMetaMapper productMetaMapper;

    MeasurementService measurementService;


    public void setMeasurementService(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    public ProductService(ProductMetaMapper productMetaMapper) {
        this.productMetaMapper = productMetaMapper;
    }

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
        ProductMeta checkingProductMeta = productMetaMapper.getProductById(productMeta.getId());
        if(checkingProductMeta != null && !checkingProductMeta.getName().isEmpty()) return;
        
        productMetaMapper.addNewProduct(productMeta);
        measurementService.addNewMeasurementToProduct(productMeta, productMeasurement);
        
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

    //TODO: delete product meta

    //TODO: update product meta
       

    public List<ProductMeta> getAllProductMeta(){
        return productMetaMapper.getAll();
    }
 
    /**
     * 
     * @param id
     * @return product meta find in database.
     */
    public ProductMeta getProductMetaById(String id) {
        if(id == null || id.equals("")) throw new NullPointerException();
        return productMetaMapper.getProductById(id);
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
        this.productMetaMapper.init();
    }

    public List<String> getProductUnactivity(List<String> productIds) {

        if(productIds == null || productIds.isEmpty()) throw new NullPointerException("productIDs is null or empty");

        List<String> returnList = productMetaMapper.bulkCheckUnactivityById(productIds);
        return returnList;
    }

}
