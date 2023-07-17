package com.knx.inventorydemo.mapper;

import java.util.List;

import com.knx.inventorydemo.entity.ProductMeta;

public interface ProductMetaMapper {

    public int addNewProduct(ProductMeta productMeta);

    public ProductMeta getProductById(String id);

    /**
     * @param str String for selecting, can compose finding specifiy by add %str%
     * Example I select the single word arround with %word%, then multi word can using %apollo%%a3020% together.
     * 
     * 
     * @return a list of {@link} ProductMeta contains id and productName only
     */
    public List<ProductMeta> getProductByStr(String str);

    public int deleteProductMetaById(String id);

    public int update(ProductMeta productMeta);

    public int setActivityById(int id, boolean activity);

    public List<String> bulkCheckUnactivityById(List<String> productIds);
    
    public boolean checkActivityById(int id);

    
    /**
     * this method is a phase of getting unexist product id in database.
     * @see getUnexistProductIds
     * @return success of creating temporary table.
     */
    public boolean prepareForUnexistProductIds();

    
    /**
     * this method is a phase of getting unexist product id in database.
     * @see getUnexistProductIds
     * @param productIds a list of product id being check.
     * @return how much row has been inserted.
     */
    public int insertToCheckExistProductIds(List<String> productIds);


    /**
     * <pre>
     * this method obtains un-existed product's id, it require some step before can obtains it.
     * <strong>phase 1</strong> prepareForUnexistProductIds to creating a table.
     * <strong>phase 2</strong> insertToCheckExistProductIds inserting which product's id being loopup.
     * <strong>phase 3</strong> getUnexistProductIds receive which product's id is not existed.
     * <strong>phase 4</strong> endOfGetUnexistProductIds clearing all temporary data.
     * </pre>
     * @return a list of un-existed product's id in database's product_meta.
     */
    public List<String> getUnexistProductIds();

    /**
     * this method is a phase of getting unexist product id in database.
     * @see getUnexistProductIds
     * @return how much row has been deleted.
     */
    public int endOfGetUnexistProductIds();

    public List<ProductMeta> getAll();

    public void init();
}
