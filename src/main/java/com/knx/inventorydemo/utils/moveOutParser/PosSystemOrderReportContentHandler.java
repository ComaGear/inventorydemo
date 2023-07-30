package com.knx.inventorydemo.utils.moveOutParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.utils.moveOutParser.StockMoveOutXLSXParser.dataType;

public class PosSystemOrderReportContentHandler extends DefaultHandler{

        private static final String SIMPLE_DATE_FORMAT = "dd/MM/yyyy";
        // private static final String INTERNAL_DATE_FORMAT = "yyyy-mm-dd";
        private static final String DOCS_DATE = "Docs Date";
        private static final String DOCS_ID = "Docs ID";
        private static final String ITEM_NO = "Item No";
        private static final String UOM = "UOM";
        private static final  String salesChannel = "MERCHANT";
        private static final String QUANTITY = "Quantity";

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
        private SimpleDateFormat simpleDateFormat;

        // private SimpleDateFormat internalDateFormat;

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
                        else string = dataFormatter.formatRawCellContents(Float.parseFloat(value.toString()), this.formatIndex, this.formatString);
                        break;
                    case SSTINDEX:
                        String sstIndex = value.toString();
                        try {
                            RichTextString rts = sharedStringsTable.getItemAt(Integer.parseInt(sstIndex));
                            string = rts.toString();
                        } catch (NumberFormatException e) {
                        }
                        break;
                    default:
                    string = "(TODO: Unexpected type: " + readingVDataType + ")";
                        break;
                }

                if(readingRow == 0 && string != null){
                    switch(string){
                        case DOCS_ID:
                            headerPosition.put(columString, 0);
                            break;
                        case ITEM_NO:
                            headerPosition.put(columString, 1);
                            break;
                        case QUANTITY:
                            headerPosition.put(columString, 2);
                            break;
                        case DOCS_DATE:
                            headerPosition.put(columString, 3);
                            break;
                        case UOM:
                            headerPosition.put(columString, 4);
                            break;
                    }
                    return;
                }


                Integer integer = -1;
                if(headerPosition.containsKey(columString)) integer = headerPosition.get(columString);
                switch(integer){
                    case 0:
                        stockMoveOut.setOrderId(string);
                        break;
                    case 1:
                        stockMoveOut.setProductId(string);
                        break;
                    case 2:
                        stockMoveOut.setQuantity(Double.parseDouble(string));
                        break;
                    case 3:
                        try {
                            stockMoveOut.setDate(simpleDateFormat.parse(string));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            stockMoveOut.setDate(new Date(System.currentTimeMillis()));
                        }
                        break;
                    case 4:
                        stockMoveOut.setUsedUOM(string);
                    default :
                        break;
                }
                return;
            }

            if("row".equals(qName)){
                if(readingRow > 0) moveOuts.add(stockMoveOut);
                this.readingRow += 1;
                this.stockMoveOut = null;
            }

            if(value != null && value.length() > 0) value.delete(0, value.length());

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

            }
            if("row".equals(qName)){
                if(readingRow == 0) return;
                this.stockMoveOut = new StockMoveOut();
                this.stockMoveOut.setSalesChannel(salesChannel);
            }
        }

        public PosSystemOrderReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
                ArrayList<StockMoveOut> moveOuts) {
            this.sharedStringsTable = sharedStrings;
            this.stylesTable = stylesTable;
            this.moveOuts = moveOuts;

            this.dataFormatter = new DataFormatter();
            headerPosition = new HashMap<String, Integer>();

            this.simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            // this.internalDateFormat = new SimpleDateFormat(INTERNAL_DATE_FORMAT);
        }

    }
