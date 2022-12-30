package com.knx.inventorydemo.mapper;

import java.util.List;

import com.knx.inventorydemo.structure.ProductMeta;

public interface ProductMetaMapper {

    public int addNewProduct(ProductMeta productMeta);

    public ProductMeta getProductById(int id);

    public ProductMeta getProductByStr(String str);

    public int deleteProductMetaById(int id);

    public int updateProductMetaById(ProductMeta productMeta);

    public int update(ProductMeta productMeta);

    public int setActivityById(int id);
    
    public boolean checkActivityById(int id);

    public List<ProductMeta> getAll();
}
