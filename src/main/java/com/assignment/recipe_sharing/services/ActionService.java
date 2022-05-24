package com.assignment.recipe_sharing.services;

import com.assignment.recipe_sharing.models.Action;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Service
public class ActionService {

    MongoStoreService mongoStoreService;

    @Autowired
    ActionService() {
        this.mongoStoreService = MongoStoreService.getInstance();
    }

    public void upsertAction(String recipeId, Action action) throws RuntimeException {
        if (action != null) {
            MongoDatabase database = mongoStoreService.mongoClient().getDatabase(
                    "recipe_sharing_db");
            ArrayList<String> collections = database.listCollectionNames().into(new ArrayList<>());
            if (!collections.contains("action")) {
                database.createCollection("action");
                postAction(recipeId, action);
            } else {
                MongoCollection<Action> collection = database.getCollection("action", Action.class);
                if (action.getActionType().equals(Action.ActionType.LIKE)) {
                    List<Bson> filters = new ArrayList<>();
                    filters.add(eq("recipeId", recipeId));
                    filters.add(eq("username", action.getUsername()));
                    filters.add(eq("actionType", Action.ActionType.LIKE.toString()));
                    List<Action> actions = getActions(filters);
                    if (actions.size() == 1) {
                        collection.deleteOne(and(filters));
                    } else if (CollectionUtils.isEmpty(actions)) {
                        postAction(recipeId, action);
                    }
                } else {
                    postAction(recipeId, action);
                }
            }
        }
    }

    public void postAction(String recipeId, Action action) throws RuntimeException {
        try {
            MongoDatabase database = mongoStoreService.mongoClient().getDatabase(
                    "recipe_sharing_db");
            Instant now = Instant.now();
            action = action.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .recipeId(recipeId)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            database.getCollection("action", Action.class).insertOne(action);
        } catch (MongoException e) {
            throw new RuntimeException("Failed to write to database");
        }
    }


    public List<Action> getActions(List<Bson> filters) {
        try {
            MongoDatabase database = mongoStoreService.mongoClient().getDatabase(
                    "recipe_sharing_db");
            return database.getCollection("action", Action.class).find(and(filters)).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new RuntimeException("Failed to read from database");
        }

    }
}
