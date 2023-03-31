package com.knx.inventorydemo.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.mapper.ProductMovementMapper;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

public class StockingService{

    private ProductStockingMapper pStockingMapper;
    private ProductMovementMapper pMovementMapper;
    private MeasurementService measurementService;

    private Queue<ProductMovement> pendingMovements;

    public void pushMovement(ProductMovement movement){
        pendingMovements.add(movement);
    }

    public void pushMovement(Order order){
        if(order == null || !order.hasMovement()){ throw new NullPointerException("order is null or emptry."); }
        
        Iterator<ProductMovement> iterator = order.getMovements().iterator();
        while(iterator.hasNext()){
            ProductMovement moves = iterator.next();
            pendingMovements.add(moves);
        }
        order.setAnalysed(true);
    }

    /**
     * this method getting origin measurement of product meta by specify measurement's relativeId of ProdutMovement. 
     * 
     * @param movements List of ProductMovement
     * @return Map key by sales channel, and value is map key by ProductMeasurement's relativeId.
     */
    public HashMap<String, Map<String, ProductMeasurement>> pullOriginMeasurement(List<ProductMovement> movements){

        //TODO: null point check.

        HashMap<String, List<String>> channelMovementMap = new HashMap<String, List<String>>();
        Set<String> channelKeySet = channelMovementMap.keySet();
        HashMap<String, Map<String, ProductMeasurement>> resultMovementMap = new HashMap<String, Map<String, ProductMeasurement>>();

        for(ProductMovement movement : movements){
            if(!channelKeySet.contains(movement.getSalesChannel())){
                channelMovementMap.put(movement.getSalesChannel(), new ArrayList<String>());
            }

            List<String> relativeIds = channelMovementMap.get(movement.getSalesChannel());
            if(!relativeIds.contains(movement.getRelativeId())){
                relativeIds.add(movement.getRelativeId());
            }
        }

        for(String salesChannel : channelKeySet){
            List<String> relativeIds = channelMovementMap.get(salesChannel);
            Map<String, ProductMeasurement> resultMap = measurementService.getProductMeasByRelativeIdWithChannel(relativeIds, salesChannel);
            resultMovementMap.put(salesChannel, resultMap);
        }

        return resultMovementMap;
    }

    // HashMap<String, ProductMovement> moveMap = new HashMap<>();
    // Set<ProductMovement> moves = pMovementMapper.bulkGetMovements(null);
    // Iterator<ProductMovement> iterator = moves.iterator();
    // while(iterator.hasNext()){
    //     ProductMovement next = iterator.next();
    //     moveMap.put(next.getRelativeId(), next);
    // }


    public StockingService(){

    }

    public StockingService(ProductStockingMapper productStockingMapper, ProductMovementMapper productMovementMapper, MeasurementService measurementService){
        this.measurementService = measurementService;
        this.pMovementMapper = productMovementMapper;
        this.pStockingMapper = productStockingMapper;
    }
    
}