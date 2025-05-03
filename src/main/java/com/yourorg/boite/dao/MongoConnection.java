package com.yourorg.boite.dao;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoConnection {
    private static final MongoClient client;

    static {
        // 1) POJO codec provider
        PojoCodecProvider pojoProvider = PojoCodecProvider.builder()
            .automatic(true)
            .build();

        // 2) Combine codec registry
        CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(pojoProvider)
        );

        // 3) Connexion au mongos (router)
        ConnectionString connString = new ConnectionString("mongodb://localhost:27017");

        // 4) Build MongoClientSettings sans replicaSet
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
            .codecRegistry(pojoCodecRegistry)
            .build();
        client = MongoClients.create(settings);
            }

    /**  
     * @return la base de données configurée  
     */
    public static MongoDatabase getDatabase() {
        return client.getDatabase("boite_de_nuit");
    }
}
