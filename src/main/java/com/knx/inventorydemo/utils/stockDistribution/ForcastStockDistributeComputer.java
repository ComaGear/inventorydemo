package com.knx.inventorydemo.utils.stockDistribution;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * this interface rule computing stock need for a range of dates. 
 */
public interface ForcastStockDistributeComputer{

    public boolean setforecastingDate(Date startDate, Date endDate);

    public Map<String, List<ForecastedStock>> forecastSpecifyProductByIds(List<String> productIds);

    public Map<String, List<ForecastedStock>> forecastAllActiveProduct();
    
}