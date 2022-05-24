package com.assignment.recipe_sharing.services;

import com.assignment.recipe_sharing.models.Action;
import com.assignment.recipe_sharing.models.Recipe;
import com.assignment.recipe_sharing.models.RecipeFilter;
import com.assignment.recipe_sharing.utils.ImageUtils;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.*;


@Service
public class RecipeSharingService {

    public static final String RECIPE = "recipe";
    MongoStoreService mongoStoreService;
    ActionService actionService;

    @Autowired
    RecipeSharingService() {
        this.mongoStoreService = MongoStoreService.getInstance();
        this.actionService = new ActionService();
    }

    public Recipe insert(Recipe recipe) throws RuntimeException {
        try {
            Instant currentTime = Instant.now();
            MongoDatabase database = mongoStoreService.getRecipeSharingDatabase();
            ArrayList<String> collections = database.listCollectionNames().into(new ArrayList<>());
            if (!collections.contains(RECIPE)) {
                database.createCollection(RECIPE);
            }
            recipe = recipe.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .comments(null)
                    .likes(null)
                    .createdAt(currentTime)
                    .updatedAt(currentTime)
                    .build();
            database.getCollection(RECIPE, Recipe.class).insertOne(recipe);
        } catch (MongoException e) {
            throw new RuntimeException("Failed to write to database", e);
        }
        return recipe;
    }

    public String update(String recipeId, Recipe recipe) {
        try {
            List<Bson> updatedFields = new ArrayList<>();
            if (!CollectionUtils.isEmpty(recipe.getIngredients())) {
                updatedFields.add(Updates.set("ingredients", recipe.getIngredients()));
            }
            if (!CollectionUtils.isEmpty(recipe.getCookingSteps())) {
                updatedFields.add(Updates.set("cookingSteps", recipe.getCookingSteps()));
            }
            if (CollectionUtils.isEmpty(updatedFields)) {
                return recipeId;
            }
            updatedFields.add((Updates.set("updatedAt", Instant.now())));
            MongoDatabase database = mongoStoreService.getRecipeSharingDatabase();
            MongoCollection<Recipe> collection = database.getCollection(RECIPE, Recipe.class);
            collection.updateMany(and(eq("_id", recipeId)), Updates.combine(updatedFields));
            return recipeId;
        } catch (MongoException e) {
            throw new RuntimeException("Failed to update database", e);
        }
    }

    public String delete(String recipeId) {
        MongoDatabase database = mongoStoreService.getRecipeSharingDatabase();
        MongoCollection<Recipe> collection = database.getCollection(RECIPE, Recipe.class);
        collection.deleteOne(eq("_id", recipeId));
        return recipeId;
    }


    public List<Recipe> listRecipes() {
        return listRecipes(null);
    }

    public List<Recipe> listRecipes(RecipeFilter filter) {
        try {
            MongoCollection<Recipe> collection = mongoStoreService.getRecipeSharingDatabase()
                    .getCollection(RECIPE, Recipe.class);
            ArrayList<Recipe> recipes;
            if (filter != null && !CollectionUtils.isEmpty(filter.getIngredients())) {
                List<Bson> filters = new ArrayList<>();
                if (!CollectionUtils.isEmpty(filter.getIngredients())) {
                    filters.add(in("ingredients", filter.getIngredients()));
                }
                recipes = collection.find(and(filters)).into(new ArrayList<>());
            } else {
                recipes = collection.find().into(new ArrayList<>());
            }
            if (!CollectionUtils.isEmpty(recipes)) {
                return recipes.stream()
                        .map(recipe -> recipe.toBuilder()
                                .likes(actionService.getActions(createFilters(recipe.getId(),
                                        Action.ActionType.LIKE)))
                                .comments(actionService.getActions(createFilters(recipe.getId(),
                                        Action.ActionType.COMMENT)))
                                .build()).toList();
            }
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from database", e);
        }
    }

    public List<Bson> createFilters(String recipeId, Action.ActionType actionType) {
        List<Bson> filters = new ArrayList<>();
        filters.add(eq("recipeId", recipeId));
        filters.add(eq("actionType", actionType.toString()));
        return filters;
    }

    public String postAction(String recipeId, Action action) {
        actionService.upsertAction(recipeId, action);
        return recipeId;
    }

    public String uploadImages(String recipeId, MultipartFile[] imgFiles) throws HttpMediaTypeNotSupportedException {
        if (imgFiles.length > 0) {
            for (MultipartFile img : imgFiles) {
                ImageUtils.uploadImage(recipeId, img, "src/main/resources/cooking_pics/");
            }
        }
        return recipeId;
    }

    public List<byte[]> getImages(String recipeId) {
        return ImageUtils.getImage(recipeId, "src/main/resources/cooking_pics/");
    }

}
