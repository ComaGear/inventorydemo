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

    public List<ProductMeasurement> getProductMeasByProductIdWithChannel(String productId);

    public ProductMeasurement getProductMeasByRelativeIdWithChannel(String channel, String relativeId);

    public List<ProductMeasurement> bulkGetProductMeasByRelativeIdwithChannel(String channel, List<String> relativeIds);
    
    public List<ProductMeasurement> getProductMeasListBySimilarRelativeId(String channel, String relativeId);

    /**
     * this method is a phase of getting unexist product's relative id in database.
     * @see getUnexistRelativeIds
     * @return success of creating temporary table.
     */
    public boolean prepareForUnexistRelativeIds();

    
    /**
     * this method is a phase of getting unexist product's relative id in database.
     * @see getUnexistRelativeIds
     * @param relativeIds a list of product's relative id being check.
     * @return how much row has been inserted.
     */
    public int insertToCheckExistRelativeIds(List<String> relativeIds);


    /**
     * <pre>
     * this method obtains un-existed product's relative id, it require some step before can obtains it.
     * <strong>phase 1</strong> prepareForUnexistRelativeIds to creating a table.
     * <strong>phase 2</strong> insertToCheckExistRelativeIds inserting which product's relative id id being loopup.
     * <strong>phase 3</strong> getUnexistRelativeIds receive which product's relative id id is not existed.
     * <strong>phase 4</strong> endOfGetUnexistRelativeIds clearing all temporary data.
     * </pre>
     * @return a list of un-existed product's relative id id in database's product_meta.
     */
    public List<String> getUnexistRelativeIds();

    /**
     * this method is a phase of getting unexist product id in database.
     * @see getUnexistRelativeIds
     * @return how much row has been deleted.
     */
    public int endOfGetUnexistRelativeIds();
    
    public void measInit();

    public int bulkRemoveMeasureByProductIds(List<String> productIds);

    // TODO implementing bulkRemoveMeasureByRelativeIds
    public int bulkRemoveMeasureByRelativeIds(List<String> relativeIdList);

    // public void updateRuleInit(String layer);
}
