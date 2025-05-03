package com.yourorg.boite.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Reservation {
     @BsonProperty("reservation_id")
    private int reservationId;
    private String date;    
    private Table table;
    private Event event;

    public Reservation() {}

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Table getTable() { return table; }
    public void setTable(Table table) { this.table = table; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
