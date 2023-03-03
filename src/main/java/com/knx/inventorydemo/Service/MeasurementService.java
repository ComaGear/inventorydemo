package com.knx.inventorydemo.Service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.exception.UnkrownLayerException;
import com.knx.inventorydemo.mapper.ProductMeasurementMapper;

public class MeasurementService  {

    private ProductMeasurementMapper productMeasurementMapper;

    public MeasurementService(ProductMeasurementMapper productMeasurementMapper) {
        this.productMeasurementMapper = productMeasurementMapper;
    }

    public boolean checkLayerExists(String layer) {
        try {
            String t = "product_meas_" + layer;
            int i = productMeasurementMapper.checkLayerExists(t);
            if(i == 1) return true;
        }catch(DataAccessException e){
            throw e;
        }
        return false;
    }

    public List<ProductMeasurement> findAllCustomMeasurementByProductId(String layer, String id) {
        if(id == null || id.equals("")) throw new NullPointerException("provided id is null");
        
        List<ProductMeasurement> productMeasList = productMeasurementMapper.getProductMeasByProductIdWithLayer(layer, id);

        return productMeasList;
    }

    public List<ProductMeasurement> findAllCustomMeasurementByProductId(String id) {
        String layer = ProductUOM.LAYER;
        return findAllCustomMeasurementByProductId(layer, id);
    }


    /**
     * add a new product's measurment to specify layer of sales channel.
     * @param product should contains at least product's id.
     */
    public void addNewMeasurementToProduct(ProductMeta product, ProductMeasurement measurement) {

        if(product.getId() == null || product.getId() == "") throw new NullPointerException("product meta id is null");
        if(measurement.getMeasurement() <= 0) throw new IllegalArgumentException("Measurement's measure value should not less than zero");
        if(measurement.getLayer() != ProductUOM.LAYER) {
            if(measurement.getRelativeId() == null || measurement.getRelativeId() == "") throw new NullPointerException("measurement's relative id is null");
        } 
        if(measurement.getLayer() == null || measurement.getLayer().equals("")) throw new NullPointerException("measurement's layer is null");

        // checking param ProductMeasurement existence.
        if(measurement.getRelativeId() != null){
            ProductMeasurement checkingMeasurement = this.getProductMeasByRelativeIdWithLayer(measurement.getRelativeId(), measurement.getLayer());
            if(checkingMeasurement != null && checkingMeasurement.getProductId() != null) return; // measurement relative id is already exist.
        }

        // relativing 
        if(measurement.getProductId() == null || measurement.getProductId() == "") {
            measurement.setProductId(product.getId());
        }
        if(!this.checkLayerExists(measurement.getLayer())) throw new UnkrownLayerException(measurement.getLayer() + " is not exists");
        
        productMeasurementMapper.addMeasureTo(measurement.getLayer(), measurement);
    }

    private ProductMeasurement getProductMeasByRelativeIdWithLayer(String relativeId, String layer) {
        if(relativeId == null || relativeId.equals("")) throw new NullPointerException("param of relative id is null");
        if(layer == null || layer.equals("")) throw new NullPointerException("layer is null");

        //implements
        return productMeasurementMapper.getProductMeasByRelativeIdWithLayer(layer, relativeId);
    }

    /**
     * this method is used for adding a new child to exists parent sku.
     * @param measure contains available productId, measurement and layer 
     * @param parentSku if parentSku is single in parent tree, it will update with a new 
     * @return return a modified parentsku with new sku value.
     * @throws IllegalAccessException
     */
    public String addChildSkuToExistsSku(ProductMeasurement measure, String parentSku) throws IllegalAccessException{
        if(measure.getProductId() == null || measure.getProductId() == "") throw new IllegalArgumentException("Product Measurement's product Id is null");
        if(measure.getMeasurement() <= 0) throw new IllegalArgumentException("Product Measurement's measure should not less than zero");
        if(measure.getLayer() == null || measure.getLayer() == "") throw new IllegalArgumentException("Product Measurement's layer is null");
        
        // modify parentsku with only sku not child sku.
        if(parentSku.contains("-")) {
            int index = parentSku.indexOf("-");
            parentSku = parentSku.substring(0, index);
        }
        boolean exists = false;

        // bulk get where like parent sku 
        List<ProductMeasurement> bulkGet = productMeasurementMapper.getProductMeasListBySimilarRelativeId(measure.getLayer(), parentSku + "%");

        if(bulkGet == null || bulkGet.isEmpty()) {
            throw new IllegalArgumentException(parentSku + " is not exists.");
        } else {
            for (ProductMeasurement gMeasurement : bulkGet) {
                if(gMeasurement.getProductId() != null && !gMeasurement.getProductId().equals("")) {
                    exists = true;
                }
            }
        }

        if(!exists){
            throw new IllegalArgumentException("parentSku is not exist in database");
        }   

        if (bulkGet.size() == 1){
            char c = 'a';
            ProductMeasurement parentMeas = bulkGet.get(0);
            if(parentMeas.getRelativeId().contains("-")) throw new IllegalAccessException("Parent Measurement contains '-' as child");

            String parentNewSku;
            parentNewSku = parentSku + "-" + Character.toString(c);
            
            parentMeas.setRelativeId(parentNewSku);
            productMeasurementMapper.updateMeasureTo(parentMeas.getLayer(), parentMeas, parentSku);

            //process child meas as first child.
            c++;
            measure.setRelativeId(parentSku + "-" + Character.toString(c));
            productMeasurementMapper.addMeasureTo(measure.getLayer(), measure);
        } else{

            // get Charater after "-" and add 1 to next char bring new sku.
            String nextSku = null;
            String vaId = bulkGet.get(bulkGet.size() -1).getRelativeId();
            if(vaId.contains("-")) {
                int index = vaId.indexOf("-");
                vaId = vaId.substring(index, vaId.length());
                char c = vaId.toCharArray()[1];
                nextSku = parentSku + "-"+ ++c;
            }

            measure.setRelativeId(nextSku);
            productMeasurementMapper.addMeasureTo(measure.getLayer(), measure);
        }

        return parentSku;
    }

    public void init(String layer) {
        if(layer == null) productMeasurementMapper.measInit(ProductUOM.LAYER);
        productMeasurementMapper.measInit(layer);

        // TODO: Update Rule Setting 
    }

    public void updateMeasUpdateRule(String relativeId, String productId, String updateRule, String layer) {

        if(updateRule == null || updateRule.equals("")) throw new NullPointerException("Measurement's update rule is null");
        if(layer == null || layer.equals("")) throw new NullPointerException("Measurement's update rule is null");

        if(relativeId != null && !relativeId.equals("")){

            ProductMeasurement measurement = new ProductMeasurement();
            measurement.setLayer(layer)
                .setRelativeId(relativeId)
                .setUpdateRule(updateRule);
            productMeasurementMapper.updateMeasureTo(layer, measurement, measurement.getRelativeId());

        } else if(productId != null && !productId.equals("")){
            productMeasurementMapper.changeUpdateRuleToByProductId(layer, updateRule, productId);

        } else {
            throw new NullPointerException("Measurement's relative id is null");
        }
    }

    private void updateByRelativeId(ProductMeasurement productMeas) {
        productMeasurementMapper.updateMeasureTo(productMeas.getLayer(), productMeas, productMeas.getRelativeId());
    }
    
}
