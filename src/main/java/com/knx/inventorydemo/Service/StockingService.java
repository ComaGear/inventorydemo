package com.knx.inventorydemo.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knx.inventorydemo.entity.Order;
import com.knx.inventorydemo.entity.ProductMeasurement;
import com.knx.inventorydemo.entity.ProductMovement;
import com.knx.inventorydemo.entity.StockInDocs;
import com.knx.inventorydemo.entity.StockMoveIn;
import com.knx.inventorydemo.entity.StockMoveOut;
import com.knx.inventorydemo.entity.Stocking;
import com.knx.inventorydemo.exception.MovementValidationException;
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

    private Queue<ProductMovement> pendingMovements;

    public boolean updateToRepository(){

        if(pendingMovements.isEmpty()) return false;
        List<ProductMovement> beingMovements = new LinkedList<ProductMovement>();
        beingMovements.addAll(pendingMovements);
        pendingMovements.clear();

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

        HashMap<String, Object> resultMap = null;
        if(!moveOuts.isEmpty()){
            resultMap = this.stockMoveOutUpdateToRepository(beingMovements, moveOuts);
            this.putWithAdding((Map<String, Double>) resultMap.get(ENSURE_STOCKING_MAP), ensureStockingMap);
            List<ProductMovement> movesList = (List<ProductMovement>) resultMap.get(ENSURE_STOCKING_MOVEMENTS);
            // ensureStockingMovements.addAll((List<ProductMovement>) resultMap.get(ENSURE_STOCKING_MOVEMENTS));
            // for(ProductMovement moves : movesList) {
            //     if(moves instanceof StockMoveOut){
            //         StockMoveOut moveOut = (StockMoveOut) moves;
            //         moveOut.prepareStocking();
            //     }
            // }
            ensureStockingMovements.addAll(movesList);
        }
        
        if(!moveIns.isEmpty()){
            resultMap = this.stockMoveInUpdateToRepository(beingMovements, moveIns);
            this.putWithAdding((Map<String, Double>) resultMap.get(ENSURE_STOCKING_MAP), ensureStockingMap);
            ensureStockingMovements.addAll((List<ProductMovement>) resultMap.get(ENSURE_STOCKING_MOVEMENTS));
        }

        // ensureStockingMovments turn stocking state and then put it to ensureStockingMap
        for(ProductMovement ensureMovement : ensureStockingMovements){
            if(ensureStockingMap.containsKey(ensureMovement.getRelativeId())){
                Double stockDouble = ensureStockingMap.get(ensureMovement.getRelativeId());
                stockDouble += Stocking.prepareStocking(ensureMovement);
                ensureStockingMap.put(ensureMovement.getRelativeId(), stockDouble);
            } else {
                ensureStockingMap.put(ensureMovement.getRelativeId(), Stocking.prepareStocking(ensureMovement));
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
    private List<ProductMovement> getNotExistsRecordForOrderId(List<String> OrderIdSet, LinkedList<ProductMovement> beingMovements) {

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

    public List<Order> getOrderRecord(List<String> orderIds){
        if(orderIds == null || orderIds.isEmpty()) throw new NullPointerException();

        List<StockMoveOut> productMovements = pMovementMapper.bulkGetMoveOutByOrderIds(orderIds);

        Map<String, Order> orderMap = new HashMap<String, Order>();

        for(StockMoveOut moveOut : productMovements){
            if(orderMap.containsKey(moveOut.getOrderId())){
                orderMap.get(moveOut.getOrderId()).pushMovement(moveOut);
            } else {
                Order order = new Order().setOrderId(moveOut.getOrderId()).setChannel(moveOut.getSalesChannel())
                    .setDate(moveOut.getDate());
                order.pushMovement(moveOut);
                orderMap.put(moveOut.getOrderId(), order);
            }
        }

        List<Order> orders = new ArrayList<>(orderMap.values());
        return orders;
    }

    public List<StockInDocs> getDocsRecord(List<String> docsIds){
        if(docsIds == null || docsIds.isEmpty()) throw new NullPointerException();

        List<StockMoveIn> productMovements = pMovementMapper.bulkGetMoveInByDocsIds(docsIds);

        Map<String, StockInDocs> docsMap = new HashMap<String, StockInDocs>();

        for(StockMoveIn moveIn : productMovements){
            if(docsMap.containsKey(moveIn.getDocsId())){
                docsMap.get(moveIn.getDocsId()).pushMoveIn(moveIn);
            } else {
                StockInDocs stockInDocs = new StockInDocs().setDocsId(moveIn.getDocsId()).setDate(moveIn.getDate());
                docsMap.put(moveIn.getDocsId(), stockInDocs);
            }
        }

        List<StockInDocs> docsList = new ArrayList<>(docsMap.values());
        return docsList;
    }

    private HashMap<String, Object> stockMoveOutUpdateToRepository(List<ProductMovement> beingMovements, List<StockMoveOut> beingMoveOuts){

        if(beingMovements == null || beingMovements.isEmpty()) return null;
        if(beingMoveOuts == null || beingMoveOuts.isEmpty()) return null;

        List<String> uniqueOrderIds = new LinkedList<String>();
        List<StockMoveOut> ensureStockingMoveOuts = new LinkedList<>();
        Map<String, Double> ensureStockingMap = new HashMap<String, Double>(); // key by product relative id.

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
            }
        }
        beingMoveOuts.removeAll(ensureStockingMoveOuts);
        beingMovements.removeAll(ensureStockingMoveOuts);

        List<StockMoveOut> repositoryMoveOuts;
        if(!existsOrderIds.isEmpty())
            repositoryMoveOuts = pMovementMapper.bulkGetMoveOutByOrderIds(existsOrderIds); // get repository's order from mapper.
        else
            repositoryMoveOuts = new LinkedList<StockMoveOut>();

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
        List<StockMoveOut> toRemoveRepositoryItem = new LinkedList<StockMoveOut>();

        while(repositoryIterator.hasNext() && beingIterator.hasNext() || first ){
            if(beingMoveOut == null || rMoveOut == null) return null;
            first = false;
            int compare =  beingMoveOut.compareTo(rMoveOut);
            
            if(compare < 0) // smallest than
                beingMoveOut = beingIterator.next();
            if(compare > 0)
                rMoveOut = repositoryIterator.next();
            if(compare == 0){
                double beingQuantity = beingMoveOut.getQuantity();
                double rQuantity = rMoveOut.getQuantity();
                double stocking = rQuantity - beingQuantity;
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

                toRemoveRepositoryItem.add(rMoveOut);
                if(beingIterator.hasNext()) beingMoveOut = beingIterator.next();
                if(repositoryIterator.hasNext()) rMoveOut = repositoryIterator.next();
            }
        }
        if(toUpdateMoveOuts != null && !toUpdateMoveOuts.isEmpty()){
            pMovementMapper.bulkUpdateMoveOut(toUpdateMoveOuts);
            beingMoveOuts.removeAll(toUpdateMoveOuts);
            beingMovements.removeAll(toUpdateMoveOuts);
            repositoryMoveOuts.removeAll(toRemoveRepositoryItem);
        }
        
        // since any duplicate movements and updated movements is remove from both beingMoveOuts and repositoryMoveOuts.
        // iterate beingMoveOuts a new movement by orders. insert to repository movement and put it to ensureStockingMovements, remove
        //      from beingMoveOuts
        List<StockMoveOut> newMoveOuts = new LinkedList<StockMoveOut>();
        for(StockMoveOut moveOut: beingMoveOuts){
            ensureStockingMoveOuts.add(moveOut);
            newMoveOuts.add(moveOut);
        }
        if(!newMoveOuts.isEmpty()){
            pMovementMapper.bulkInsertMoveOut(newMoveOuts);
            beingMoveOuts.removeAll(newMoveOuts);
            beingMovements.removeAll(newMoveOuts);
        }

        // iterate repositoryMoveOuts remains toDelete movements. figure down quantity return to Stocking, putting return quantity
        //      ensureStockingList, remove from repositoryMoveOuts.
        List<StockMoveOut> toDeleteMoveOuts = new LinkedList<StockMoveOut>();
        for(StockMoveOut moveOut : repositoryMoveOuts){
            if(ensureStockingMap.containsKey(moveOut.getRelativeId())){
                Double double1 = ensureStockingMap.get(moveOut.getRelativeId());
                double1 += moveOut.getQuantity();
                ensureStockingMap.put(moveOut.getRelativeId(), double1);
            } else
                ensureStockingMap.put(moveOut.getRelativeId(), moveOut.getQuantity());
            
            toDeleteMoveOuts.add(moveOut);
        }
        if(!toDeleteMoveOuts.isEmpty()){
            pMovementMapper.bulkRemoveMoveOuts(toDeleteMoveOuts);
            repositoryMoveOuts.removeAll(toDeleteMoveOuts);
        }

        if(!ensureStockingMoveOuts.isEmpty())
            pMovementMapper.bulkInsertMoveOut(ensureStockingMoveOuts);
        
        // finally step, return both ensureStockingMovements and ensureStockingList.
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(ENSURE_STOCKING_MAP, ensureStockingMap);
        resultMap.put(ENSURE_STOCKING_MOVEMENTS, ensureStockingMoveOuts);
        return resultMap;
    }

    private HashMap<String, Object> stockMoveInUpdateToRepository(List<ProductMovement> beingMovements, List<StockMoveIn> beingMoveIns){

        if(beingMovements == null || beingMovements.isEmpty()) return null;
        if(beingMoveIns == null || beingMoveIns.isEmpty()) return null;

        // similarly to stockMoveOutUpdateToRepository. but use MoveIn as parameter.
        List<String> uniqueDocsIds = new LinkedList<String>();
        List<StockMoveIn> ensureStockingMoveIns = new LinkedList<>();
        Map<String, Double> ensureStockingMap = new HashMap<String, Double>(); // key by product relative id.

        for(StockMoveIn moveIn : beingMoveIns){
            if(!uniqueDocsIds.contains(moveIn.getDocsId())) uniqueDocsIds.add(moveIn.getDocsId());
        }

        List<String> existsDocsIds = pMovementMapper.getExistsDocsIds(uniqueDocsIds);
        for(StockMoveIn moveIn : beingMoveIns){
            if(!existsDocsIds.contains(moveIn.getDocsId())){
                logger.debug(String.format("repository has not contains this moveIn's docsId by %s, relativeId is %s" ,
                moveIn.getDocsId(), moveIn.getRelativeId()));
                ensureStockingMoveIns.add(moveIn);
            }
        }
        beingMoveIns.removeAll(ensureStockingMoveIns);
        beingMovements.removeAll(ensureStockingMoveIns);

        List<StockMoveIn> repositoryMoveIns;
        if(!existsDocsIds.isEmpty())
            repositoryMoveIns = pMovementMapper.bulkGetMoveInByDocsIds(existsDocsIds); // get repository's order from mapper.
        else
            repositoryMoveIns = new LinkedList<StockMoveIn>();

        // sorting StockMoveOut first priority by orderId, then second priority is product's relative id (productId + UOM).
        Comparator<StockMoveIn> comparator = new Comparator<StockMoveIn>() {

            @Override
            public int compare(StockMoveIn o1, StockMoveIn o2) {
                if(o2 == null || o2.getDocsId().isEmpty()) return -1;
                if(o1 == null || o1.getDocsId().isEmpty()) return 1;

                if(o1.getDocsId().compareTo(o2.getDocsId()) == 0){

                    if(o1.getItemRowOfDocs() < o2.getItemRowOfDocs()) return -1;
                    if(o1.getItemRowOfDocs() > o2.getItemRowOfDocs()) return 1;

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

                return o1.getDocsId().compareTo(o2.getDocsId());
            }
        };
        beingMoveIns.sort(comparator);
        repositoryMoveIns.sort(comparator);

        Iterator<StockMoveIn> repositoryIterator = repositoryMoveIns.iterator();
        Iterator<StockMoveIn> beingIterator = beingMoveIns.iterator();
        StockMoveIn rMoveIn = null;
        StockMoveIn beingMoveIn = null;
        boolean first = false;
        if(repositoryIterator.hasNext()){
            rMoveIn = repositoryIterator.next();
            beingMoveIn = beingIterator.next();
            first = true;
        }

        List<StockMoveIn> toUpdateMoveIns = new LinkedList<StockMoveIn>();
        LinkedList<StockMoveIn> toRemoveRepositoryItem = new LinkedList<StockMoveIn>();

        while(repositoryIterator.hasNext() && beingIterator.hasNext() || first){
            if(beingMoveIn == null || rMoveIn == null) return null;
            first = false;
            int compare =  beingMoveIn.compareTo(rMoveIn);
            
            if(compare < 0) // smallest than
                beingMoveIn = beingIterator.next();
            if(compare > 0)
                rMoveIn = repositoryIterator.next();
            if(compare == 0){
                double beingQuantity = beingMoveIn.getQuantity();
                double rQuantity = rMoveIn.getQuantity();
                double stocking = rQuantity - beingQuantity;
                String format = new DecimalFormat("#########.####").format(stocking);
                if(Double.valueOf(format) != 0.0000d){
                    if(!ensureStockingMap.containsKey(beingMoveIn.getRelativeId()))
                        ensureStockingMap.put(beingMoveIn.getRelativeId(), stocking);
                    else {
                        double double1 = ensureStockingMap.get(beingMoveIn.getRelativeId());
                        double d = double1 + stocking;
                        ensureStockingMap.put(beingMoveIn.getRelativeId(), d);
                    }
                    toUpdateMoveIns.add(beingMoveIn);
                }

                toRemoveRepositoryItem.add(rMoveIn);
                beingMoveIn = beingIterator.next();
                rMoveIn = repositoryIterator.next();
            }
        }
        if(!toUpdateMoveIns.isEmpty()){
            pMovementMapper.bulkUpdateMoveIn(toUpdateMoveIns);
            beingMoveIns.removeAll(toUpdateMoveIns);
            beingMovements.removeAll(toUpdateMoveIns);
            repositoryMoveIns.removeAll(toRemoveRepositoryItem);
        }


        List<StockMoveIn> newMoveIns = new LinkedList<StockMoveIn>();
        for(StockMoveIn moveIn: beingMoveIns){
            ensureStockingMoveIns.add(moveIn);
            newMoveIns.add(moveIn);
        }
        if(!newMoveIns.isEmpty()){
            pMovementMapper.bulkInsertMoveIn(newMoveIns);
            beingMoveIns.removeAll(newMoveIns);
            beingMovements.removeAll(newMoveIns);
        }


        List<StockMoveIn> toDeleteMoveIns = new LinkedList<StockMoveIn>();
        for(StockMoveIn moveIn : repositoryMoveIns){
            if(ensureStockingMap.containsKey(moveIn.getRelativeId())){
                Double double1 = ensureStockingMap.get(moveIn.getRelativeId());
                double1 += moveIn.getQuantity();
                ensureStockingMap.put(moveIn.getRelativeId(), double1);
            } else
                ensureStockingMap.put(moveIn.getRelativeId(), moveIn.getQuantity());
            
            toDeleteMoveIns.add(moveIn);
        }
        if(!toDeleteMoveIns.isEmpty()){
            pMovementMapper.bulkRemoveMoveIns(toDeleteMoveIns);
        }

        if(!ensureStockingMoveIns.isEmpty())
            pMovementMapper.bulkInsertMoveIn(ensureStockingMoveIns);

        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(ENSURE_STOCKING_MAP, ensureStockingMap);
        resultMap.put(ENSURE_STOCKING_MOVEMENTS, ensureStockingMoveIns);
        return resultMap;
    }

    
	public void removeMoveOuts(List<Order> toDeleteOrder) {
        if(toDeleteOrder == null || toDeleteOrder.isEmpty()) throw new NullPointerException();

        List<StockMoveOut> toDelete = new LinkedList<StockMoveOut>();
        Iterator<Order> iterator = toDeleteOrder.iterator();
        while(iterator.hasNext()){
            Order next = iterator.next();
            toDelete.addAll(next.getMovements());
        }

        pMovementMapper.bulkRemoveMoveOuts(toDelete);
	}

    public void removeMoveIns(List<StockInDocs> toDeleteDocs){
        if(toDeleteDocs == null || toDeleteDocs.isEmpty()) throw new NullPointerException();

        List<StockMoveIn> toDelete = new LinkedList<StockMoveIn>();
        Iterator<StockInDocs> iterator = toDeleteDocs.iterator();
        while(iterator.hasNext()){
            StockInDocs next = iterator.next();
            toDelete.addAll(next.getMovements());
        }

        pMovementMapper.bulkRemoveMoveIns(toDelete);
    }

    /**
     * @param movement ProductMovement, push movement to pendings movement.
     * @param skipCheck this using to reduce resource of query, free up checking when already checking by before.
     * @return pushing result. return true when success.
     */
    // private boolean pushMovement(ProductMovement movement, boolean skipCheck){

    //     LinkedList<String> linkedList = new LinkedList<String>();
    //     linkedList.add(movement.getProductId());
    //     List<String> unactivityList = null;
    //     if(!skipCheck) unactivityList = productService.getProductUnactivity(linkedList);

    //     if(unactivityList == null || unactivityList.isEmpty()) pendingMovements.add(movement);
    //     if(unactivityList != null && unactivityList.get(0).equals(movement.getProductId())) return false;
    //     return true;
    // }

    // public boolean pushMovement(ProductMovement movement){
    //     return this.pushMovement(movement, false);
    // }

    public void pushMovement(List<ProductMovement> movements) throws MovementValidationException{
    
        LinkedList<String> productIds = new LinkedList<String>();
        LinkedList<String> relativeIds = new LinkedList<String>();
        for(ProductMovement moves : movements){
            if(!productIds.contains(moves.getProductId()))
                productIds.add(moves.getProductId());
            if(!relativeIds.contains(moves.getRelativeId()))
            relativeIds.add(moves.getRelativeId());
        }
        List<String> unExistedProductIds = productService.lookupUnexistProduct(productIds);
        List<String> unActiveProductIds = productService.getProductUnactivity(productIds);
        List<String> unExistMeasurement = measurementService.lookupMeasurementExistence(relativeIds);

        List<ProductMovement> tempList = new LinkedList<ProductMovement>();
        List<String> NonValidDocsIds = new LinkedList<String>();
        for(ProductMovement moves : movements){


            // casting to sub class of productMovement obtains Order or Docs ID
            String moveDocsId = "";
            if(moves instanceof StockMoveOut){
                StockMoveOut moveOut = (StockMoveOut) moves; 
                moveDocsId = moveOut.getOrderId();
            }
            if(moves instanceof StockMoveIn){
                StockMoveIn moveOut = (StockMoveIn) moves; 
                moveDocsId = moveOut.getDocsId();
            }
            

            // obtaining previous docs ID.
            String previousDocsId = "";
            if(!tempList.isEmpty()){
                ProductMovement tempMoves = tempList.get(0);
                if(tempMoves instanceof StockMoveOut){
                    StockMoveOut tempMoveOut = (StockMoveOut) tempMoves;
                    previousDocsId = tempMoveOut.getOrderId();
                }
                if(tempMoves instanceof StockMoveIn){
                    StockMoveIn tempMoveOut = (StockMoveIn) tempMoves;
                    previousDocsId = tempMoveOut.getDocsId();
                }
            }

            String productId = moves.getProductId();
            String relativeId = moves.getRelativeId();

            if(!NonValidDocsIds.isEmpty() && NonValidDocsIds.get(NonValidDocsIds.size() -1).equals(moveDocsId)){
                continue;
            }
            if(unExistedProductIds.contains(productId) || unActiveProductIds.contains(productId) || unExistMeasurement.contains(relativeId)){

                NonValidDocsIds.add(moveDocsId);
                tempList.clear();
                continue;
            }
            

            // add moves to list with same docsId. elsewise push tempList's movement to pendingMovements queue, clear tempList.
            if(previousDocsId.equals(moveDocsId)){
                tempList.add(moves);
                continue;
            } else {
                pendingMovements.addAll(tempList);
                tempList.clear();
                tempList.add(moves);
                continue;
            }


            // add a new move and next loop since tempList is empty but MoveDocsId should not in non-valid order/docs
            // if(tempList.size() == 0 && (NonValidDocsIds.isEmpty() || !NonValidDocsIds.get(NonValidDocsIds.size()-1).equals(moveDocsId))){
            //     tempList.add(moves);
            //     continue;
            // }

            
        }
        // end of movements push movement to pendingMovements
        if(!tempList.isEmpty()){
            pendingMovements.addAll(tempList);
            tempList.clear();
        }
        
        HashMap<String, HashMap<String, List<String>>> nonValidMap = new HashMap<String, HashMap<String, List<String>>>();
        for(ProductMovement moves : movements){
            
            String moveDocsId = "";
            if(moves instanceof StockMoveOut){
                StockMoveOut moveOut = (StockMoveOut) moves; 
                moveDocsId = moveOut.getOrderId();
            }
            if(moves instanceof StockMoveIn){
                StockMoveIn moveOut = (StockMoveIn) moves; 
                moveDocsId = moveOut.getDocsId();
            }
            
            if(NonValidDocsIds.contains(moveDocsId)){
                String productId = moves.getProductId();
                String relativeId = moves.getRelativeId();

                if(!nonValidMap.containsKey(moveDocsId)) nonValidMap.put(moveDocsId, new HashMap<String, List<String>>());

                if(unExistedProductIds.contains(productId)){ 

                    HashMap<String, List<String>> hashMap = nonValidMap.get(moveDocsId);
                    if(!hashMap.containsKey(MovementValidationException.UNEXISTED_PRODUCT_ID)) hashMap.put(
                        MovementValidationException.UNEXISTED_PRODUCT_ID, new LinkedList<String>());
                    hashMap.get(MovementValidationException.UNEXISTED_PRODUCT_ID).add(productId);
                }
                if(unExistMeasurement.contains(relativeId)){    

                    HashMap<String, List<String>> hashMap = nonValidMap.get(moveDocsId);
                    if(!hashMap.containsKey(MovementValidationException.UNEXISTED_MEASUREMENT)) hashMap.put(
                        MovementValidationException.UNEXISTED_MEASUREMENT, new LinkedList<String>());
                    hashMap.get(MovementValidationException.UNEXISTED_MEASUREMENT).add(relativeId);
                }
                if(unActiveProductIds.contains(productId)){ 

                    HashMap<String, List<String>> hashMap = nonValidMap.get(moveDocsId);
                    if(!hashMap.containsKey(MovementValidationException.UNACTIVE_PRODUCT_ID)) hashMap.put(
                        MovementValidationException.UNACTIVE_PRODUCT_ID, new LinkedList<String>());
                    hashMap.get(MovementValidationException.UNACTIVE_PRODUCT_ID).add(productId);
                }
            }
        }

        if(!nonValidMap.isEmpty()){
            MovementValidationException movementValidationException = new MovementValidationException();
            movementValidationException.setNonValidMap(nonValidMap);
            throw movementValidationException;
        }
    }

    public void pushMovement(Order order) throws MovementValidationException{
        if(order == null || !order.hasMovement()) { throw new NullPointerException("order is null or emptry."); }

        List<StockMoveOut> movements = order.getMovements();
        List<ProductMovement> moves = new LinkedList<ProductMovement>();
        for(StockMoveOut moveOut : movements){
            moves.add(moveOut);
        }
        this.pushMovement(moves);

        // LinkedList<String> toCheckingList = new LinkedList<String>();
        // Iterator<StockMoveOut> checkingIterator = order.getMovements().iterator();
        // while(checkingIterator.hasNext()){
        //     toCheckingList.add(checkingIterator.next().getProductId());
        // }
        // List<String> unactivityList = productService.getProductUnactivity(toCheckingList);

        // // throw exception return to caller and give a list of unactitvity product to exception member that has given following.
        // if(unactivityList != null && !unactivityList.isEmpty()){
        //     ProductUnactivityException exception = new ProductUnactivityException("checked unactivity product has" + unactivityList.toString());
        //     exception.setUnactivityProductList(unactivityList);
        //     throw exception;
        // }

        // LinkedList<ProductMovement> unablePushList = new LinkedList<ProductMovement>();
        // Iterator<StockMoveOut> iterator = movements.iterator();
        // while(iterator.hasNext()){
        //     ProductMovement moves = iterator.next();
        //     boolean added = this.pushMovement(moves);
        //     if(!added) {
        //         unablePushList.add(moves);
        //     }
        // }
        // order.setAnalysed(true);
        // return unablePushList.isEmpty() ? null : unablePushList;
    }

    // public Map<Order, List<String>> pushMovement(List<Order> orders){
    //     if(orders == null || orders.isEmpty()) throw new NullPointerException("orders is null or emptry");

    //     HashMap<Order, List<String>> unablePushOrders = new HashMap<Order, List<String>>();

    //     // a list of unable insert to pending cause by contains product is not activity. reject certain order to verify.
    //     List<Order> unInsertOrders = new LinkedList<Order>();

    //     for(Order order : orders){

    //         List<String> unactivityProducts = null;

    //         try{
    //             this.pushMovement(order);
    //         } catch(ProductUnactivityException e){ 
    //             unactivityProducts = e.getUnactivityProductList(); 
    //         }

    //         if(unactivityProducts != null){
    //             unInsertOrders.add(order);
    //             unablePushOrders.put(order, unactivityProducts);
    //         }
    //     }

    //     return unablePushOrders.isEmpty() ? null : unablePushOrders;
    // }

    public void pushMoveIns(StockInDocs docs){
        if(docs == null || !docs.hasMovement()) { throw new NullPointerException("order is null or emptry."); }

        List<StockMoveIn> movements = docs.getMovements();
        List<ProductMovement> moves = new LinkedList<ProductMovement>();
        for(StockMoveIn moveOut : movements){
            moves.add(moveOut);
        }
        this.pushMovement(moves);
        
        // List<String> productIds = new LinkedList<String>();
        // Iterator<StockMoveIn> checkingIterator = docs.getMovements().iterator();
        // while(checkingIterator.hasNext()){
        //     StockMoveIn next = checkingIterator.next();
        //     if(!productIds.contains(next.getProductId())) productIds.add(next.getProductId());
        // }

        // List<String> unactivityList = productService.getProductUnactivity(productIds);

        // // throw exception return to caller and give a list of unactitvity product to exception member that has given following.
        // if(unactivityList != null && !unactivityList.isEmpty()){
        //     ProductUnactivityException exception = new ProductUnactivityException("checked unactivity product has" + unactivityList.toString());
        //     exception.setUnactivityProductList(unactivityList);
        //     throw exception;
        // }

        // LinkedList<StockMoveIn> unablePushList = new LinkedList<StockMoveIn>();
        // Iterator<StockMoveIn> iterator = docs.getMovements().iterator();
        // while(iterator.hasNext()){
        //     StockMoveIn moves = iterator.next();
        //     boolean added = this.pushMovement(moves, true);
        //     if(!added) {
        //         unablePushList.add(moves);
        //     }
        // }
        // return unablePushList.isEmpty() ? null : unablePushList;
    }

    // public Map<StockInDocs, List<String>> pushMoveIns(List<StockInDocs> docsList){
    //     if(docsList == null || docsList.isEmpty()) throw new NullPointerException("docsList is null or emptry");

    //     HashMap<StockInDocs, List<String>> unablePushDocsList = new HashMap<StockInDocs, List<String>>();

    //     // a list of unable insert to pending cause by contains product is not activity. reject certain order to verify.
    //     List<StockInDocs> unInsertOrders = new LinkedList<StockInDocs>();

    //     for(StockInDocs docs : docsList){

    //         List<String> unactivityProducts = null;

    //         try{
    //             this.pushMoveIns(docs);
    //         } catch(ProductUnactivityException e){ 
    //             unactivityProducts = e.getUnactivityProductList(); 
    //         }

    //         if(unactivityProducts != null){
    //             unInsertOrders.add(docs);
    //             unablePushDocsList.put(docs, unactivityProducts);
    //         }
    //     }

    //     return unablePushDocsList.isEmpty() ? null : unablePushDocsList;
    // }

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
    // public HashMap<String, Map<String, ProductMeasurement>> pullOriginMeasurement(List<ProductMovement> movements){

    //     if(movements == null || movements.isEmpty()) throw new NullPointerException("movements is null or empty");

    //     HashMap<String, List<String>> channelMovementMap = new HashMap<String, List<String>>();
    //     Set<String> channelKeySet = channelMovementMap.keySet();
    //     HashMap<String, Map<String, ProductMeasurement>> resultMovementMap = new HashMap<String, Map<String, ProductMeasurement>>();

    //     for(ProductMovement movement : movements){
    //         if(!channelKeySet.contains(movement.getSalesChannel())){
    //             channelMovementMap.put(movement.getSalesChannel(), new ArrayList<String>());
    //         }

    //         List<String> relativeIds = channelMovementMap.get(movement.getSalesChannel());
    //         if(!relativeIds.contains(movement.getRelativeId())){
    //             String relativeId = movement.getRelativeId();
    //             if(!relativeId.contains("-")) relativeId = relativeId + "-" + movement.getUsedUOM();
    //             relativeIds.add(relativeId);
    //         }
    //     }

    //     for(String salesChannel : channelKeySet){
    //         List<String> relativeIds = channelMovementMap.get(salesChannel);
    //         Map<String, ProductMeasurement> resultMap = measurementService.getProductMeasByRelativeIdWithChannel(relativeIds, salesChannel);
    //         resultMovementMap.put(salesChannel, resultMap);
    //     }

    //     return resultMovementMap;
    // }

    public Map<String, ProductMeasurement> pullOriginMeasurementByRelativeIds(List<String> relativeIds){
        Map<String, ProductMeasurement> productMeasByRelativeIds = measurementService.getProductMeasByRelativeIds(relativeIds);
        return productMeasByRelativeIds;
    }

    public List<ProductMovement> getAllMoveRecord(String productId) {
        return null;
    }

	public void creatingNewStocking(String id) {

        if(id == null || id.isEmpty()) throw new NullPointerException("id is null");
        
        List<String> list = new LinkedList<String>();
        list.add(id);
        this.creatingNewStocking(list);
	}

    public void creatingNewStocking(List<String> ids){
        pStockingMapper.createStockingByProductIds(ids);
    }

    public boolean removeStockingForProductId(String productId){
        if(productId == null || productId.isEmpty()) throw new NullPointerException();

        List<String> list = new LinkedList<String>();
        list.add(productId);
        return pStockingMapper.deleteByProductIds(list) > 0;
    }

    public void init() {
        pStockingMapper.init();
        pMovementMapper.init();
    }
    
    public StockingService(){
        pendingMovements = new LinkedList<ProductMovement>();
    }

    public StockingService(ProductStockingMapper productStockingMapper, ProductMovementMapper productMovementMapper, MeasurementService measurementService, ProductService productService){
        this.measurementService = measurementService;
        this.pMovementMapper = productMovementMapper;
        this.pStockingMapper = productStockingMapper;
        this.productService = productService;
        this.pendingMovements = new LinkedList<ProductMovement>();
    }

}