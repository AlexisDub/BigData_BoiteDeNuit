package com.yourorg.boite.dao;

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
        // 1. Crée un PojoCodecProvider en mode automatique
        PojoCodecProvider pojoProvider = PojoCodecProvider.builder()
            .automatic(true)
            .build();

        // 2. Construit un registre qui combine le codec par défaut + le pojoProvider
        CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(pojoProvider)
        );

        // 3. Monte un MongoClientSettings avec ce CodecRegistry
        MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojoCodecRegistry)
            .build();

        // 4. Crée le client avec ces settings
        client = MongoClients.create(settings);
    }

    /**  
     * @return la base de données configurée  
     */
    public static MongoDatabase getDatabase() {
        return client.getDatabase("boite_de_nuit");
    }
}