package com.knx.inventorydemo.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.exception.ProductUnactivityException;
import com.knx.inventorydemo.mapper.ProductMovementMapper;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

public class StockingService{

    static Logger logger = LoggerFactory.getLogger(StockingService.class);

    private ProductStockingMapper pStockingMapper;
    private ProductMovementMapper pMovementMapper;
    private MeasurementService measurementService;
    private ProductService productService;

    private BlockingQueue<ProductMovement> pendingMovements;

    public boolean updateToRepository(){

        if(pendingMovements.isEmpty()) return false;
        LinkedList<ProductMovement> beingMovements = new LinkedList<ProductMovement>();
        pendingMovements.drainTo(beingMovements);

        //insert all being movement to movement table
        //verify any movement not duplicate in record. if duplicate check any change on the movement, update once.
        // if any update is about quantity or used uom change. figure down different then adding to being movement,
        // also remove previous movement has been checked got updated.

        pMovementMapper.bulkGetMoveOutByOrderIdsAndProductIds(beingMovements);


        // replace all movement's used measurement to origin measurement that product meta matched.
        // continue update the product meta matched stocking table.

        HashMap<String, Map<String, ProductMeasurement>> originMeasurement = this.pullOriginMeasurement(beingMovements);

        ProductMovement polledMoves = beingMovements.poll();
        // getting hashMap by sales channel continue getting by product measurement relative id.
        ProductMeasurement measurement = originMeasurement.get(polledMoves.getSalesChannel())
            .get(polledMoves.getProductId() + "-" + polledMoves.getUsedUOM());
        polledMoves.setQuantity(polledMoves.getQuantity() * measurement.getMeasurement());

        return true;
    }

    /**
     * @param movement ProductMovement, push movement to pendings movement.
     * @param skipCheck this using to reduce resource of query, free up checking when already checking by before.
     * @return pushing result. return true when success.
     */
    private boolean pushMovement(ProductMovement movement, boolean skipCheck){

        // TODO checking movement's product id activity.
        LinkedList<String> linkedList = new LinkedList<String>();
        linkedList.add(movement.getProductId());
        List<String> unactivityList = null;
        if(!skipCheck) unactivityList = productService.getProductUnactivity(linkedList);

        if(unactivityList == null || unactivityList.isEmpty()) pendingMovements.add(movement);
        if(unactivityList != null && unactivityList.get(0).equals(movement.getProductId())) return false;
        return true;
    }

    public boolean pushMovement(ProductMovement movement){
        return this.pushMovement(movement, false);
    }

    /**
     * @param order
     * @return
     */
    public List<ProductMovement> pushMovement(Order order){
        if(order == null || !order.hasMovement()) { throw new NullPointerException("order is null or emptry."); }

        LinkedList<String> toCheckingList = new LinkedList<String>();
        Iterator<ProductMovement> checkingIterator = order.getMovements().iterator();
        while(checkingIterator.hasNext()){
            toCheckingList.add(checkingIterator.next().getProductId());
        }
        List<String> unactivityList = productService.getProductUnactivity(toCheckingList);

        // throw exception return to caller and give a list of unactitvity product to exception member that has given following.
        if(unactivityList != null && !unactivityList.isEmpty()){
            ProductUnactivityException exception = new ProductUnactivityException("checked unactivity product has" + unactivityList.toString());
            exception.setUnactivityProductList(unactivityList);
            throw exception;
        }

        LinkedList<ProductMovement> unablePushList = new LinkedList<ProductMovement>();
        Iterator<ProductMovement> iterator = order.getMovements().iterator();
        while(iterator.hasNext()){
            ProductMovement moves = iterator.next();
            boolean added = this.pushMovement(moves, true);
            if(!added) {
                unablePushList.add(moves);
            }
        }
        order.setAnalysed(true);
        return unablePushList.isEmpty() ? null : unablePushList;
    }

    public Map<Order, List<String>> pushMovement(List<Order> orders){
        if(orders == null || orders.isEmpty()) throw new NullPointerException("orders is null or emptry");

        HashMap<Order, List<String>> unablePushOrders = new HashMap<Order, List<String>>();

        // a list of unable insert to pending cause by contains product is not activity. reject certain order to verify.
        List<Order> unInsertOrders = new LinkedList<Order>();

        for(Order order : orders){

            List<String> unactivityProducts = null;

            try{
                this.pushMovement(order);
            } catch(ProductUnactivityException e){ 
                unactivityProducts = e.getUnactivityProductList(); 
            }

            if(unactivityProducts != null){
                unInsertOrders.add(order);
                unablePushOrders.put(order, unactivityProducts);
            }
        }

        return unablePushOrders.isEmpty() ? null : unablePushOrders;
    }

