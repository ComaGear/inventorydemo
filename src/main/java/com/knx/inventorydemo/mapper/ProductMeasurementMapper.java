package com.knx.inventorydemo.mapper;

import java.util.List;

import com.knx.inventorydemo.entity.ProductMeasurement;

public interface ProductMeasurementMapper {

    public int addMeasureTo(String channel, ProductMeasurement measure);
 
    public int updateMeasureTo(String channel, ProductMeasurement measure, String relativeId);

    /**
     * change all product's measurements update rule by specify product ID.
     * @param channel
     * @param updateRule
     * @param productId
     * @return how many row has been update.
     */
    public int changeUpdateRuleToByProductId(String channel, String updateRule, String productId);

    // public int checkLayerExists(String layer);

    public List<ProductMeasurement> getProductMeasByProductIdWithChannel(String channel, String productId);

    public ProductMeasurement getProductMeasByRelativeIdWithChannel(String channel, String relativeId);

    public List<ProductMeasurement> bulkGetProductMeasByRelativeIdwithChannel(String channel, List<String> relativeIds);
    
    public List<ProductMeasurement> getProductMeasListBySimilarRelativeId(String channel, String relativeId);
    
    public void measInit();

    public int bulkRemoveMeasureByProductIds(List<String> productIds);

    // TODO implementing bulkRemoveMeasureByRelativeIds
    public int bulkRemoveMeasureByRelativeIds(List<String> relativeIdList);

    // public void updateRuleInit(String layer);
}
