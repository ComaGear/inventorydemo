package com.knx.inventorydemo.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.mapper.ProductMeasurementMapper;

public class MeasurementService  {

    static Logger logger = LoggerFactory.getLogger(MeasurementService.class);

    private ProductMeasurementMapper productMeasurementMapper;

    public MeasurementService(ProductMeasurementMapper productMeasurementMapper) {
        this.productMeasurementMapper = productMeasurementMapper;
    }

    public boolean checkChannelExist(String channel) {
        List<ProductMeasurement> measList = productMeasurementMapper.getProductMeasListBySimilarRelativeId(channel, "%%" );
        if(measList == null || measList.isEmpty()) return false;
        if(measList.get(0).getSalesChannel() == channel) return true;
        return false;
    }

    public List<ProductMeasurement> findAllCustomMeasurementByProductId(String id) {
        if(id == null || id.equals("")) throw new NullPointerException("provided id is null");
        
        List<ProductMeasurement> productMeasList = productMeasurementMapper.getProductMeasByProductIdWithChannel(id);

        return productMeasList;
    }

    // public List<ProductMeasurement> findAllCustomMeasurementByProductId(String id) {
    //     String layer = ProductUOM.LAYER;
    //     return findAllCustomMeasurementByProductId(layer, id);
    // }

    public Map<String, ProductMeasurement> getProductMeasByRelativeIdWithChannel(List<String> relativeIds, String channel) {
        if(relativeIds.isEmpty()) throw new IllegalArgumentException("relativeIds is empty");

        // logger.info(relativeIds.toString());

        List<ProductMeasurement> measList = productMeasurementMapper.bulkGetProductMeasByRelativeIdwithChannel(channel, relativeIds);

        // logger.info(measList.toString());

        // if(measList == null || measList.isEmpty()) {
        //     if(this.checkChannelExist(channel)){
        //         throw new IllegalArgumentException("channel is not exists");
        //     }
        //     throw new ResultMapException("have not relativeId's measurement exists");
        // }

        HashMap<String, ProductMeasurement> measMap = new HashMap<String, ProductMeasurement>();
        for(ProductMeasurement meas : measList){
            measMap.put(meas.getRelativeId(), meas);
        }
        
        return measMap;
    }

    public Map<String, ProductMeasurement> getProductMeasByRelativeIds(List<String> relativeIds){
        List<ProductMeasurement> list = productMeasurementMapper.bulkGetProductMeasByRelativeIdwithChannel(null, relativeIds);
        Map<String, ProductMeasurement> measMap = new HashMap<String, ProductMeasurement>();
        for(ProductMeasurement meas : list){
            measMap.put(meas.getRelativeId(), meas);
        }
        return measMap;
    }


    /**
     * add a new product's measurment to specify layer of sales channel.
     * @param product should contains at least product's id.
     */
    public void addNewMeasurementToProduct(ProductMeta product, ProductMeasurement measurement) {

        if(product.getId() == null || product.getId() == "") throw new NullPointerException("product meta id is null");
        if(measurement.getMeasurement() <= 0) throw new IllegalArgumentException("Measurement's measure value should not less than zero");
        if(measurement.getSalesChannel() != ProductUOM.LAYER) {
            if(measurement.getRelativeId() == null || measurement.getRelativeId() == "") throw new NullPointerException("measurement's relative id is null");
        } 
        if(measurement.getSalesChannel() == null || measurement.getSalesChannel().equals("")) throw new NullPointerException("measurement's layer is null");

        // checking param ProductMeasurement existence.
        if(measurement.getRelativeId() != null){
            LinkedList<String> relativeIds = new LinkedList<String>();
            
            // check income product measurement contains "-" as child, and then obtain parent sku.
            String parentSku = measurement.getRelativeId();
            if(parentSku.contains("-")) {
                int index = parentSku.indexOf("-");
                parentSku = parentSku.substring(0, index);
            }

            relativeIds.add(measurement.getRelativeId());
            relativeIds.add(parentSku + "-a");
            ProductMeasurement checkingMeasurement = this.getProductMeasByRelativeIdWithChannel(relativeIds, measurement.getSalesChannel()).get(measurement.getRelativeId());
            if(checkingMeasurement != null && checkingMeasurement.getProductId() != null) return; // measurement relative id is already exist.
        }

        // relativing 
        if(measurement.getProductId() == null || measurement.getProductId() == "") {
            measurement.setProductId(product.getId());
        }
        // if(!this.checkLayerExists(measurement.getSalesChannel())) throw new UnkrownLayerException(measurement.getSalesChannel() + " is not exists");
        
        productMeasurementMapper.addMeasureTo(measurement.getSalesChannel(), measurement);
    }

