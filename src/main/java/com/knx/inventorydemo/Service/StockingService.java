package com.knx.inventorydemo.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.mapper.ProductMovementMapper;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

public class StockingService{

    private ProductStockingMapper pStockingMapper;
    private ProductMovementMapper pMovementMapper;
    private MeasurementService measurementService;

    private PriorityBlockingQueue<ProductMovement> pendingMovements;

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

    public void pushMovement(List<Order> orders){
        if(orders == null || orders.isEmpty()) throw new NullPointerException("orders is null or emptry");

        for(Order order : orders){
            this.pushMovement(order);
        }
    }

    public List<ProductMovement> fetchMovesQueueMovementByRelativeId(List<String> relativeIds){
        if(relativeIds == null || relativeIds.isEmpty()) throw new NullPointerException(); // give a message.

        if(pendingMovements.isEmpty()) return null;

        ProductMovement[] MovementArray = (ProductMovement[]) pendingMovements.toArray();
        List<ProductMovement> movementList = Arrays.asList(MovementArray);

        // MovementList.sort(new Comparator<ProductMovement>() {
        //     @Override
        //     public int compare(ProductMovement o1, ProductMovement o2) {

        //         if(o1.getProductId() == o2.getProductId()) return 0;

        //         return o1.getProductId().compareToIgnoreCase(o2.getProductId());
        //     }
        // });
        movementList.sort(new Comparator<ProductMovement>() {
            @Override
            public int compare(ProductMovement o1, ProductMovement o2) {

                if(o1.getRelativeId() == o2.getRelativeId()) return 0;

                return o1.getRelativeId().compareToIgnoreCase(o2.getRelativeId());
            }
        });

        relativeIds.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                return o1.compareToIgnoreCase(o2);
            }
        });

        LinkedList<ProductMovement> linkedList = new LinkedList<ProductMovement>();
        int lastFoundIndex = 0;
        int relativeIdsIndex = 0;
        int movementIndex = 0;
        for( ; relativeIdsIndex < relativeIds.size(); relativeIdsIndex++){
            if(movementList.get(movementIndex).getRelativeId() == relativeIds.get(relativeIdsIndex)){
                linkedList.add(movementList.get(movementIndex));

                lastFoundIndex = movementIndex;
                continue;
            }

            if(movementIndex >= movementList.size()) {
                movementIndex = lastFoundIndex;
            }

            for( ; movementIndex < movementList.size(); movementIndex++){
                if(movementList.get(movementIndex).getRelativeId() == relativeIds.get(relativeIdsIndex)){
                    linkedList.add(movementList.get(movementIndex));
    
                    lastFoundIndex = movementIndex;
                    break;
                }
            }
        }

        return linkedList;
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
        pendingMovements = new PriorityBlockingQueue<>();
    }

    public StockingService(ProductStockingMapper productStockingMapper, ProductMovementMapper productMovementMapper, MeasurementService measurementService){
        this.measurementService = measurementService;
        this.pMovementMapper = productMovementMapper;
        this.pStockingMapper = productStockingMapper;
        this.pendingMovements = new PriorityBlockingQueue<>();
    }

    public void init() {
        //TODO:
    }

    // private class SynchronizedMovementQueue{
    //     private Queue<ProductMovement> pendingMovements;

    //     public SynchronizedMovementQueue() {
    //         this.pendingMovements = new queue
    //     }
    // }
    
}