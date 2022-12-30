package com.knx.inventorydemo.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.knx.inventorydemo.mapper.ProductMetaMapper;
import com.knx.inventorydemo.structure.ProductMeta;

public class ProductService {
    
    @Autowired
    ProductMetaMapper productMetaMapper;

    public void addNewProduct(ProductMeta productMeta){
        if(productMeta.getDefaultUom() == null) productMeta.setDefaultUom("UNIT");
        if(productMeta.getId() == null || productMeta.getName() == null) throw new NullPointerException();

        productMetaMapper.addNewProduct(productMeta);
    }

    public List<ProductMeta> getAllProductMeta(){
        return productMetaMapper.getAll();
    }

}
