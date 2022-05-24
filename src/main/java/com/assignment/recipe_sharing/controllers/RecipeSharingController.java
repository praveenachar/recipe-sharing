package com.assignment.recipe_sharing.controllers;

import com.assignment.recipe_sharing.security.JwtTokenUtil;
import com.assignment.recipe_sharing.models.Action;
import com.assignment.recipe_sharing.models.DefaultResponse;
import com.assignment.recipe_sharing.models.Recipe;
import com.assignment.recipe_sharing.models.RecipeFilter;
import com.assignment.recipe_sharing.services.RecipeSharingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/recipe")
public class RecipeSharingController {

    RecipeSharingService recipeSharingService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    public RecipeSharingController(RecipeSharingService recipeSharingService) {
        this.recipeSharingService = recipeSharingService;
    }

    @PostMapping(path = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> createRecipe(@RequestHeader("Authorization") String bearerToken,
            @RequestBody Recipe recipe) {
        String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
        try {
            return ResponseEntity.ok().body(
                    DefaultResponse.builder()
                            .data(recipeSharingService.insert(recipe
                                    .toBuilder()
                                    .username(username)
                                    .build()))
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(DefaultResponse.builder()
                            .message("Internal server error")
                            .build());
        }
    }

    @PutMapping(path = "/{recipeId}/update", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> updateRecipe(@PathVariable String recipeId,
            @RequestBody Recipe recipe) {
        try {
            String id = recipeSharingService.update(recipeId, recipe);
            return ResponseEntity.ok().body(DefaultResponse.builder()
                    .id(id)
                    .message("Updated recipe")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(DefaultResponse.builder()
                            .message("Internal server error")
                            .build());
        }
    }


    @PutMapping(path = "/{recipeId}/action", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> postAction(@RequestHeader("Authorization") String bearerToken,
            @PathVariable String recipeId,
            @RequestBody Action action) {
        if(action != null) {
            if (Action.ActionType.COMMENT.equals(action.getActionType()) &&
                    StringUtils.isEmpty(action.getComment())) {
                return ResponseEntity.badRequest().body(DefaultResponse.builder()
                        .message("Empty comment")
                        .build());
            }
            String username = jwtTokenUtil.getUsernameFromToken(bearerToken.substring(7));
            String id = recipeSharingService.postAction(recipeId,
                    action.toBuilder()
                            .username(username)
                            .build());
            return ResponseEntity.ok().body(DefaultResponse
                    .builder()
                    .id(id)
                    .message("Updated recipe with the action")
                    .build());
        } else {
            return ResponseEntity.badRequest().body(DefaultResponse
                    .builder()
                    .message("Bad request. Nothing to post")
                    .build());
        }

    }

    @PostMapping(path = "/{recipeId}/images/upload")
    public ResponseEntity<DefaultResponse> postAction(@RequestParam("images") MultipartFile[] multipartFiles,
            @PathVariable String recipeId) {
        try {
            String id = recipeSharingService.uploadImages(recipeId,
                    multipartFiles);
            return ResponseEntity.ok().body(DefaultResponse
                    .builder()
                    .id(id)
                    .message("images uploaded successfully")
                    .build());
        } catch (HttpMediaTypeNotSupportedException e) {
            return ResponseEntity.badRequest().body(DefaultResponse
                    .builder()
                    .message("not supported image type")
                    .build());
        }

    }

    @GetMapping(path = {"/{recipeId}/images/download"})
    public ResponseEntity<DefaultResponse> getImage(@PathVariable String recipeId) throws IOException {
        List<byte[]> imgBytes = recipeSharingService.getImages(recipeId);
        if (imgBytes != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(APPLICATION_JSON_VALUE))
                    .body(DefaultResponse
                            .builder()
                            .data(imgBytes)
                            .build());
        } else {
            return ResponseEntity.ok()
                    .body(
                            DefaultResponse
                                    .builder()
                                    .message("images not found")
                                    .build()
                    );
        }
    }

    @GetMapping(path = "/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> getRecipes() {
        return ResponseEntity.ok()
                .body(DefaultResponse
                        .builder()
                        .data(recipeSharingService.listRecipes())
                        .build());
    }

    @PostMapping(path = "/list_with_filter", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> getRecipesWithFilter(@RequestBody RecipeFilter recipeFilter) {
        return ResponseEntity.ok()
                .body(DefaultResponse
                        .builder()
                        .data(recipeSharingService.listRecipes(recipeFilter))
                        .build());
    }

    @DeleteMapping(path = "/{recipeId}")
    public ResponseEntity<DefaultResponse> deleteRecipe(@PathVariable String recipeId) {
        try {
            return ResponseEntity.ok().body(DefaultResponse
                    .builder()
                    .message("deleted recipe successfully")
                    .id(recipeSharingService.delete(recipeId))
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(DefaultResponse.builder()
                            .message("Internal server error")
                            .build());
        }
    }
}
