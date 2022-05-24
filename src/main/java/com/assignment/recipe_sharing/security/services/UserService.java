package com.assignment.recipe_sharing.security.services;

import com.assignment.recipe_sharing.security.models.UpdateUser;
import com.assignment.recipe_sharing.models.User;
import com.assignment.recipe_sharing.services.MongoStoreService;
import com.assignment.recipe_sharing.utils.ImageUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.*;

@Service
public class UserService {

    MongoStoreService mongoStoreService;

    @Autowired
    UserService() {
        this.mongoStoreService = MongoStoreService.getInstance();
    }

    public String insert(User user) {
        Instant now = Instant.now();
        user = user.toBuilder().id(UUID.randomUUID().toString())
                .createdAt(now)
                .updatedAt(now)
                .build();
        MongoDatabase database = mongoStoreService.mongoClient().getDatabase("recipe_sharing_db");
        ArrayList<String> collections = database.listCollectionNames().into(new ArrayList<>());
        if (!collections.contains("user")) {
            database.createCollection("user");
        }
        MongoCollection<User> collection = database.getCollection("user", User.class);
        collection.insertOne(user);
        collection.updateMany(empty(), Updates.combine(Updates.set("username", null),
                Updates.set("password", null)));
        return user.getId();
    }

    public String updatedUser(String userId, UpdateUser updateUser) throws AuthenticationException {
        MongoDatabase database = mongoStoreService.mongoClient().getDatabase("recipe_sharing_db");
        MongoCollection<User> collection = database.getCollection("user", User.class);
        List<Bson> filters = new ArrayList<>();
        filters.add(eq("username", updateUser.getUsername()));
        if (collection.find(and(filters)).into(new ArrayList<>()).size() > 0) {
            throw new AuthenticationException("username already exists");
        }
        filters = new ArrayList<>();
        filters.add(eq("_id", userId));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(updateUser.getPassword());
        Bson updateFields = Updates.combine(Updates.set("username", updateUser.getUsername()),
                Updates.set("password", encodedPassword));
        collection.updateMany(and(filters), updateFields);
        return userId;
    }

    public List<UpdateUser> getCreds(String username) {
        MongoDatabase database = mongoStoreService.mongoClient().getDatabase("recipe_sharing_db");
        MongoCollection<UpdateUser> collection = database.getCollection("user", UpdateUser.class);
        List<Bson> filters = new ArrayList<>();
        filters.add(eq("username", username));
        ArrayList<UpdateUser> into = collection.find(and(filters)).into(new ArrayList<>());
        return into;
    }

    public String uploadImage(String userId, MultipartFile file) throws HttpMediaTypeNotSupportedException {
        return ImageUtils.uploadImage(userId, file, "src/main/resources/profile_pics/");
    }

    public List<byte[]> getImage(String userId) {
       return ImageUtils.getImage(userId, "src/main/resources/profile_pics/");
    }
}
