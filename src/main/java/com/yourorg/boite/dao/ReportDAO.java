package com.yourorg.boite.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

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
            Aggregates.group(
                null,
                Accumulators.sum("total", "$orders.drinks.price")
            )
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
            Aggregates.group("$orders.drinks.name",
                Accumulators.sum("count", 1)
            ),
            Aggregates.sort(new Document("count", -1)),
            Aggregates.limit(n)
        );
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * 4) Clients par numéro de casier.
     */
    public List<Document> clientsByLocker() {
        List<Bson> pipeline = Arrays.asList(
            Aggregates.match(Filters.exists("locker")),
            Aggregates.project(Projections.fields(
                Projections.include("name", "locker")
            ))
        );
        return collection.aggregate(pipeline).into(new ArrayList<>());
    }

    /**
     * 5) Nombre de clients inscrits à un événement.
     */
    public long countClientsByEvent(int eventId) {
        // Compte directement le nombre de clients
        return collection.countDocuments(eq("reservations.event.event_id", eventId));
    }

    /**
     * 6) Réseau de clients connectés par un même eventId (graphLookup + projection).
     */
    public List<String> clientNetworkByEvent(int eventId) {
    List<Bson> pipeline = Arrays.asList(
      match(eq("reservations.event.event_id", eventId)),
      graphLookup(
        "clients",
        "$reservations.event.event_id",    // startWith
        "reservations.event.event_id",    // connectFromField
        "reservations.event.event_id",    // connectToField
        "network"                         // as
      ),
      project(fields(
        excludeId(),
        computed("names", "$network.name")
      ))
    );

    Document doc = collection.aggregate(pipeline).first();
    if (doc == null) {
        return Collections.emptyList();
    }
    // Renvoie la liste de noms
    return doc.getList("names", String.class);
}
}