    public boolean removeAllMeasureForProductId(String productId){
        if(productId == null || productId.isEmpty()) throw new NullPointerException();

        List<String> list = new LinkedList<String>();
        list.add(productId);

        return productMeasurementMapper.bulkRemoveMeasureByProductIds(list) > 0;
    }

    public boolean removeMeasureByRelativeId(String relativeId){
        // TODO
        return false;
    }

    private ProductMeasurement getProductMeasByRelativeIdWithChannel(String relativeId, String channel) {
        if(relativeId == null ||relativeId.equals("")) throw new NullPointerException("param of relative id is null");
        if(channel == null || channel.equals("")) throw new NullPointerException("layer is null");

        LinkedList<String> linkedList = new LinkedList<String>();
        linkedList.add(relativeId);
        Map<String, ProductMeasurement> measMap = this.getProductMeasByRelativeIdWithChannel(linkedList, channel);

        return measMap.get(relativeId);
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
        if(measure.getSalesChannel() == null || measure.getSalesChannel() == "") throw new IllegalArgumentException("Product Measurement's layer is null");
        
        // modify parentsku with only sku not child sku.
        if(parentSku.contains("-")) {
            int index = parentSku.indexOf("-");
            parentSku = parentSku.substring(0, index);
        }
        boolean exists = false;

        // bulk get where like parent sku s
        List<ProductMeasurement> bulkGet = productMeasurementMapper.getProductMeasListBySimilarRelativeId(measure.getSalesChannel(), parentSku + "%");

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
            productMeasurementMapper.updateMeasureTo(parentMeas.getSalesChannel(), parentMeas, parentSku);

            //process child meas as first child.
            c++;
            measure.setRelativeId(parentSku + "-" + Character.toString(c));
            productMeasurementMapper.addMeasureTo(measure.getSalesChannel(), measure);
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
            productMeasurementMapper.addMeasureTo(measure.getSalesChannel(), measure);
        }

        return parentSku;
    }

    

    public void init() {
        productMeasurementMapper.measInit();
        // TODO: Update Rule Setting 
    }

    public void updateMeasUpdateRule(String relativeId, String productId, String updateRule, String layer) {

        if(updateRule == null || updateRule.equals("")) throw new NullPointerException("Measurement's update rule is null");
        if(layer == null || layer.equals("")) throw new NullPointerException("Measurement's update rule is null");

        if(relativeId != null && !relativeId.equals("")){

            ProductMeasurement measurement = new ProductMeasurement();
            measurement.setSalesChannel(layer)
                .setUpdateRule(updateRule);
            productMeasurementMapper.updateMeasureTo(layer, measurement, measurement.getRelativeId());

        } else if(productId != null && !productId.equals("")){
            productMeasurementMapper.changeUpdateRuleToByProductId(layer, updateRule, productId);

        } else {
            throw new NullPointerException("Measurement's relative id is null");
        }
    }

    public void updateByRelativeId(ProductMeasurement productMeas, String relativeId) {
        productMeasurementMapper.updateMeasureTo(productMeas.getSalesChannel(), productMeas, relativeId);
    }

    public List<String> lookupMeasurementExistence(List<String> relativeIds) {

        productMeasurementMapper.prepareForUnexistRelativeIds();
        productMeasurementMapper.insertToCheckExistRelativeIds(relativeIds);
        List<String> unexistRelativeIds = productMeasurementMapper.getUnexistRelativeIds();

        productMeasurementMapper.endOfGetUnexistRelativeIds();

        return unexistRelativeIds;
    }
    
}
