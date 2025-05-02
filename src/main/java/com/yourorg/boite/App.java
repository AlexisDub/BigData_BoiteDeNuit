// File: src/main/java/com/yourorg/boite/App.java
package com.yourorg.boite;

import com.yourorg.boite.dao.ClientDAO;
import com.yourorg.boite.dao.EmployeeDAO;
import com.yourorg.boite.dao.ReportDAO;
import com.yourorg.boite.model.Client;
import com.yourorg.boite.model.Locker;
import com.yourorg.boite.model.Reservation;
import com.yourorg.boite.model.Order;
import com.yourorg.boite.model.Drink;
import com.yourorg.boite.model.Employee;
import com.yourorg.boite.model.Table;
import com.yourorg.boite.model.Event;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bson.Document;

public class App {
    public static void main(String[] args) {
        ClientDAO dao = new ClientDAO();

        List<Client> allClients = dao.findAll();
        System.out.println("Nombre de clients importés depuis JSON : " + allClients.size());
        // Affichez quelques noms pour valider
        allClients.stream()
          .limit(5)
          .map(Client::getName)
          .forEach(name -> System.out.println(" - " + name));



        EmployeeDAO empDao = new EmployeeDAO();
        List<Employee> emps = empDao.findAll();
        if (emps.isEmpty()) {
            throw new IllegalStateException("Pas d'employés en base !");
        }
        Random rnd = new Random();
        Employee randomEmp = emps.get(rnd.nextInt(emps.size()));

        // 1. INSERT un client simple
        Client c = new Client();
        c.setName("Test User");
        c.setPhone("000-000-0000");
        c.setAge(42);
        
        // Initialiser et assigner le locker
        Locker locker = new Locker();
        locker.setLockerId(1);
        locker.setSize("L");
        c.setLocker(locker);

        // Initialiser et assigner les réservations
        Reservation res = new Reservation();
        res.setReservationId(1);
        String formatted = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        res.setDate(formatted);
        Table table = new Table();
        table.setTableId(5);
        table.setCapacity(4);
        res.setTable(table);
        Event event = new Event();
        event.setEventId(1);
        event.setName("Soirée Test");
        res.setEvent(event);
        c.setReservations(Arrays.asList(res));

        // Initialiser et assigner les commandes
        Drink drink = new Drink();
        drink.setDrinkId(1);
        drink.setName("Cocktail Test");
        drink.setCategory("Alcool");
        drink.setPrice(10.0);
        Order order = new Order();
        order.setOrderId(1);
        order.setEmployee(randomEmp);
        order.setDrinks(Arrays.asList(drink));
        c.setOrders(Arrays.asList(order));


        

        System.out.println(" order : " + c.getOrders().get(0).getOrderId() + ", drink: " + c.getOrders().get(0).getDrinks().get(0).getName() + ", employee: " + c.getOrders().get(0).getEmployee().getName());

        dao.insertOne(c);
        System.out.println("Inséré client avec _id = " + c.getId());

        // 2. UPDATE partiel : modifier le téléphone
        System.out.println("Ancien téléphone : " + c.getPhone());
        dao.updatePhone(c.getId(), "111-222-3333");
        System.out.println("Téléphone mis à jour pour _id = " + c.getId());
        c = dao.findById(c.getId()); 
        System.out.println("Nouveau téléphone : " + c.getPhone());
        System.out.println("Client : " + c.getName() + ", téléphone : " + c.getPhone() + ", âge : " + c.getAge());

        // 3. DELETE le client
        dao.deleteOne(c.getId());
        System.out.println("Client supprimé pour _id = " + c.getId());

        // 4. INSERT plusieurs clients
        Client c1 = new Client();
        c1.setName("Batch1"); c1.setPhone("123"); c1.setAge(30);
        Client c2 = new Client();
        c2.setName("Batch2"); c2.setPhone("456"); c2.setAge(35);
        dao.insertMany(Arrays.asList(c1, c2));
        System.out.println("Batch de 2 clients inséré.");

        // 5. DELETE en masse : tous > 30 ans
        dao.deleteByAgeGreaterThan(30);
        System.out.println("Clients de plus de 30 ans supprimés.");


        // === Début tests ReportDAO : PIPELINE ===

        ReportDAO report = new ReportDAO();

        System.out.println("\n--- 1) clientsWithReservationsOn(\"15/02/2025\") ---");
        report.clientsWithReservationsOn("15/02/2025")
              .forEach(d -> System.out.println(d.toJson()));

        System.out.println("\n--- 2) totalOrderValueOnDate(\"15/02/2025\") ---");
        System.out.println(report.totalOrderValueOnDate("15/02/2025"));

        System.out.println("\n--- 3) topNDrinks(3, \"01/01/2025\", \"31/12/2025\") ---");
        report.topNDrinks(3, "01/01/2025", "31/12/2025")
              .forEach(d -> System.out.println(d.toJson()));

        System.out.println("\n--- 4) clientsWithLocker() ---");
        report.clientsWithLocker()
              .forEach(d -> System.out.println(d.toJson()));

        System.out.println("\n--- 5) ordersWithEmployeeDetails() ---");
        report.ordersWithEmployeeDetails()
              .forEach(d -> System.out.println(d.toJson()));

        System.out.println("\n--- 6) clientNetworkByEvent(1) ---");
        report.clientNetworkByEvent(1)
              .forEach(d -> System.out.println(d.toJson()));

        System.out.println("--- Fin des tests ReportDAO ---");
        
        
    }
}
