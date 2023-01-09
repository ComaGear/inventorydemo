package com.knx.inventorydemo.structure;

/**
 * when create a new product meta, it has unnullable field was id, name, defaultUom
 * 
 * 
 */
public class ProductMeta {

    // this often references to product barcode
    private String id;
    
    private String name;
    private Vendor vendor;
    private String defaultUom;
    private boolean activity;

    public boolean check(){
        if(id == null) throw new NullPointerException("ProductMeta's id is null");
        if(name == null) throw new NullPointerException("ProductMeta's name is null");
        return true;
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public String getDefaultUom() {
        return defaultUom;
    }

    public ProductMeta setDefaultUom(String defaultUom) {
        this.defaultUom = defaultUom;
        return this;
    }

    public ProductMeta(String id, String name, Vendor vendor) {
        this.id = id;
        this.name = name;
        this.vendor = vendor;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public ProductMeta setVendor(Vendor vendor) {
        this.vendor = vendor;
        return this;
    }

    public String getId() {
        return id;
    }

    public ProductMeta setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProductMeta setName(String name) {
        this.name = name;
        return this;
    }

}
