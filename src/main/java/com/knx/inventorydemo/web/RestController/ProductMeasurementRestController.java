package com.knx.inventorydemo.web.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.entity.ProductMeasurement;

@RestController
@RequestMapping("/api/measurement")
public class ProductMeasurementRestController {

    @Autowired
    MeasurementService measurementService;
    
    @GetMapping(path = "/")
    public List<ProductMeasurement> getMeasurement(@RequestParam(name = "product_id", required = true) String productId,
        @RequestParam(name = "relative_id", required = false) String relativeId){

        LinkedList<ProductMeasurement> returnList = new LinkedList<ProductMeasurement>();
        
        if(relativeId != null){
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(relativeId);
            Map<String, ProductMeasurement> productMeasByRelativeIds = measurementService.getProductMeasByRelativeIds(arrayList);
            returnList.add(productMeasByRelativeIds.get(relativeId));
            return returnList;
        }
        List<ProductMeasurement> measByProductIds = measurementService.findAllCustomMeasurementByProductId(productId);
        returnList.addAll(measByProductIds);
        return returnList;
    }

    @PutMapping(path = "/")
    public void createMeasurement(@RequestBody ProductMeasurement measurement){
        
    }
}
