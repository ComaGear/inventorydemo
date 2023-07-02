package com.knx.inventorydemo.xlsxTests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.StockingService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.utils.PosSystemOrderReportContentHandler;
import com.knx.inventorydemo.utils.ShopeeOrderReportContentHandler;
import com.knx.inventorydemo.utils.StockMoveConverter;
import com.knx.inventorydemo.utils.StockMoveOutXLSXParser;

@SpringBootTest
public class StockMoveOutParserTest {
    
    private static final String ONLINE_TARGET_FILE_PATH = "Order.toship.20230601_20230603.xlsx";
    private static final String POS_TARGET_FILE_PATH = "rptProductSalesListingWithCostDetail1.xlsx";

    private static final String BUFFER_PATH = "C:/Users/comag/Downloads";

    private static final Logger log = LoggerFactory.getLogger(StockMoveOutParserTest.class);

    private StockMoveOutXLSXParser parser;
    final StockMoveConverter converter = new StockMoveConverter() {

        @Override
        public List<StockMoveOut> convert(final List<StockMoveOut> moveOuts) {
            final List<String> relativeIds = new LinkedList<String>();
            final Iterator<StockMoveOut> iterator = moveOuts.iterator();
            while(iterator.hasNext()){
                final StockMoveOut next = iterator.next();
                if(next.getRelativeId() != null) relativeIds.add(next.getRelativeId());
            }
            final Map<String, ProductMeasurement> measureMap = stockingService.pullOriginMeasurementByRelativeIds(relativeIds);

            final Iterator<StockMoveOut> iterator2 = moveOuts.iterator();
            while(iterator2.hasNext()){
                final StockMoveOut next = iterator2.next();
                final float measureSize = measureMap.get(next.getRelativeId()).getMeasurement();
                final double quantity = next.getQuantity();
                next.setQuantity(quantity * measureSize);
                next.setProductId(measureMap.get(next.getRelativeId()).getProductId());
            }
            return moveOuts;
        }
    };
    @Autowired
    private StockingService stockingService;

    @BeforeEach
    public void prepare(){
        parser = new StockMoveOutXLSXParser(BUFFER_PATH);
    }

    @Test
    public void readingXLSXFile_NotErrorCause(){
        List<StockMoveOut> parse = parser.parse(ONLINE_TARGET_FILE_PATH, null, ShopeeOrderReportContentHandler.class);
        log.debug(parse.toString());
        assertNotNull(parse);
    }

    @Test
    public void readingPosDataFile_NotErrorCause(){
        List<StockMoveOut> parse = parser.parse(POS_TARGET_FILE_PATH, null, PosSystemOrderReportContentHandler.class);
        log.debug(parse.toString());
        assertNotNull(parse);
    }
}
