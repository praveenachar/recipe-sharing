package com.assignment.recipe_sharing.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include. NON_NULL)
public class DefaultResponse {
    String id;
    String message;
    Object data;
}
