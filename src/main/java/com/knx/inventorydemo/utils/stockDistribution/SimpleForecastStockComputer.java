package com.knx.inventorydemo.utils.stockDistribution;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.knx.inventorydemo.Service.StockMovementService;

public class SimpleForecastStockComputer implements ForcastStockDistributeComputer {

    StockMovementService service;

    @Override
    public boolean setforecastingDate(Date startDate, Date endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setforecastingDate'");
    }

    @Override
    public Map<String, List<ForecastedStock>> forecastSpecifyProductByIds(List<String> productIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forecastSpecifyProductByIds'");
    }

    @Override
    public Map<String, List<ForecastedStock>> forecastAllActiveProduct() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forecastAllActiveProduct'");
    }

    public SimpleForecastStockComputer(StockMovementService service){
        this.service = service;
    }
    
}
