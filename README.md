## BigData_BoiteDeNuit

Un projet Java / MongoDB pour gérer les clients, réservations et commandes d’une boîte de nuit.
Video presentation : https://www.youtube.com/watch?v=sP4MJCq4iR0
---

## Prérequis

- Java 11+  
- Maven  
- MongoDB en fonctionnement
- Docker & Docker-Compose installés


---


## Compilation et exécution du cluster 

1. docker-compose up -d

### Initialisation du Replica Set des config servers :
2. docker-compose exec config1 mongosh --port 27019 --eval " rs.initiate({ _id: 'rs-config', configsvr: true, members: [ { _id: 0, host: 'config1:27019' }, { _id: 1, host: 'config2:27019' }, { _id: 2, host: 'config3:27019' } ] }); "

### Initialiser les 3 shards : 
3. docker-compose exec shard1 mongosh --eval "rs.initiate({_id:'rs-shard1',members:[{_id:0,host:'shard1:27017'}]});"
4. docker-compose exec shard2 mongosh --eval "rs.initiate({_id:'rs-shard2',members:[{_id:0,host:'shard2:27017'}]});"
5. docker-compose exec shard3 mongosh --eval "rs.initiate({_id:'rs-shard3',members:[{_id:0,host:'shard3:27017'}]});"

### Ajouter les shards : 
6. docker-compose exec mongos mongosh --eval " sh.addShard('rs-shard1/shard1:27017'); sh.addShard('rs-shard2/shard2:27017'); sh.addShard('rs-shard3/shard3:27017'); "

### Activer le sharding sur notre DB :
7. docker-compose exec mongos mongosh --eval "sh.enableSharding('boite_de_nuit');"



### Maintenant, on peut importer les collections

### clients:
8. mongoimport --host localhost --port 27017 --db boite_de_nuit --collection clients --file ".\data\clients.json" --jsonArray --drop

### employees:
9. mongoimport --host localhost --port 27017 --db boite_de_nuit --collection employees --file ".\data\employees.json" --jsonArray --drop

### Ensuite on peut lancer avec Maven : 

10. mvn clean compile
11. mvn exec:java



---

## Tests unitaires

> Attention : les tests réinitialisent les collections (`clients`, `employees`).  
> Il faudra donc réimporter les données après chaque exécution des tests.

mvn test

---


