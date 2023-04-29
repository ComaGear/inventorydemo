package com.knx.inventorydemo.entity;

public class StockMoveIn extends ProductMovement {
    private String docsId; // repository column name: docs_id
    private int itemRowOfDocs; // repository column name: row_of_item

    public int getItemRowOfDocs() {
        return itemRowOfDocs;
    }

    public void setItemRowOfDocs(int itemRowOfDocs) {
        this.itemRowOfDocs = itemRowOfDocs;
    }

    public String getDocsId() {
        return docsId;
    }

    public void setDocsId(String docsId) {
        this.docsId = docsId;
    }
}
