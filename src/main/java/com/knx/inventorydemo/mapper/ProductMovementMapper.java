package com.knx.inventorydemo.mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;

public interface ProductMovementMapper {
    
    public int bulkInsertMoveIn(List<StockMoveIn> moveIns);

    public int bulkInsertMoveOut(List<StockMoveOut> moveOuts);
    
    public int bulkUpdateMoveIn(List<StockMoveIn> moveIns, String relativeId, String productId);

    public int bulkUpdateMoveOut(List<StockMoveIn> moveOuts, String relativeId, String productId);

    public Set<ProductMovement> bulkGetMoveIn(List<String> relativeIds);

    public Set<ProductMovement> bulkGetMoveOut(List<String> relativeIds);

    public Set<ProductMovement> bulkGetMoveInByProductId(List<String> productIds);

    public Set<ProductMovement> bulkGetMoveOutByProductId(List<String> productIds);

    public Set<ProductMovement> bulkGetMoveInByProductId(List<String> productIds, Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMoveOutByProductId(List<String> productIds, Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMoveInSpecifyDate(Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMoveOutSpecifyDate(Date startDate, Date endDate);

    public int bulkDeleteMovements(List<String> relativeIds, List<String> productIds);

    public void init();
}
