## BigData_BoiteDeNuit

Un projet Java / MongoDB pour gérer les clients, réservations et commandes d’une boîte de nuit.

---

## Prérequis

- Java 11+  
- Maven  
- MongoDB en fonctionnement
- Docker & Docker-Compose installés


---

## Import des données


### Clients

mongoimport --db boite_de_nuit --collection clients --file "data\clients.json" --jsonArray --drop

### Employés

mongoimport --db boite_de_nuit --collection employees --file "data\employees.json" --jsonArray --drop

## Compilation et exécution du cluster 

2. Lancer Docker
   docker-compose up -d

2. Compiler le projet  
   mvn clean compile

3. Lancer l’application  
   mvn exec:java

---

## Tests unitaires

> Attention : les tests réinitialisent les collections (`clients`, `employees`).  
> Il faudra donc réimporter les données après chaque exécution des tests.

mvn test

---


