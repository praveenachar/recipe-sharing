package com.assignment.recipe_sharing.services;

import com.assignment.recipe_sharing.security.models.UpdateUser;
import com.assignment.recipe_sharing.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    UserService userService;

    @Autowired
    UserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UpdateUser> dbUsers = userService.getCreds(username);
        if (dbUsers.size() == 1) {
            return new User(dbUsers.get(0).getUsername(), dbUsers.get(0).getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
