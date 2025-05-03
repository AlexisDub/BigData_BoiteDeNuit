// File: src/main/java/com/yourorg/boite/model/Employee.java
package com.yourorg.boite.model;

import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Employee {
    @BsonId
    private ObjectId id;
    
    @BsonProperty("name")
    private String name;
    
    @BsonProperty("role")
    private String role;
    
    @BsonProperty("salary")
    private int salary;

    // Constructeur vide nécessaire pour le PojoCodecProvider
    public Employee() {}

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