    public List<ProductMovement> fetchMovesQueueMovementByRelativeId(List<String> relativeIds){
        if(relativeIds == null || relativeIds.isEmpty()) throw new NullPointerException(); // give a message.

        if(pendingMovements.isEmpty()) return null;

        LinkedList<ProductMovement> movementList = new LinkedList<ProductMovement>();
        Object[] array = pendingMovements.toArray();
        for(Object o : array){
            if(o instanceof StockMoveOut) {
                StockMoveOut movesMovement = (StockMoveOut) o;
                movementList.add(movesMovement);
            }
        }
        
        movementList.sort(new Comparator<ProductMovement>() {
            @Override
            public int compare(ProductMovement o1, ProductMovement o2) {

                if(o1.getRelativeId() == o2.getRelativeId()) return 0;

                return o1.getRelativeId().compareToIgnoreCase(o2.getRelativeId());
            }
        });

        relativeIds.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                return o1.compareToIgnoreCase(o2);
            }
        });

        logger.info("movementList is :" + movementList.toString());
        logger.info("relativeIds is :" + relativeIds.toString());

        LinkedList<ProductMovement> linkedList = new LinkedList<ProductMovement>();
        int lastFoundIndex = 0;
        int relativeIdsIndex = 0;
        int movementIndex = 0;
        for( ; relativeIdsIndex < relativeIds.size(); relativeIdsIndex++){

            if(movementList.get(movementIndex).getRelativeId() == relativeIds.get(relativeIdsIndex)){
                linkedList.add(movementList.get(movementIndex));

                lastFoundIndex = movementIndex;

                if((relativeIdsIndex + 1) != relativeIds.size()) {
                    if(relativeIds.get(relativeIdsIndex + 1) != relativeIds.get(relativeIdsIndex)){
                        movementIndex++;
                    } else {
                        continue;
                    }
                } else {
                    movementIndex++;
                }
            }

            for( ; movementIndex < movementList.size(); movementIndex++){
                if(movementList.get(movementIndex).getRelativeId() == relativeIds.get(relativeIdsIndex)){
                    linkedList.add(movementList.get(movementIndex));
    
                    lastFoundIndex = movementIndex;

                    if((movementIndex + 1) < movementList.size() &&
                        movementList.get(movementIndex + 1) != movementList.get(movementIndex)){

                        continue;
                    }
                    break;
                }
            }

            if(movementIndex >= movementList.size()) {
                movementIndex = lastFoundIndex;
            }
            
        }

        return linkedList;
    }

    public void clearPushedMovements() {
        this.pendingMovements.clear();
    }

    /**
     * this method getting origin measurement of product meta by specify measurement's relativeId of ProdutMovement. 
     * 
     * @param movements List of ProductMovement
     * @return Map key by sales channel, value is Map contains Measurement key by measurement's relative id such as "9968-unit", Map<SalesChannel, Map<RelativeId, ProductMeasurement>>
     */
    public HashMap<String, Map<String, ProductMeasurement>> pullOriginMeasurement(List<ProductMovement> movements){

        if(movements == null || movements.isEmpty()) throw new NullPointerException("movements is null or empty");

        HashMap<String, List<String>> channelMovementMap = new HashMap<String, List<String>>();
        Set<String> channelKeySet = channelMovementMap.keySet();
        HashMap<String, Map<String, ProductMeasurement>> resultMovementMap = new HashMap<String, Map<String, ProductMeasurement>>();

        for(ProductMovement movement : movements){
            if(!channelKeySet.contains(movement.getSalesChannel())){
                channelMovementMap.put(movement.getSalesChannel(), new ArrayList<String>());
            }

            List<String> relativeIds = channelMovementMap.get(movement.getSalesChannel());
            if(!relativeIds.contains(movement.getRelativeId())){
                String relativeId = movement.getRelativeId();
                if(!relativeId.contains("-")) relativeId = relativeId + "-" + movement.getUsedUOM();
                relativeIds.add(relativeId);
            }
        }

        for(String salesChannel : channelKeySet){
            List<String> relativeIds = channelMovementMap.get(salesChannel);
            Map<String, ProductMeasurement> resultMap = measurementService.getProductMeasByRelativeIdWithChannel(relativeIds, salesChannel);
            resultMovementMap.put(salesChannel, resultMap);
        }

        return resultMovementMap;
    }

    public StockingService(){
        pendingMovements = new PriorityBlockingQueue<>();
    }

    public StockingService(ProductStockingMapper productStockingMapper, ProductMovementMapper productMovementMapper, MeasurementService measurementService){
        this.measurementService = measurementService;
        this.pMovementMapper = productMovementMapper;
        this.pStockingMapper = productStockingMapper;
        this.pendingMovements = new PriorityBlockingQueue<>();
    }

    public void init() {
        //TODO:
    }

    public List<ProductMovement> getAllMoveRecord(String productId) {
        return null;
    }
    
}