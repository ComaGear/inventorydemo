package com.knx.inventorydemo.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.entity.Stocking;
import com.knx.inventorydemo.exception.ProductUnactivityException;
import com.knx.inventorydemo.mapper.ProductMovementMapper;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

public class StockingService{

    private static final String ENSURE_STOCKING_MOVEMENTS = "Ensure Stocking Movements";
    private static final String ENSURE_STOCKING_MAP = "Ensure Stocking Map";

    static Logger logger = LoggerFactory.getLogger(StockingService.class);

    private ProductStockingMapper pStockingMapper;
    private ProductMovementMapper pMovementMapper;
    private MeasurementService measurementService;
    private ProductService productService;

    private BlockingQueue<ProductMovement> pendingMovements;

    public boolean updateToRepository(){

        if(pendingMovements.isEmpty()) return false;
        List<ProductMovement> beingMovements = new LinkedList<ProductMovement>();
        pendingMovements.drainTo(beingMovements);

        List<ProductMovement> ensureStockingMovements = new LinkedList<ProductMovement>();
        HashMap<String, Double> ensureStockingMap = new HashMap<String, Double>();

        //if any order's id exist in record. jump to identifyOrder
        Iterator<ProductMovement> iterator = beingMovements.iterator();
        List<StockMoveOut> moveOuts = new LinkedList<StockMoveOut>();
        List<StockMoveIn> moveIns = new LinkedList<StockMoveIn>();
        while(iterator.hasNext()){
            ProductMovement next = iterator.next();
            if(next instanceof StockMoveOut){
                StockMoveOut moveOut = (StockMoveOut) next;
                moveOuts.add(moveOut);
            }
            if(next instanceof StockMoveIn){
                StockMoveIn moveIn = (StockMoveIn) next;
                moveIns.add(moveIn);
            }
        }

        HashMap<String, Object> resultMap = this.stockMoveOutUpdateToRepository(beingMovements, moveOuts);
        // TODO turn returned stockingMap and movements to negative Stocking.
        this.putWithAdding((Map<String, Double>) resultMap.get(ENSURE_STOCKING_MAP), ensureStockingMap);
        ensureStockingMovements.addAll((List<ProductMovement>) resultMap.get(ENSURE_STOCKING_MOVEMENTS));
        
        resultMap = this.stockMoveInUpdateToRepository(beingMovements, moveIns);
        this.putWithAdding((Map<String, Double>) resultMap.get(ENSURE_STOCKING_MAP), ensureStockingMap);
        ensureStockingMovements.addAll((List<ProductMovement>) resultMap.get(ENSURE_STOCKING_MOVEMENTS));

        // ensureStockingMovments turn stocking state and tehen put it to ensureStockingMap
        for(ProductMovement ensureMovement : ensureStockingMovements){
            if(ensureStockingMap.containsKey(ensureMovement.getRelativeId())){
                Double stockDouble = ensureStockingMap.get(ensureMovement.getRelativeId());
                stockDouble += ensureMovement.getQuantity();
                ensureStockingMap.put(ensureMovement.getRelativeId(), stockDouble);
            } else {
                ensureStockingMap.put(ensureMovement.getRelativeId(), ensureMovement.getQuantity());
            }
        }

        // turn ensureStockingMap to product's measurement to origin matched to product meta.
        // ensureStockingMap is key by product measurement relative id. use pullOriginMeasurement() to turn it off.
        Set<String> keySet = ensureStockingMap.keySet();
        List<String> relativeIds = new LinkedList<String>();
        for(String key : keySet) relativeIds.add(key);
        Map<String, ProductMeasurement> measByRelativeIds = this.pullOriginMeasurementByRelativeIds(relativeIds);
        
        Iterator<String> keyIterator = ensureStockingMap.keySet().iterator();
        Map<String, Stocking> stockingsMap = new HashMap<String, Stocking>();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double ensureStockingDouble = ensureStockingMap.get(key) * measByRelativeIds.get(key).getMeasurement();
            
            String productId = measByRelativeIds.get(key).getProductId();
            if(stockingsMap.containsKey(productId))
                stockingsMap.get(productId).addQuantity(ensureStockingDouble);
            else
                stockingsMap.put(productId, new Stocking(productId, ensureStockingDouble));
        }

        //turn stockings from map to list.
        List<Stocking> stockings = new LinkedList<Stocking>();
        Iterator<String> stockingsMapIterator = stockingsMap.keySet().iterator();
        while(stockingsMapIterator.hasNext()) {
            Stocking stocking = stockingsMap.get(stockingsMapIterator.next());
            stockings.add(stocking);
        }

        // finally step is udpate stocking to repository 
        int updates = pStockingMapper.updateStockingOnHold(stockings);

