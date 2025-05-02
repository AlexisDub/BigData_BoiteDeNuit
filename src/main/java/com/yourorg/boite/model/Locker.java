package com.yourorg.boite.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Locker {
    @BsonProperty("lockerId")
    private int lockerId;
    private String size;

    public Locker() {}

    public int getLockerId() { return lockerId; }
    public void setLockerId(int lockerId) { this.lockerId = lockerId; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
