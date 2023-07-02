package com.knx.inventorydemo.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.apache.poi.ss.usermodel.RichTextString;
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

public class StockMoveOutXLSXParser<T extends DefaultHandler> {

    public static final String ONLINE = "ONLINE";

    enum dataType {
        NUMBER, SSTINDEX,
    }

    private String bufferPath;

    public List<StockMoveOut> parse(String targetFileName, StockMoveConverter stockMoveConverter, Class<? extends DefaultHandler> clazz){
        String path = bufferPath + "/" + targetFileName;
        return this.parse(new File(path), stockMoveConverter, clazz);
    }

    public List<StockMoveOut> parse(File targetFile, StockMoveConverter stockMoveConverter, Class<? extends DefaultHandler> clazz){
        if(targetFile == null || !targetFile.exists()) throw new IllegalArgumentException();

        ArrayList<StockMoveOut> moveOuts = new ArrayList<StockMoveOut>();

        try {
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(targetFile));

            DefaultHandler newInstance = null;
            try {
                Constructor<? extends DefaultHandler> declaredConstructor = clazz.getDeclaredConstructor(
                    SharedStrings.class, StylesTable.class, ArrayList.class);
                newInstance = declaredConstructor.newInstance(xssfReader.getSharedStringsTable(),
                    xssfReader.getStylesTable(), moveOuts);


            } catch (Exception e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DefaultHandler contentHandler = newInstance;
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource inputSource = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(inputSource);
            
        } catch (IOException | OpenXML4JException | SAXException | ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return moveOuts; // return a empty movement list.
        }

        if(moveOuts != null && !moveOuts.isEmpty() && stockMoveConverter != null){
            stockMoveConverter.convert(moveOuts);
        }

        return moveOuts;
    }

    public StockMoveOutXLSXParser(String bufferPath){
        this.bufferPath = bufferPath;
    }
    
}
