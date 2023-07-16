package com.knx.inventorydemo.mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;

public interface ProductMovementMapper {
    
    public int bulkInsertMoveIn(List<StockMoveIn> moveIns);

    public int bulkInsertMoveOut(List<StockMoveOut> moveOuts);
    
    public int bulkUpdateMoveIn(List<StockMoveIn> moveIns);

    public int bulkUpdateMoveOut(List<StockMoveOut> moveOuts);

    // below not useful in large data, so discontinue
    // public List<StockMoveIn> bulkGetMoveIn(List<String> relativeIds);

    // public List<StockMoveOut> bulkGetMoveOut(List<String> relativeIds);

    // public List<StockMoveIn> bulkGetMoveInByProductId(List<String> productIds);

    // public List<StockMoveOut> bulkGetMoveOutByProductId(List<String> productIds);

    public List<ProductMovement> bulkGetMoveOutByOrderIdsAndProductIds(List<StockMoveOut> movements);

    public List<StockMoveOut> bulkGetMoveOutByOrderIds(List<String> orderIds);

    public List<StockMoveIn> bulkGetMoveInByDocsIds(List<String> docsIds);

    public List<String> getExistsOrderIds(List<String> orderIds);

    public List<String> getExistsDocsIds(List<String> docsIds);

    /**
     * @return Map key by order Id, value is record count size
     */
    public List<Order> bulkGetRecordSizeOfOrderByOrderId(List<String> orderIds);

    public List<ProductMovement> bulkGetMoveInByProductIdInDate(List<String> productIds, Date startDate, Date endDate);

    public List<ProductMovement> bulkGetMoveOutByProductIdInDate(List<String> productIds, Date startDate, Date endDate);

    public List<StockMoveIn> bulkGetMoveInInDate(List<String> relativeIds, Date startDate, Date endDate);

    public List<StockMoveOut> bulkGetMoveOutInDate(List<String> relativeIds, Date startDate, Date endDate);

    public List<ProductMovement> bulkGetMoveInSpecifyDate(Date startDate, Date endDate);

    public List<ProductMovement> bulkGetMoveOutSpecifyDate(Date startDate, Date endDate);

    public int bulkRemoveMoveOuts(List<StockMoveOut> toDeleteMoveOuts);

    public int bulkRemoveMoveIns(List<StockMoveIn> toDeleteMoveIns);

    public void init();
}
