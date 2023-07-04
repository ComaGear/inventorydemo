package com.knx.inventorydemo.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.mapper.ProductMovementMapper;

public class StockMovementService {

    private ProductMovementMapper moveMapper;
    
    public List<ProductMovement> allMovementByProductIds(List<String> productId, Date startDate, Date endDate){

        List<ProductMovement> result = moveMapper.bulkGetMoveOutByProductIdInDate(productId, startDate, endDate);
    }
}
