package com.assignment.recipe_sharing.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@Jacksonized
public class Action {
    String id;
    String username;
    String recipeId;
    ActionType actionType;
    String comment;
    Instant createdAt;
    Instant updatedAt;

    public enum ActionType {
        LIKE,COMMENT
    }
}


