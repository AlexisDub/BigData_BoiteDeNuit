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

        ReportDAO report = new ReportDAO();
        System.out.println("\n--- 1) clientsWithReservationsOn(\"25/06/2024\") ---");
        List<Document> c1 = report.clientsWithReservationsOn("25/06/2024");
        c1.forEach(d -> System.out.println(d.toJson()));

        System.out.println("\n--- 2) totalOrderValueOnDate(\"18/05/2024\") ---");
        double total = report.totalOrderValueOnDate("18/05/2024");
        System.out.println(total);

        System.out.println("\n--- 3) topNDrinks(3, \"01/01/2025\", \"31/12/2025\") ---");
        List<Document> top = report.topNDrinks(3, "01/01/2025", "31/12/2025");
        top.forEach(d -> System.out.println(d.toJson()));

        System.out.println("\n--- 4) clientsByLocker() ---");
        List<Document> lockers = report.clientsByLocker();
        lockers.forEach(d -> System.out.println(d.toJson()));

        int eventId = 2;

      // 5) countClientsByEvent
      System.out.println("\n--- 5) countClientsByEvent(" + eventId + ") ---");
      long count = report.countClientsByEvent(eventId);
      System.out.println(count);

      // 6) clientNetworkByEvent
      System.out.println("\n--- 6) clientNetworkByEvent(" + eventId + ") ---");
      List<String> network = report.clientNetworkByEvent(eventId);
      if (network.isEmpty()) {
      System.out.println("Aucun réseau trouvé pour l'événement " + eventId);
      } else {
      System.out.println(
            "Event id : " + eventId
            + ", Clients connectés grace a cet event : " + network
      );
      }
      
}
}
