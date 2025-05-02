# BigData_BoiteDeNuit

Pour importer les clients : 
mongoimport --db boite_de_nuit --collection clients --file "data\clients.json" --jsonArray --drop

Pour importer les employés :
mongoimport --db boite_de_nuit --collection employees --file "data\employees.json" --jsonArray --drop


EXECUTER :
mvn clean compile
mvn exec:java 

Pour executer les Tests ( attention, ils suppriment les collections, il faut donc les réimporter apres) : 
mvn test