        return updates > 0;
    }

    private void putWithAdding(Map<String, Double> toPutMap, HashMap<String, Double> ensureStockingMap) {
        Iterator<String> iterator = toPutMap.keySet().iterator();
        while(iterator.hasNext()){
            String next = iterator.next();
            if(ensureStockingMap.containsKey(next)){
                Double double1 = ensureStockingMap.get(next);
                double1 += toPutMap.get(next);
                ensureStockingMap.put(next, double1);
            } else {
                ensureStockingMap.put(next, toPutMap.get(next));
            }
        }
    }

    /**
     * this method use to find up a beingMovements's move has not contained in orderIdSet from the repository.
     * 
     * @param OrderIdSet a set of order'ids has exists in repository being insert for.
     * @param beingMovements a list of source movements being insert to repository.
     * @return a list of ProductMovement not contained in OrderIdSet.
     */
    private List<ProductMovement> fetchNotExistsRecordForOrderId(List<String> OrderIdSet, LinkedList<ProductMovement> beingMovements) {

        LinkedList<ProductMovement> notExistRecordMovements = new LinkedList<ProductMovement>();
        HashSet<String> notExistOrderIds = new HashSet<String>();

        for(ProductMovement moves: beingMovements){
            if(moves instanceof StockMoveOut){
                StockMoveOut moveOut = (StockMoveOut) moves;

                if(notExistOrderIds.contains(moveOut.getOrderId()) || !OrderIdSet.contains(moveOut.getOrderId()) ){
                    notExistRecordMovements.add(moveOut);
                    notExistOrderIds.add(moveOut.getOrderId());
                }
            }
        }
        return notExistRecordMovements;
    }

    private HashMap<String, Object> stockMoveOutUpdateToRepository(List<ProductMovement> beingMovements, List<StockMoveOut> beingMoveOuts){
        List<String> uniqueOrderIds = new LinkedList<String>();
        List<StockMoveOut> ensureStockingMoveOuts = new LinkedList<>();
        Map<String, Double> ensureStockingMap = new HashMap<String, Double>(); // key by product relative id.

        Iterator<StockMoveOut> iterator = beingMoveOuts.iterator();
        while(iterator.hasNext()){
            StockMoveOut next = iterator.next();
            if(!uniqueOrderIds.contains(next.getOrderId())) uniqueOrderIds.add(next.getOrderId());
        }

        // put all new order identify by order's id from repository to ensureStockingMovements, then remove from beingMovement. 
        for(StockMoveOut moveOut : beingMoveOuts){
            if(!uniqueOrderIds.contains(moveOut.getOrderId())) uniqueOrderIds.add(moveOut.getOrderId());
        }

        List<String> existsOrderIds = pMovementMapper.getExistsOrderIds(uniqueOrderIds);
        for(StockMoveOut moveOut : beingMoveOuts){
            if(!existsOrderIds.contains(moveOut.getOrderId())){
                logger.debug(String.format("repository has not contains this moveOut's orderId by %s, relativeId is %s" ,
                     moveOut.getOrderId(), moveOut.getRelativeId()));
                ensureStockingMoveOuts.add(moveOut);
                beingMovements.remove(moveOut);
                beingMoveOuts.remove(moveOut);
            }
        }

        
        List<StockMoveOut> repositoryMoveOuts = new LinkedList<StockMoveOut>(); // get repository's order from mapper.

        // sorting StockMoveOut first priority by orderId, then second priority is product's relative id (productId + UOM).
        Comparator<StockMoveOut> comparator = new Comparator<StockMoveOut>() {

            @Override
            public int compare(StockMoveOut o1, StockMoveOut o2) {
                if(o2 == null || o2.getOrderId().isEmpty()) return -1;
                if(o1 == null || o1.getOrderId().isEmpty()) return 1;

                if(o1.getOrderId().compareTo(o2.getOrderId()) == 0){

                    String o1RelativeId = null;
                    if(o1.getRelativeId() == null || o1.getRelativeId().isEmpty()) // || !o1.getRelativeId().contains("-")
                        o1RelativeId = o1.getProductId() + "-" + o1.getUsedUOM();
                    else o1RelativeId = o1.getRelativeId();

                    String o2RelativeId = null;
                    if(o2.getRelativeId() == null || o2.getRelativeId().isEmpty()) // || !o2.getRelativeId().contains("-")
                        o2RelativeId = o2.getProductId() + "-" + o2.getUsedUOM();
                    else o2RelativeId = o2.getRelativeId();

                    return o1RelativeId.compareTo(o2RelativeId);
                }

                return o1.getOrderId().compareTo(o2.getOrderId());
            }
        };
        beingMoveOuts.sort(comparator);
        repositoryMoveOuts.sort(comparator);
        
        // use multi-multi iterator, iterate beingMoveOut and repositoryMoveOuts
        // if find the matched StockMoveOut is not any changed. remove from both beingMovements, beingMoveOuts, repositoryMoveOuts
        // if got update, update to repository's productMovement by bulkUpdate, and figure down quantity put to EnsureStockingList.
        //      remove from beingMovement, also repositoryMoveOuts.
        Iterator<StockMoveOut> repositoryIterator = repositoryMoveOuts.iterator();
        Iterator<StockMoveOut> beingIterator = beingMoveOuts.iterator();
        StockMoveOut rMoveOut = null;
        StockMoveOut beingMoveOut = null;
        boolean first = false;
        if(repositoryIterator.hasNext()){
            rMoveOut = repositoryIterator.next();
            beingMoveOut = beingIterator.next();
            first = true;
        }

        // a list to be udpate repository's record of find has record before.
        List<StockMoveOut> toUpdateMoveOuts = new LinkedList<StockMoveOut>();

        while(repositoryIterator.hasNext() || first){
            if(beingMoveOut == null || rMoveOut == null) return null;
            first = false;
            int compare =  beingMoveOut.compareTo(rMoveOut);
            
            switch(compare){
                case -1: // smallest than
                    beingMoveOut = beingIterator.next();
                    break;
                case 1:
                    rMoveOut = repositoryIterator.next();
                    break;
                case 0:
                    double beingQuantity = beingMoveOut.getQuantity();
                    double rQuantity = rMoveOut.getQuantity();
                    double stocking = beingQuantity - rQuantity;
                    String format = new DecimalFormat("#########.####").format(stocking);
                    if(Double.valueOf(format) != 0.0000d){
                        if(!ensureStockingMap.containsKey(beingMoveOut.getRelativeId()))
                            ensureStockingMap.put(beingMoveOut.getRelativeId(), stocking);
                        else {
                            double double1 = ensureStockingMap.get(beingMoveOut.getRelativeId());
                            double d = double1 + stocking;
                            ensureStockingMap.put(beingMoveOut.getRelativeId(), d);
                        }
                        toUpdateMoveOuts.add(beingMoveOut);
                    }

                    beingMoveOuts.remove(beingMoveOut);
                    beingMovements.remove(beingMoveOut);
                    repositoryMoveOuts.remove(rMoveOut);
                    beingMoveOut = beingIterator.next();
                    rMoveOut = repositoryIterator.next();

                    break;
            }
        }
        pMovementMapper.bulkUpdateMoveOut(toUpdateMoveOuts);
        
        // since any duplicate movements and updated movememnts is remove from both beingMoveOuts and repositoryMoveOuts.
        // iterate beingMoveOuts a new movement by orders. insert to repository movement and put it to ensureStockingMovements, remove
        //      from beingMoveOuts
        List<StockMoveOut> newMoveOuts = new LinkedList<StockMoveOut>();
        for(StockMoveOut moveOut: beingMoveOuts){
            ensureStockingMoveOuts.add(moveOut);
            beingMovements.remove(moveOut);
            beingMoveOuts.remove(moveOut);
            newMoveOuts.add(moveOut);
        }
        pMovementMapper.bulkInsertMoveOut(newMoveOuts);

        // iterate repositoryMoveOuts remains to be delete movements. figure down quantity return to Stocking, putting return quantity
        //      ensureStockingList, remove from repositoryMoveOuts.
        List<StockMoveOut> toDeleteMoveOuts = new LinkedList<StockMoveOut>();
        for(StockMoveOut moveOut : repositoryMoveOuts){
            if(ensureStockingMap.containsKey(moveOut.getRelativeId())){
                Double double1 = ensureStockingMap.get(moveOut.getRelativeId());
                double1 -= moveOut.getQuantity();
                ensureStockingMap.put(moveOut.getRelativeId(), double1);
            } else
                ensureStockingMap.put(moveOut.getRelativeId(), -moveOut.getQuantity());
            
            repositoryMoveOuts.remove(moveOut);
            toDeleteMoveOuts.add(moveOut);
        }

        // finally step, return both ensureStockingMovements and ensureStockingList.=
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(ENSURE_STOCKING_MAP, ensureStockingMap);
        resultMap.put(ENSURE_STOCKING_MOVEMENTS, ensureStockingMoveOuts);
        return resultMap;
    }

    private HashMap<String, Object> stockMoveInUpdateToRepository(List<ProductMovement> beingMovements, List<StockMoveIn> moveIns){
        // similarly to stockMoveOutUpdateToRepository. but use MoveIn as parameter.
        return null;
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
                    if(relativeIds.get(relativeIdsIndex + 1) != relativeIds.get(relativeIdsIndex))
                        movementIndex++;
                    else 
                        continue;
                } else movementIndex++;
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
            if(movementIndex >= movementList.size()) movementIndex = lastFoundIndex;
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

    public Map<String, ProductMeasurement> pullOriginMeasurementByRelativeIds(List<String> relativeIds){
        Map<String, ProductMeasurement> productMeasByRelativeIds = measurementService.getProductMeasByRelativeIds(relativeIds);
        return productMeasByRelativeIds;
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