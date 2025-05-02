package com.yourorg.boite.model;

import org.bson.types.ObjectId;
import java.util.List;

public class Client {
    private ObjectId id;           // correspond à _id dans MongoDB
    private String name;
    private String phone;
    private int age;
    private Locker locker;
    private List<Reservation> reservations;
    private List<Order> orders;

    // Constructeur vide (nécessaire pour le driver)
    public Client() { }

    // Getters & Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Locker getLocker() { return locker; }
    public void setLocker(Locker locker) { this.locker = locker; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}
