package com.knx.inventorydemo.web.restController;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.Service.StockMovementService;
import com.knx.inventorydemo.Service.StockingService;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.entity.ProductUOM;
import com.knx.inventorydemo.entity.Vendor;
import com.knx.inventorydemo.web.exceptions.NoSuchProductException;
import com.knx.inventorydemo.web.restController.entity.ProductMetaMeasurementsDTO;

@RestController
@RequestMapping("/api/product")
public class ProductRestController {

    @Autowired
    ProductService productService;

    @Autowired
    MeasurementService measurementService;

    @Autowired
    StockMovementService stockMovementService;
    
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getProduct(@PathVariable String id){

        // ObjectNode node = mapper.createObjectNode();

        // HashMap<String, ProductMeta> hashMap = new HashMap<String, ProductMeta>();

        // hashMap.put("9971", new ProductMeta().setId("9971").setName("Apollo Cake Chocolate 24pcs"));
        // hashMap.put("9972", new ProductMeta().setId("9972").setName("Apollo Cake Original 24pcs"));
        // hashMap.put("8891", new ProductMeta().setId("8891").setName("Nabati Strawberry"));
        // hashMap.put("9991", new ProductMeta().setId("9991").setName("No30gg Star 60pcs"));

        // HashMap<String, String> returnMap = new HashMap<String, String>();
        
        // if(hashMap.containsKey(id)) {
        //     returnMap.put("id", hashMap.get(id).getId());
        //     returnMap.put("name", hashMap.get(id).getName());
        // }

        // return !returnMap.isEmpty() ? returnMap : null;

        ProductMeta productMetaById = productService.getProductMetaById(id);
        List<ProductMeasurement> measurements = measurementService.findAllCustomMeasurementByProductId(productMetaById.getId());
        ProductMetaMeasurementsDTO productMetaMeasurementsDTO = new ProductMetaMeasurementsDTO(productMetaById);
        productMetaMeasurementsDTO.setMeasurements(measurements);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(productMetaMeasurementsDTO);
    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createProduct(@RequestBody ProductMetaMeasurementsDTO productMetaMeasurements){

        ProductMeta meta = productMetaMeasurements;

        if(productService.getProductMetaById(meta.getId()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("product id existed!");
        }

        if(productMetaMeasurements.getMeasurements() == null || productMetaMeasurements.getMeasurements().isEmpty()){
            productService.addNewProduct(meta);
            ProductMeta repositoryProductMeta = productService.getProductMetaById(meta.getId());
            ProductMetaMeasurementsDTO repositoryProductMetaMeasurementDto = new ProductMetaMeasurementsDTO(repositoryProductMeta);
            List<ProductMeasurement> repositoryMeasurements = measurementService.findAllCustomMeasurementByProductId(repositoryProductMetaMeasurementDto.getId());
            repositoryProductMetaMeasurementDto.setMeasurements(repositoryMeasurements);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(repositoryProductMetaMeasurementDto);
        }

        // getting default relative id set by user.
        String defaultRelativeUomId = productMetaMeasurements.getDefaultUom();
        List<ProductMeasurement> measurements = productMetaMeasurements.getMeasurements();
        ProductMeasurement defaultProductMeasurement = null;
        for(ProductMeasurement measure : measurements){
            measure.setSalesChannel(ProductUOM.LAYER); // TODO : temporary fix problem. please looking back design notebook how better improve it.A
            if(measure.getRelativeId().equals(defaultRelativeUomId)) defaultProductMeasurement = measure;
        }
        productService.addNewProduct(meta, defaultProductMeasurement);

        ProductMeta repositoryProductMeta = productService.getProductMetaById(meta.getId());
        ProductMetaMeasurementsDTO repositoryProductMetaMeasurementDto = new ProductMetaMeasurementsDTO(repositoryProductMeta);
        List<ProductMeasurement> repositoryMeasurements = measurementService.findAllCustomMeasurementByProductId(repositoryProductMetaMeasurementDto.getId());
        repositoryProductMetaMeasurementDto.setMeasurements(repositoryMeasurements);
        
        // return ResponseEntity.ok(repositoryProductMetaMeasurementDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repositoryProductMetaMeasurementDto);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity updateProduct(@PathVariable String id, @RequestBody ProductMetaMeasurementsDTO productMetaMeasurementsDTO){
        
        ProductMeta meta = productMetaMeasurementsDTO;

        List<ProductMeasurement> measurements = productMetaMeasurementsDTO.getMeasurements();
        ArrayList<ProductMeasurement> repositoryMeasurements = new ArrayList<>(measurementService.findAllCustomMeasurementByProductId(id));
        Comparator<ProductMeasurement> comparator = new Comparator<ProductMeasurement>() {
            @Override
            public int compare(ProductMeasurement o1, ProductMeasurement o2) {
                return o1.getRelativeId().compareTo(o2.getRelativeId());
            }
        };
        measurements.sort(comparator);
        repositoryMeasurements.sort(comparator);

        ArrayList<ProductMeasurement> newMeasurements = new ArrayList<ProductMeasurement>();
        ArrayList<ProductMeasurement> toDeleteMeasurements = new ArrayList<ProductMeasurement>();
        ArrayList<String> toDeleterelativeId = new ArrayList<String>();
        ArrayList<ProductMeasurement> toUpdateMeasurements = new ArrayList<ProductMeasurement>();


        Iterator<ProductMeasurement> repositoryMeasurementsIterator = repositoryMeasurements.iterator();
        ProductMeasurement repositoryMeas = null;
        if(repositoryMeasurementsIterator.hasNext()) repositoryMeas = repositoryMeasurementsIterator.next();
        for(ProductMeasurement meas : measurements){
            int compare = meas.getRelativeId().compareTo(repositoryMeas.getRelativeId());
            System.out.println(meas.getRelativeId());
            System.out.println(repositoryMeas.getRelativeId());
            System.out.println(compare);
            if(compare > 0) {
                newMeasurements.add(meas);
                continue;
            }
            if(compare < 0) {
                toDeleteMeasurements.add(meas);
                toDeleterelativeId.add(meas.getRelativeId());
                if(repositoryMeasurementsIterator.hasNext()) repositoryMeas = repositoryMeasurementsIterator.next();
                continue;
            }
            toUpdateMeasurements.add(meas);
            if(repositoryMeasurementsIterator.hasNext()) repositoryMeas = repositoryMeasurementsIterator.next();
        }

        boolean passDelete = toDeleteMeasurements.isEmpty();
        if(!passDelete){
            List<String> rejectDeleteMeasList = new ArrayList<String>();
            JSONArray errorMeasurementJsonArray = new JSONArray();

            Map<String, Boolean> hasMovementRecord = stockMovementService.hasMovementRecord(toDeleterelativeId);
            Set<String> keySet = hasMovementRecord.keySet();
            for(String key : keySet) {
                if(hasMovementRecord.get(key) == true) {
                    rejectDeleteMeasList.add(key);
                    errorMeasurementJsonArray.put(key);
                    toDeleteMeasurements.removeIf(measurement -> {return measurement.getRelativeId().equals(key);});
                }
            }
            if(!rejectDeleteMeasList.isEmpty()){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error_name", "Measurements has sales record");
                    jsonObject.put("error", "MeasHasSalesRecord");
                    jsonObject.put("has_sales_record_measurements_relative_ids", errorMeasurementJsonArray);
                    
                } catch (JSONException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getCause());
                }

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
            }
            // toDeleteMeasurements.removeAll(rejectDeleteMeasList);
        }

        for(ProductMeasurement measurement :newMeasurements){ // TODO : Temporary fix;
            measurement.setSalesChannel(ProductUOM.LAYER);
        }


        productService.update(meta);
        for(ProductMeasurement meas : newMeasurements){
            measurementService.addNewMeasurementToProduct(meta, meas);
        }
        for(ProductMeasurement meas : toUpdateMeasurements){
            measurementService.updateByRelativeId(meas, meas.getRelativeId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productMetaMeasurementsDTO);

    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteProduct(@PathVariable String id){
        if(id == null || id.equals("") || id.isEmpty()) throw new NullPointerException("path variable 'id' must valid.");

        ProductMeta repositoryProductMeta = productService.getProductMetaById(id);
        if(repositoryProductMeta == null) throw new NoSuchProductException(id).setMessage("Product :" + id + " don't existed.");

        List<ProductMeasurement> measurements = measurementService.findAllCustomMeasurementByProductId(id);

        ArrayList<String> measureRelativeIds = new ArrayList<String>(measurements.size());
        for(ProductMeasurement measurement : measurements){
            measureRelativeIds.add(measurement.getRelativeId());
        }

        boolean rejectDelete = false;
        Map<String, Boolean> hasMovementRecord = stockMovementService.hasMovementRecord(measureRelativeIds);
        Iterator<String> iterator = hasMovementRecord.keySet().iterator();

        JSONArray errorMeasurementJsonArray = new JSONArray();
        while(iterator.hasNext()){
            String next = iterator.next();
            if(hasMovementRecord.get(next)) {
                rejectDelete = true;
                errorMeasurementJsonArray.put(next);
            }
        }
        if(rejectDelete) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("error_name", "Measurements has sales record");
                jsonObject.put("error", "MeasHasSalesRecord");
                jsonObject.put("has_sales_record_measurements_relative_ids", errorMeasurementJsonArray);
                
            } catch (JSONException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getCause());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
        }

        productService.delete(repositoryProductMeta);

    }


    @ExceptionHandler(NoSuchProductException.class)
    public ResponseEntity handleNoSuchProductException(NoSuchProductException noSuchProductException){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("error_name", NoSuchProductException.REASON);
            jsonObject.put("error", NoSuchProductException.ERROR_ID);
            jsonObject.put("message", noSuchProductException.getMessage());
            jsonObject.put("no_such_product_id", noSuchProductException.getId());
            
        } catch (JSONException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getCause());
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject.toString());
    }

    // @ExceptionHandler(ProductValidationException.class)
    // public 
}
