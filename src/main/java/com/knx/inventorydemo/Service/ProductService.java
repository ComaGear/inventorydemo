package com.knx.inventorydemo.Service;

import java.util.List;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.mapper.ProductMetaMapper;

public class ProductService {
    
    ProductMetaMapper productMetaMapper;

    MeasurementService measurementService;

    private StockingService stockingService;


    public void setStockingService(StockingService stockingService) {
        this.stockingService = stockingService;
    }

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
            productMeta.setDefaultUom(productMeasurement.getUOM());
        }
        if(productMeta.getId() == null || productMeta.getName() == null) throw new NullPointerException();

        // implement
        ProductMeta checkingProductMeta = productMetaMapper.getProductById(productMeta.getId());
        if(checkingProductMeta != null && !checkingProductMeta.getName().isEmpty()) return;
        
        productMetaMapper.addNewProduct(productMeta);
        measurementService.addNewMeasurementToProduct(productMeta, productMeasurement);
        stockingService.creatingNewStocking(productMeta.getId());
    }

    /**
     * this method create added productMeta and provide default uom.
     */
    public void addNewProduct(ProductMeta productMeta){
        addNewProduct(productMeta, new ProductUOM()
            .setRelativeId(productMeta.getId() + "-" + "UNIT")
            .setMeasurement(1f)
            .setUOM("UNIT")
            .setProductId(productMeta.getId()
        ));
    }

    /**
     * delete productMeta specify by product id, only delete the product has not stocking movement record.
     * 
     * @return result of deleting, -1 is unsuccess, 0 is product id not found, 1 is deleted
     */
    public int delete(ProductMeta productMeta){

        String productId = productMeta.getId();

        ProductMeta product = productMetaMapper.getProductById(productId);
        if(product.getName() == null || product.getName().isEmpty()){
            return 0;
        }

        List<ProductMovement> moves = stockingService.getAllMoveRecord(productId);

        if(moves == null || moves.isEmpty()){
            stockingService.removeStockingForProductId(productId);
            measurementService.removeAllMeasureForProductId(productId);
            int result = productMetaMapper.deleteProductMetaById(productId);
            stockingService.removeStockingForProductId(productId);
            return result == 1 ? 1 : -1;
        } else {
            return -1;
        }
    }

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

    public List<String> lookupUnexistProduct(List<String> productIds){
        
        productMetaMapper.prepareForUnexistProductIds();
        productMetaMapper.insertToCheckExistProductIds(productIds);
        List<String> unexistProductIds = productMetaMapper.getUnexistProductIds();

        productMetaMapper.endOfGetUnexistProductIds();

        return unexistProductIds;
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

    public void update(ProductMeta productMeta) {

        productMetaMapper.update(productMeta);
    }

}
