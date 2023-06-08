package com.knx.inventorydemo.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.knx.inventorydemo.entity.StockMoveOut;

public class StockMoveOutXLSXParser {

    public static final String ONLINE = "ONLINE";

    enum dataType {
        NUMBER, SSTINDEX,
    }

    private String bufferPath;

    public List<StockMoveOut> parse(String targetFileName, StockMoveConverter StockMoveConverter){
        String path = bufferPath + "/" + targetFileName;
        return this.parse(new File(path), StockMoveConverter);
    }

    public List<StockMoveOut> parse(File targetFile, StockMoveConverter StockMoveConverter){
        if(targetFile == null || !targetFile.exists()) throw new IllegalArgumentException();

        ArrayList<StockMoveOut> moveOuts = new ArrayList<StockMoveOut>();

        try {
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(targetFile));
            ShopeeOrderReportContentHandler contentHandler = new ShopeeOrderReportContentHandler(xssfReader.getSharedStringsTable(),
             xssfReader.getStylesTable(), moveOuts);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource inputSource = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(inputSource);
            
        } catch (IOException | OpenXML4JException | SAXException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return moveOuts; // return a empty movement list.
        }

        if(moveOuts != null && !moveOuts.isEmpty()){
            moveOuts.forEach(moveOut -> {
                StockMoveConverter.convert(moveOut);
            });
        }

        return moveOuts;
    }

    public StockMoveOutXLSXParser(String bufferPath){
        this.bufferPath = bufferPath;
    }


    private class ShopeeOrderReportContentHandler extends DefaultHandler{

        private static final String SIMPLE_DATE_FORMAT = "yyyy-mm-dd";
        private static final String ESTIMATED_SHIP_OUT_DATE = "Estimated Ship Out Date";
        private static final String QUANTITY = "Quantity";
        private static final String SKU_REFERENCE_NO = "SKU Reference No.";
        private static final String ORDER_ID = "Order ID";

        private ArrayList<StockMoveOut> moveOuts;
        private StylesTable stylesTable;
        private SharedStrings sharedStringsTable;
        private Map<String,Integer> headerPosition;
        private boolean isValue;
        private dataType readingVDataType;
        private int formatIndex;
        private String formatString;
        private int readingRow = 0;
        private StockMoveOut stockMoveOut;
        private String columString;
        private StringBuilder value;
        private DataFormatter dataFormatter;
        
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if(value == null) this.value = new StringBuilder();

            if(isValue) value.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            
            String string = null;

            if("v".equals(qName)){
                switch(readingVDataType){
                    case NUMBER: 
                        if(this.formatString == null) string = value.toString();
                        else string = dataFormatter.formatRawCellContents(Float.parseFloat(string), this.formatIndex, this.formatString);
                        break;
                    case SSTINDEX:
                        String sstIndex = value.toString();
                        try {
                            XSSFRichTextString rtss = new XSSFRichTextString((CTRst) sharedStringsTable.getItemAt(Integer.parseInt(sstIndex)));
                            string = rtss.toString();
                        } catch (NumberFormatException e) {
                        }
                        break;
                    default:
                    string = "(TODO: Unexpected type: " + readingVDataType + ")";
                        break;
                }

                if(readingRow == 0 && string != null){
                    switch(string){
                        case ORDER_ID:
                            headerPosition.put(columString, 0);
                            break;
                        case SKU_REFERENCE_NO:
                            headerPosition.put(columString, 1);
                            break;
                        case QUANTITY:
                            headerPosition.put(columString, 2);
                            break;
                        case ESTIMATED_SHIP_OUT_DATE:
                            headerPosition.put(columString, 3);
                    }
                }

                Integer integer = headerPosition.get(columString);
                switch(integer){
                    case 0:
                        stockMoveOut.setOrderId(string);
                        break;
                    case 1:
                        stockMoveOut.setRelativeId(string);
                        break;
                    case 2:
                        stockMoveOut.setQuantity(Double.parseDouble(string));
                        break;
                    case 3:
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
                        try {
                            stockMoveOut.setDate(simpleDateFormat.parse(string));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            stockMoveOut.setDate(new Date(System.currentTimeMillis()));
                        }
                        break;
                }
                return;
            }

            if("row".equals(qName)){
                this.readingRow += 1;
                moveOuts.add(stockMoveOut);
                this.stockMoveOut = null;
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            
            if("v".equals(qName)) { // 'v' is a tag name that contains cell's value.
                isValue = true;
                return;
            }

            if("c".equals(qName)) { // 'c' is tag name parent node of 'v'. this is cell itself.

                int firstDigit = 0;
                String references = attributes.getValue("r"); // 'r' is reference like A1, C3.
                for(int i = 0; i < references.length(); i++){
                    if(Character.isDigit(references.charAt(i))){
                        firstDigit = i;
                        break;
                    }
                }
                this.columString = references.substring(0, firstDigit);

                readingVDataType = dataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleString = attributes.getValue("s");
                
                if("s".equals(cellType)){
                    this.readingVDataType = dataType.SSTINDEX;
                    return;
                }
                if(cellStyleString != null){
                    XSSFCellStyle style = stylesTable.getStyleAt(Integer.parseInt(cellStyleString));
                    this.formatString = style.getDataFormatString();
                    this.formatIndex = style.getDataFormat();
                    if(this.formatString == null) this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                    return;
                }

                if("row".equals(qName)){
                    this.stockMoveOut = new StockMoveOut();
                    this.stockMoveOut.setSalesChannel(ONLINE);
                }
            }
        }

        public ShopeeOrderReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
                ArrayList<StockMoveOut> moveOuts) {
            this.sharedStringsTable = sharedStrings;
            this.stylesTable = stylesTable;
            this.moveOuts = moveOuts;

            this.dataFormatter = new DataFormatter();
            headerPosition = new HashMap<String, Integer>();
        }

        // private int nameToColumn(String name) {
        //     int column = -1;
        //     for (int i = 0; i < name.length(); ++i) {
        //         int c = name.charAt(i);
        //         column = (column + 1) * 26 + c - 'A';
        //     }
        //     return column;
        // }
        
    }
}
