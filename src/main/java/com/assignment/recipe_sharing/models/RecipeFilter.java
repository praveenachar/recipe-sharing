package com.assignment.recipe_sharing.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class RecipeFilter {
    List<String> ingredients;
}
