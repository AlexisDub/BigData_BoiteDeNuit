package com.yourorg.boite.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Drink {
    @BsonProperty("drink_id")
    private int drinkId;
    private String name;
    private String category;
    private double price;

    public Drink() {}

    public int getDrinkId() { return drinkId; }
    public void setDrinkId(int drinkId) { this.drinkId = drinkId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
