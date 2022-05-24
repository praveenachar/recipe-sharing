package com.assignment.recipe_sharing.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@Jacksonized
public class Recipe {
    String id;
    String username;
    List<String> ingredients;
    List<String> cookingSteps;
    Instant createdAt;
    Instant updatedAt;
    List<Action> likes;
    List<Action> comments;

}
