package com.knx.inventorydemo.mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.entity.ProductMovement.relativeAndProductId;

public interface ProductMovementMapper {
    
    public int bulkInsertMoveIn(List<StockMoveIn> moveIns);

    public int bulkInsertMoveOut(List<StockMoveOut> moveOuts);
    
    public int bulkUpdateMoveIn(List<StockMoveIn> moveIns, String relativeId, String productId);

    public int bulkUpdateMoveOut(List<StockMoveIn> moveOuts, String relativeId, String productId);

    //TODO: following to implements
    public Set<ProductMovement> bulkGetMovements(List<String> relativeIds);

    public Set<ProductMovement> bulkGetMovementsByProductId(List<String> productIds);

    public Set<ProductMovement> bulkGetMovementsByProductId(List<String> productIds, Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMovementsSpecifyDate(Date startDate, Date endDate);

    public int bulkDeleteMovements(List<relativeAndProductId> relativeAndProductIds);

    public void init();
}
