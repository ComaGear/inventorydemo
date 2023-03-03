package com.knx.inventorydemo.mapper;

import java.util.List;

import com.knx.inventorydemo.entity.ProductMeasurement;

public interface ProductMeasurementMapper {

    public int addMeasureTo(String layer, ProductMeasurement measure);
 
    public int updateMeasureTo(String layer, ProductMeasurement measure, String relativeId);

    /**
     * change all product's measurements update rule by specify product ID.
     * @param layer
     * @param updateRule
     * @param productId
     * @return how many row has been update.
     */
    public int changeUpdateRuleToByProductId(String layer, String updateRule, String productId);

    public int checkLayerExists(String layer);

    public List<ProductMeasurement> getProductMeasByProductIdWithLayer(String layer, String productId);

    public ProductMeasurement getProductMeasByRelativeIdWithLayer(String layer, String relativeId);

    public List<ProductMeasurement> bulkGetProductMeasByRelativeIdwithLayer(String layer, List<String> relativeIds);
    
    public List<ProductMeasurement> getProductMeasListBySimilarRelativeId(String layer, String relativeId);
    
    public void measInit(String layer);

    public void updateRuleInit(String layer);
}
