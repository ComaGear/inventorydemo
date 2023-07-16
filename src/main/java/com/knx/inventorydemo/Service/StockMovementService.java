package com.knx.inventorydemo.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.mapper.ProductMovementMapper;

public class StockMovementService {

    private ProductMovementMapper moveMapper;
    
    public List<ProductMovement> allMoveOutByProductIds(List<String> productId, Date startDate, Date endDate){

        List<ProductMovement> result = moveMapper.bulkGetMoveOutByProductIdInDate(productId, startDate, endDate);
        return result;
    }
    public List<ProductMovement> allMoveInByProductIds(List<String> productId, Date startDate, Date endDate){

        List<ProductMovement> result = moveMapper.bulkGetMoveInByProductIdInDate(productId, startDate, endDate);
        return result;
    }

    public List<ProductMovement> allMoveByProductId(List<String> productId, Date startDate, Date endDate){

        List<ProductMovement> moveInResult = moveMapper.bulkGetMoveInByProductIdInDate(productId, startDate, endDate);
        List<ProductMovement> moveOutResult = moveMapper.bulkGetMoveOutByProductIdInDate(productId, startDate, endDate);

        Comparator<ProductMovement> comparator = new Comparator<ProductMovement>() {
            @Override
            public int compare(ProductMovement o1, ProductMovement o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };

        LinkedList<ProductMovement> returnList = new LinkedList<ProductMovement>();
        returnList.addAll(moveOutResult);
        returnList.addAll(moveInResult);
        returnList.sort(comparator);
        return returnList;
    }

    public StockMovementService(ProductMovementMapper productMovementMapper) {
        this.moveMapper = productMovementMapper;
    }
}
