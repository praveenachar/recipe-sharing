package com.assignment.recipe_sharing.security.controllers;


import com.assignment.recipe_sharing.security.models.UpdateUser;
import com.assignment.recipe_sharing.security.services.UserService;
import com.assignment.recipe_sharing.models.DefaultResponse;
import com.assignment.recipe_sharing.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/add", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> addUser(@RequestBody User user) {
        String id = userService.insert(user);
        return ResponseEntity.ok()
                .body(DefaultResponse.builder()
                        .id(id)
                        .message("user created successfully")
                        .build());
    }

    @PostMapping(path = "/{userId}/update", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> addUser(@PathVariable String userId,
            @RequestBody UpdateUser updateUser) {
        try {
            String id = userService.updatedUser(userId, updateUser);
            return ResponseEntity.ok()
                    .body(DefaultResponse.builder()
                            .id(id)
                            .message("user updated")
                            .build());
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(DefaultResponse.builder()
                    .message(e.getMessage())
                    .build());
        }
    }

    @PostMapping(path = "/{userId}/image/upload")
    public ResponseEntity<DefaultResponse> uploadImage(@RequestParam("image") MultipartFile multipartFile,
            @PathVariable String userId) {
        try {
            String id = userService.uploadImage(userId, multipartFile);
            return ResponseEntity.ok().body(DefaultResponse
                    .builder().id(id).message("image uploaded successfully").build());
        } catch (HttpMediaTypeNotSupportedException e) {
            return ResponseEntity.badRequest().body(DefaultResponse.builder()
                    .message("image type not supported")
                    .build());
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = {"/{userId}/image/download"})
    public ResponseEntity<?> getImage(@PathVariable String userId) throws IOException {
        List<byte[]> imgBytes = userService.getImage(userId);
        if (imgBytes != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(MediaType.IMAGE_JPEG.toString()))
                    .body(imgBytes.get(0));
        } else {
            return ResponseEntity.status(404)
                    .body(DefaultResponse.builder()
                            .message("image not found")
                            .build());
        }

    }
}
