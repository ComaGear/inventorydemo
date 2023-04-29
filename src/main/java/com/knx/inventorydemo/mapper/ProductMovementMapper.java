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

    public Set<ProductMovement> bulkGetMoveIn(List<String> relativeIds);

    public Set<ProductMovement> bulkGetMoveOut(List<String> relativeIds);

    public Set<ProductMovement> bulkGetMoveInByProductId(List<String> productIds);

    public Set<ProductMovement> bulkGetMoveOutByProductId(List<String> productIds);

    public List<ProductMovement> bulkGetMoveOutByOrderIdsAndProductIds(List<StockMoveOut> movements);

    public List<String> getExistsOrderIds(List<String> orderIds);

    public List<String> getExistsDocsIds(List<String> docsIds);

    /**
     * @return Map key by order Id, value is record count size
     */
    public List<Order> bulkGetRecordSizeOfOrderByOrderId(List<String> orderIds);

    public Set<ProductMovement> bulkGetMoveInByProductIdInDate(List<String> productIds, Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMoveOutByProductIdInDate(List<String> productIds, Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMoveInSpecifyDate(Date startDate, Date endDate);

    public Set<ProductMovement> bulkGetMoveOutSpecifyDate(Date startDate, Date endDate);

    public int bulkDeleteMovements(List<String> relativeIds, List<String> productIds);

    public void init();
}
