package com.agnelle.backend.controller;

import com.agnelle.backend.entity.User;
import com.agnelle.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{slug}")
    public ResponseEntity<?> getUserBySlug(@PathVariable String slug) {
        try {
            User user = userService.getUserBySlug(slug);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Slug not found");
            }

            return ResponseEntity.ok(user);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/{userSlug}")
    public ResponseEntity<?> editUserBySlug(@PathVariable String userSlug, @RequestBody User user) {
        try {
            User editUser = userService.editRegisteredUser(userSlug, user);
            return ResponseEntity.ok("User edited successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
