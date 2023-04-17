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

    public List<ProductMeta> getAll();

    public void init();
}
