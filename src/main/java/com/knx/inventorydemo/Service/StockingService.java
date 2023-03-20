package com.knx.inventorydemo.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.knx.inventorydemo.entity.Order;
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

    public void pullOriginMeasurement(List<ProductMovement> movements){
        measurementService
    }

    public StockingService(){

    }

    public StockingService(ProductStockingMapper productStockingMapper, ProductMovementMapper productMovementMapper, MeasurementService measurementService){
        this.measurementService = measurementService;
        this.pMovementMapper = productMovementMapper;
        this.pStockingMapper = productStockingMapper;
    }
    
}