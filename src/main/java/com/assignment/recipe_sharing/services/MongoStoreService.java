package com.assignment.recipe_sharing.services;

import com.assignment.recipe_sharing.models.Recipe;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoStoreService {
    private static final MongoStoreService mongoStoreService = new MongoStoreService();
    private final String connectionString = System.getenv("CONNECTION_STRING");

    public MongoStoreService() {
    }

    public static MongoStoreService getInstance() {
        return mongoStoreService;
    }

    public MongoClient mongoClient() {
        CodecRegistry myRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder()
                        .register("com.assignment.recipe_sharing.models")
                        .build()),
                fromProviders(PojoCodecProvider.builder()
                        .register("com.assignment.recipe_sharing.security.models")
                        .build()));
        MongoClientSettings.Builder clusterSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.applyConnectionString(new ConnectionString(connectionString)))
                .codecRegistry(myRegistry)
                .uuidRepresentation(UuidRepresentation.STANDARD);
        return MongoClients.create(clusterSettings.build());
    }

    public MongoDatabase getRecipeSharingDatabase() throws RuntimeException {
        try {
            return mongoClient().getDatabase("recipe_sharing_db");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Database does not exist", e);
        }
    }
}
