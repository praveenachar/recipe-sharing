package com.assignment.recipe_sharing.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@Jacksonized
public class AuthenticationResponse {
    String bearerToken;
}
