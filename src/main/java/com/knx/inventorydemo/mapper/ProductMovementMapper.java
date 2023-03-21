package com.knx.inventorydemo.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.knx.inventorydemo.entity.ProductMovement;

public interface ProductMovementMapper {
    
    public int bulkInsertMovements(List<ProductMovement> moves);
    
    public int bulkUpdateMovements(List<ProductMovement> moves);

    public Set<ProductMovement> bulkGetMovements(List<String> relativeIds);
}
