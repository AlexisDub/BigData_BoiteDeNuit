package com.yourorg.boite.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Event {
    @BsonProperty("event_id")
    private int eventId;
    private String name;

    public Event() {}

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
