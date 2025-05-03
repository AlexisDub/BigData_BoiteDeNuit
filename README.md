## BigData_BoiteDeNuit

Un projet Java / MongoDB pour gérer les clients, réservations, commandes et rapports d’une boîte de nuit.

---

## Prérequis

- Java 11+  
- Maven  
- MongoDB en fonctionnement  

---

## Import des données


### Clients

mongoimport --db boite_de_nuit --collection clients --file "data\clients.json" --jsonArray --drop

### Employés

mongoimport --db boite_de_nuit --collection employees --file "data\employees.json" --jsonArray --drop

## Compilation et exécution

1. Compiler le projet  
   mvn clean compile

2. Lancer l’application  
   mvn exec:java

---

## Tests unitaires

> Attention : les tests réinitialisent les collections (`clients`, `employees`).  
> Il faudra donc réimporter les données après chaque exécution des tests.

mvn test

---

## Structure du projet

BigData_BoiteDeNuit/
├── data/
│ ├── clients.json
│ └── employees.json
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/yourorg/boite/
│ │ │ ├── App.java
│ │ │ ├── MongoConnection.java
│ │ │ ├── dao/
│ │ │ │ ├── ClientDAO.java
│ │ │ │ ├── EmployeeDAO.java
│ │ │ │ └── ReportDAO.java
│ │ │ └── model/
│ │ │ ├── Client.java
│ │ │ ├── Employee.java
│ │ │ ├── Order.java
│ │ │ ├── Drink.java
│ │ │ ├── Reservation.java
│ │ │ ├── Table.java
│ │ │ ├── Locker.java
│ │ │ └── Event.java
│ │ └── resources/
│ └── test/
│ └── java/
│ └── com/yourorg/boite/dao/
│ └── ReportDAOTest.java
├── .gitignore
├── pom.xml
└── README.md
