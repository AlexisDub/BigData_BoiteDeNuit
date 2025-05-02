package com.yourorg.boite.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ReportDAO contient des méthodes d'agrégation MongoDB
 * couvrant groupements, jointures, et recherche hiérarchique.
 */
public class ReportDAO {
    private final MongoCollection<Document> collection;

    public ReportDAO() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("clients");
    }

    /**
     * 1) Clients ayant une réservation à la date donnée.
     */
    public List<Document> clientsWithReservationsOn(String date) {
        List<Bson> pipeline = Arrays.asList(
            Aggregates.unwind("$reservations"),
            Aggregates.match(Filters.eq("reservations.date", date)),
            Aggregates.project(Projections.fields(
                Projections.include("name"),
                Projections.computed("reservation", "$reservations")
            ))
        );
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * 2) Montant total des commandes pour une date.
     */
    public double totalOrderValueOnDate(String date) {
        List<Bson> pipeline = Arrays.asList(
            Aggregates.unwind("$orders"),
            Aggregates.unwind("$orders.drinks"),
            Aggregates.match(Filters.eq("orders.date", date)),
            Aggregates.group(null, Accumulators.sum("total", "$orders.drinks.price"))
        );
        Document result = collection.aggregate(pipeline).first();
        return result != null && result.containsKey("total")
            ? result.getDouble("total")
            : 0.0;
    }

    /**
     * 3) Top N des boissons vendues entre deux dates.
     */
    public List<Document> topNDrinks(int n, String fromDate, String toDate) {
        List<Bson> pipeline = Arrays.asList(
            Aggregates.unwind("$orders"),
            Aggregates.unwind("$orders.drinks"),
            Aggregates.match(Filters.and(
                Filters.gte("orders.date", fromDate),
                Filters.lte("orders.date", toDate)
            )),
            Aggregates.group("$orders.drinks.name", Accumulators.sum("count", 1)),
            Aggregates.sort(Sorts.descending("count")),
            Aggregates.limit(n)
        );
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * 4) Clients ayant un casier.
     */
    public List<Document> clientsWithLocker() {
        List<Bson> pipeline = Arrays.asList(
            Aggregates.match(Filters.exists("locker")),
            Aggregates.project(Projections.fields(
                Projections.include("name", "locker")
            ))
        );
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * 5) Jointure clients <> employees pour enrichir les commandes.
     */
    public List<Document> ordersWithEmployeeDetails() {
        List<Bson> pipeline = Arrays.asList(
            Aggregates.unwind("$orders"),
            Aggregates.lookup(
                "employees",            // collection à joindre
                "orders.employeeId",    // champ local
                "_id",                  // champ distant
                "employee"              // nom du champ résultat
            ),
            Aggregates.unwind("$employee"),
            Aggregates.project(Projections.fields(
                Projections.computed("clientName", "$name"),
                Projections.computed("order", "$orders"),
                Projections.computed("employee", "$employee")
            ))
        );
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * 6) Réseau de clients connectés par un même eventId (graphLookup).
     */
    public List<Document> clientNetworkByEvent(int eventId) {
        Bson match = Aggregates.match(
            Filters.eq("reservations.event.eventId", eventId)
        );
        Bson graph = new Document("$graphLookup",
            new Document("from", "clients")
                .append("startWith", "$reservations.event.eventId")
                .append("connectFromField", "reservations.event.eventId")
                .append("connectToField", "reservations.event.eventId")
                .append("as", "network")
        );
        return collection.aggregate(Arrays.asList(match, graph)).into(new ArrayList<>());
    }
}
