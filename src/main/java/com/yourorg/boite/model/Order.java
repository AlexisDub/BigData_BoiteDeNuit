package com.yourorg.boite.model;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.util.List;

public class Order {
    @BsonProperty("orderId")
    private int orderId;

    private Employee employee;

    private List<Drink> drinks;

    public Order() {
        // constructeur vide nécessaire pour le PojoCodecProvider
    }

    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /** 
     * Renvoie l’ObjectId de l’employé s’il a déjà été désérialisé, sinon null.
     */
    public ObjectId getEmployeeId() {
        return employee != null ? employee.getId() : null;
    }

    /**
     * Setter utilisé par le PojoCodec : on instancie l’Employee s’il est null,
     * puis on lui assigne l’ID.
     */
    public void setEmployeeId(ObjectId employeeId) {
        if (this.employee == null) {
            this.employee = new Employee();
        }
        this.employee.setId(employeeId);
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }
    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }
}
