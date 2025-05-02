// File: src/main/java/com/yourorg/boite/dao/ClientDAO.java
package com.yourorg.boite.dao;

import com.yourorg.boite.model.Client;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.yourorg.boite.dao.MongoConnection;
import org.bson.types.ObjectId;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private final MongoCollection<Client> collection;

    public ClientDAO() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("clients", Client.class);
    }

    /**
     * Insère un client.
     */
    public void insertOne(Client client) {
        collection.insertOne(client);
    }

    /**
     * Insère plusieurs clients.
     */
    public void insertMany(List<Client> clients) {
        collection.insertMany(clients);
    }

    /**
     * Remplace entièrement un document client existant (update global).
     */
    public void replaceOne(Client client) {
        collection.replaceOne(
            Filters.eq("_id", client.getId()),
            client
        );
    }

    /**
     * Met à jour le numéro de téléphone d'un client.
     */
    public void updatePhone(ObjectId id, String newPhone) {
        collection.updateOne(
            Filters.eq("_id", id),
            Updates.set("phone", newPhone)
        );
    }

    /**
     * Supprime un client par son identifiant.
     */
    public void deleteOne(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    /**
     * Supprime tous les clients dont l'âge est strictement supérieur à la valeur donnée.
     */
    public void deleteByAgeGreaterThan(int age) {
        collection.deleteMany(Filters.gt("age", age));
    }

    /**
     * Récupère un client par son identifiant.
     */
    public Client findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    public List<Client> findAll() {
        return collection.find().into(new ArrayList<>());
    }
}
