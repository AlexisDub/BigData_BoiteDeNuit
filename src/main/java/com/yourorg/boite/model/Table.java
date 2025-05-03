package com.yourorg.boite.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Table {
     @BsonProperty("table_id")
    private int tableId;
    private int capacity;

    public Table() {}

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
