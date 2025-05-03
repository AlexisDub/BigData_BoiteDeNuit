package com.yourorg.boite.dao;

import com.yourorg.boite.model.Employee;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private final MongoCollection<Employee> collection;

    public EmployeeDAO() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("employees", Employee.class);
    }

    /**
     * Insère un employé.
     */
    public void insertOne(Employee employee) {
        collection.insertOne(employee);
    }

    /**
     * Insère plusieurs employés.
     */
    public void insertMany(List<Employee> employees) {
        collection.insertMany(employees);
    }

    /**
     * Récupère un employé par son identifiant ObjectId.
     */
    public Employee findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    /**
     * Récupère tous les employés.
     */
    public List<Employee> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    /**
     * Met à jour le rôle d'un employé.
     */
    public void updateRole(ObjectId id, String newRole) {
        collection.updateOne(
            Filters.eq("_id", id),
            com.mongodb.client.model.Updates.set("role", newRole)
        );
    }

    /**
     * Supprime un employé par son identifiant.
     */
    public void deleteOne(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }
}
