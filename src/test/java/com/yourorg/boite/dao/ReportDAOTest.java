package com.yourorg.boite.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ReportDAO, jeu de données contrôlé pour chaque pipeline.
 */
public class ReportDAOTest {
    private static ReportDAO reportDao;

    @BeforeAll
    public static void setup() {
        MongoDatabase db = MongoConnection.getDatabase();
        MongoCollection<Document> col = db.getCollection("clients");
        col.drop();

        // Jeu de données contrôlé
        String date = "01/01/2025";

        // Client A : deux réservations dont une le date, commande deux drinks
        Document resA1 = new Document("date", date)
            .append("table", new Document("table_id", 101))
            .append("event", new Document("eventId", 1));
        Document resA2 = new Document("date", "02/01/2025")
            .append("table", new Document("table_id", 102))
            .append("event", new Document("eventId", 1));
        Document drinkA1 = new Document("name", "D1").append("category", "C1").append("price", 5.0);
        Document drinkA2 = new Document("name", "D2").append("category", "C2").append("price", 7.0);
        Document orderA = new Document("date", date).append("drinks", Arrays.asList(drinkA1, drinkA2));
        Document clientA = new Document("name", "Alice")
            .append("reservations", Arrays.asList(resA1, resA2))
            .append("orders", Arrays.asList(orderA))
            .append("locker", new Document("lockerId", 10));

        // Client B : une réservation le date, commande un drink
        Document resB = new Document("date", date)
            .append("table", new Document("table_id", 201))
            .append("event", new Document("eventId", 1));
        Document drinkB = new Document("name", "D1").append("category", "C1").append("price", 3.0);
        Document orderB = new Document("date", date).append("drinks", Arrays.asList(drinkB));
        Document clientB = new Document("name", "Bob")
            .append("reservations", Arrays.asList(resB))
            .append("orders", Arrays.asList(orderB))
            .append("locker", new Document("lockerId", 20));

        // Insérer clients
        col.insertMany(Arrays.asList(clientA, clientB));

        // Créer collection employees et insérer
        MongoCollection<Document> empCol = db.getCollection("employees");
        empCol.drop();
        Document emp = new Document("name", "Paul").append("role", "Barman").append("salary", 2000);
        empCol.insertOne(emp);

        // Mettre à jour employeeId dans orders
        col.updateOne(Filters.eq("name", "Alice"),
            new Document("$set", new Document("orders.0.employeeId", emp.getObjectId("_id"))));
        col.updateOne(Filters.eq("name", "Bob"),
            new Document("$set", new Document("orders.0.employeeId", emp.getObjectId("_id"))));

        reportDao = new ReportDAO();
    }

    @Test
    public void testClientsWithReservationsOn() {
        List<Document> res = reportDao.clientsWithReservationsOn("01/01/2025");
        assertEquals(2, res.size(), "2 clients doivent avoir une réservation le 01/01/2025");
    }

    @Test
    public void testTotalOrderValueOnDate() {
        double total = reportDao.totalOrderValueOnDate("01/01/2025");
        // Alice:5+7=12, Bob:3 => 15
        assertEquals(15.0, total, 0.001);
    }

    @Test
    public void testTopNDrinks() {
        List<Document> top = reportDao.topNDrinks(1, "01/01/2025", "02/01/2025");
        assertEquals(1, top.size());
        assertEquals("D1", top.get(0).getString("_id"));
    }

    @Test
    public void testClientsWithLocker() {
        List<Document> clients = reportDao.clientsWithLocker();
        assertEquals(2, clients.size(), "2 clients doivent avoir un casier");
    }

    @Test
    public void testOrdersWithEmployeeDetails() {
        List<Document> list = reportDao.ordersWithEmployeeDetails();
        assertFalse(list.isEmpty(), "Doit retourner des commandes enrichies");
        assertTrue(list.get(0).containsKey("employee"), "Chaque document doit contenir 'employee'");
    }

    @Test
    public void testClientNetworkByEvent() {
        List<Document> net = reportDao.clientNetworkByEvent(1);
        assertFalse(net.isEmpty(), "Réseau non vide pour eventId=1");
        assertTrue(net.get(0).containsKey("network"), "Chaque document doit contenir 'network'");
    }
}
