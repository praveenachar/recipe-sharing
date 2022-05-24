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
public class User {
    String id;
    String firstName;
    String lastName;
    Instant createdAt;
    Instant updatedAt;
}
