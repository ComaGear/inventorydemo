package com.knx.inventorydemo.utils.moveOutParser;

import java.util.List;

import com.knx.inventorydemo.entity.StockMoveOut;

public interface StockMoveConverter {

    public List<StockMoveOut> convert(List<StockMoveOut> moveOuts);
}
