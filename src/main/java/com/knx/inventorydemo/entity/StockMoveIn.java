package com.knx.inventorydemo.entity;

public class StockMoveIn extends ProductMovement implements Comparable{
    private String docsId; // repository column name: docs_id
    private int itemRowOfDocs; // repository column name: row_of_item

    public int getItemRowOfDocs() {
        return itemRowOfDocs;
    }

    public StockMoveIn setItemRowOfDocs(int itemRowOfDocs) {
        this.itemRowOfDocs = itemRowOfDocs;
        return this;
    }

    public String getDocsId() {
        return docsId;
    }

    public StockMoveIn setDocsId(String docsId) {
        this.docsId = docsId;
        return this;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null) return -1;

        if(o instanceof StockMoveIn){
            StockMoveIn oMoveIn = (StockMoveIn) o;
            if(oMoveIn.getDocsId().equals(this.getDocsId())
                && oMoveIn.getRelativeId().equals(getRelativeId()))
                return 0;
            
            if(oMoveIn.getDocsId() == null || oMoveIn.getDocsId().isEmpty()) return -1;
            if(this.getDocsId() == null || this.getDocsId().isEmpty()) return 1;

            if(oMoveIn.getDocsId().compareTo(this.getDocsId()) == 0){
                if(oMoveIn.getItemRowOfDocs() == this.getItemRowOfDocs())
                    return 0;
                else
                    return oMoveIn.getItemRowOfDocs() > this.getItemRowOfDocs()
                        ? 1 : -1;
            }
        }

        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StockMoveIn){
            StockMoveIn moveIn = (StockMoveIn) obj;
            return moveIn.getDocsId().equals(this.getDocsId())
                && moveIn.getRelativeId().equals(this.getRelativeId())
                && moveIn.getItemRowOfDocs() == this.getItemRowOfDocs();
        }
        return super.equals(obj);
    }

    
}
