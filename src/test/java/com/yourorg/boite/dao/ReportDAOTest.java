package com.yourorg.boite.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;

public class ReportDAOTest {
    private static ReportDAO reportDao;

    @BeforeAll
    public static void setup() {
        MongoDatabase db = MongoConnection.getDatabase();

        // Préparation de la collection clients
        MongoCollection<Document> col = db.getCollection("clients");
        col.drop();

        String date = "01/01/2025";

        // --- Client A ---
        Document resA1 = new Document("date", date)
            .append("table", new Document("table_id", 101).append("capacity", 4))
            .append("event", new Document("event_id", 1).append("name", "E1"));
        Document resA2 = new Document("date", "02/01/2025")
            .append("table", new Document("table_id", 102).append("capacity", 2))
            .append("event", new Document("event_id", 1).append("name", "E1"));
        Document drinkA1 = new Document("drink_id", 1).append("name", "D1").append("category", "C1").append("price", 5.0);
        Document drinkA2 = new Document("drink_id", 2).append("name", "D2").append("category", "C2").append("price", 7.0);
        Document orderA = new Document("date", date).append("drinks", Arrays.asList(drinkA1, drinkA2)).append("employee_id", 42);
        Document clientA = new Document("name", "Alice")
            .append("reservations", Arrays.asList(resA1, resA2))
            .append("orders", Arrays.asList(orderA))
            .append("locker", new Document("locker_id", 10).append("size", "M"));

        // --- Client B ---
        Document resB = new Document("date", date)
            .append("table", new Document("table_id", 201).append("capacity", 6))
            .append("event", new Document("event_id", 1).append("name", "E1"));
        Document drinkB = new Document("drink_id", 3).append("name", "D1").append("category", "C1").append("price", 3.0);
        Document orderB = new Document("date", date).append("drinks", Arrays.asList(drinkB)).append("employee_id", 43);
        Document clientB = new Document("name", "Bob")
            .append("reservations", Arrays.asList(resB))
            .append("orders", Arrays.asList(orderB))
            .append("locker", new Document("locker_id", 20).append("size", "L"));

        // Insérer en base
        col.insertMany(Arrays.asList(clientA, clientB));

        reportDao = new ReportDAO();
    }

    @Test
    public void testClientsWithReservationsOn() {
        List<Document> res = reportDao.clientsWithReservationsOn("01/01/2025");
        // Alice & Bob ont chacun au moins une réservation à cette date
        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(d -> "Alice".equals(d.getString("name"))));
        assertTrue(res.stream().anyMatch(d -> "Bob".equals(d.getString("name"))));
    }

    @Test
    public void testTotalOrderValueOnDate() {
        double total = reportDao.totalOrderValueOnDate("01/01/2025");
        // Alice : 5 + 7 = 12, Bob : 3 → total = 15
        assertEquals(15.0, total, 1e-6);
    }

    @Test
    public void testTopNDrinks() {
        // Sur l’intervalle couvrant les deux commandes, D1 a 2 occurrences, D2 en a 1
        List<Document> top = reportDao.topNDrinks(1, "01/01/2025", "02/01/2025");
        assertEquals(1, top.size());
        assertEquals("D1", top.get(0).getString("_id"));
        assertEquals(2, top.get(0).getInteger("count"));
    }

    @Test
    public void testClientsByLocker() {
        List<Document> list = reportDao.clientsByLocker();
        // Les deux clients ont un champ locker
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(d -> d.getString("name").equals("Alice")));
        assertTrue(list.stream().anyMatch(d -> d.getString("name").equals("Bob")));
    }

    @Test
    public void testCountClientsByEvent() {
        long count = reportDao.countClientsByEvent(1);
        // Alice et Bob ont réservation.event_id == 1
        assertEquals(2L, count);
    }

    @Test
    public void testClientNetworkByEvent() {
        List<String> network = reportDao.clientNetworkByEvent(1);
        // Doit contenir au moins Alice et Bob
        assertTrue(network.contains("Alice"));
        assertTrue(network.contains("Bob"));
        // et pas vide
        assertFalse(network.isEmpty());
    }
}
