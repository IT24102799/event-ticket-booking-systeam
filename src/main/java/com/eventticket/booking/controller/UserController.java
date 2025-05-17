package com.eventticket.booking.controller;

import com.eventticket.booking.model.User;
import com.eventticket.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // View mappings for user pages
    @GetMapping("/user")
    public String userLoginPage() {
        System.out.println("Handling /user request");
        return "user";
    }

    @GetMapping("/user-profile")
    public String userProfilePage() {
        System.out.println("Handling /user-profile request");
        return "user-profile";
    }
    
  
    
    @GetMapping("/contact")
    public String contactPage() {
        System.out.println("Handling /contact request");
        return "contact";
    }

    // REST API endpoints for user operations
    @RestController
    @RequestMapping("/api/users")
    public static class UserApiController {
        
        @Autowired
        private UserService userService;
        
        @GetMapping
        public ResponseEntity<List<User>> getAllUsers() {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
        
        @GetMapping("/{id}")
        public ResponseEntity<?> getUserById(@PathVariable Long id) {
            Optional<User> user = userService.getUserById(id);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        
        @PostMapping
        public ResponseEntity<?> createUser(@RequestBody User user) {
            try {
                User createdUser = userService.createUser(user);
                return ResponseEntity.status(201).body(createdUser);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }
        
        @PutMapping("/{id}")
        public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
            try {
                if (!id.equals(user.getId())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "ID in path must match ID in body"));
                }
                
                User updatedUser = userService.updateUser(id, user);
                if (updatedUser != null) {
                    return ResponseEntity.ok(updatedUser);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }
        
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteUser(@PathVariable Long id) {
            try {
                boolean deleted = userService.deleteUser(id);
                if (deleted) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                System.out.println("Error deleting user: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }
        
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
            try {
                String email = credentials.get("email");
                String password = credentials.get("password");
                
                System.out.println("Login attempt for email: " + email);
                
                Optional<User> authenticatedUser = userService.authenticateUser(email, password);
                if (authenticatedUser.isPresent()) {
                    User user = authenticatedUser.get();
                    System.out.println("Login successful for user: " + user.getName() + " (ID: " + user.getId() + ")");
                    return ResponseEntity.ok(user);
                } else {
                    System.out.println("Login failed: Invalid credentials for email: " + email);
                    return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
                }
            } catch (Exception e) {
                System.out.println("Login error: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }
    }
}



