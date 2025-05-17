package com.eventticket.booking.service;

import com.eventticket.booking.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserService {
    private Map<Long, User> users;
    private AtomicLong userIdCounter;
    private final ReentrantLock userLock = new ReentrantLock();

    @Autowired
    private FileStorageService fileStorageService;

    @PostConstruct
    public void init() {
        // Initialize with empty maps in case file loading fails
        this.users = new ConcurrentHashMap<>();
        this.userIdCounter = new AtomicLong(1);
        
        try {
            // Try to load from files
            Map<Long, User> loadedUsers = fileStorageService.loadUsers();
            if (loadedUsers != null && !loadedUsers.isEmpty()) {
                this.users = loadedUsers;
                this.userIdCounter = fileStorageService.loadUserCounter();
                System.out.println("Successfully loaded " + users.size() + " users from file");
            } else {
                System.out.println("No users loaded from file, creating default admin");
                createDefaultAdmin();
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            createDefaultAdmin();
        }
    }
    
    private void createDefaultAdmin() {
        User adminUser = new User("Admin", "admin@yourticket.lk", "+94 77 123 4567", "admin123");
        adminUser.setRole("ADMIN");
        adminUser.setId(1L);
        users.put(1L, adminUser);
        userIdCounter.set(2L);
        
        try {
            fileStorageService.saveUsers(users);
            fileStorageService.saveUserCounter(userIdCounter);
            System.out.println("Created and saved default admin user");
        } catch (Exception e) {
            System.err.println("Error saving default admin: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> getUserByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    public User createUser(User user) {
        try {
            userLock.lock();
            
            System.out.println("Creating new user: " + user.getName() + " with email: " + user.getEmail());

            // Check if email already exists
            if (getUserByEmail(user.getEmail()).isPresent()) {
                System.out.println("Email already in use: " + user.getEmail());
                throw new RuntimeException("Email already in use");
            }

            // Ensure ID is set
            user.setId(userIdCounter.getAndIncrement());

            // Set default role if not specified
            if (user.getRole() == null) {
                user.setRole("USER");
            }

            // Add to users map
            users.put(user.getId(), user);
            System.out.println("Added user to map with ID: " + user.getId());
            
            // Save to file
            try {
                fileStorageService.saveUsers(users);
                fileStorageService.saveUserCounter(userIdCounter);
                System.out.println("Saved users to file. Total users: " + users.size());
            } catch (Exception e) {
                System.err.println("Error saving users to file: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Print all users for debugging
            System.out.println("Current users in system after registration:");
            users.values().forEach(u -> 
                System.out.println("User: " + u.getId() + " - " + u.getName() + " - " + u.getEmail()));
                
            return user;
        } finally {
            userLock.unlock();
        }
    }

    public User updateUser(Long id, User userDetails) {
        try {
            userLock.lock();
            User existingUser = users.get(id);
            if (existingUser != null) {
                // Check if email is being changed and if it's already in use
                if (!existingUser.getEmail().equals(userDetails.getEmail()) &&
                        getUserByEmail(userDetails.getEmail()).isPresent()) {
                    throw new RuntimeException("Email already in use");
                }

                existingUser.setName(userDetails.getName());
                existingUser.setEmail(userDetails.getEmail());
                existingUser.setPhone(userDetails.getPhone());

                // Only update password if provided
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    existingUser.setPassword(userDetails.getPassword());
                }

                // Only allow role updates if explicitly provided
                if (userDetails.getRole() != null) {
                    existingUser.setRole(userDetails.getRole());
                }

                users.put(id, existingUser);
                fileStorageService.saveUsers(users);
                return existingUser;
            }
            return null;
        } finally {
            userLock.unlock();
        }
    }

    public boolean deleteUser(Long id) {
        try {
            userLock.lock();
            boolean removed = users.remove(id) != null;
            if (removed) {
                fileStorageService.saveUsers(users);
            }
            return removed;
        } finally {
            userLock.unlock();
        }
    }

    public Optional<User> authenticateUser(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        System.out.println("Authenticating user with email: " + email);
        System.out.println("Current users in system: " + users.size());
        
        // Print all users for debugging
        users.values().forEach(user -> 
            System.out.println("User in system: " + user.getId() + " - " + user.getEmail() + " - " + user.getPassword()));
        
        Optional<User> userOpt = users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
                
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Found user: " + user.getName() + " with email: " + user.getEmail());
            
            // Check if password matches
            if (password.equals(user.getPassword())) {
                System.out.println("Password matches, authentication successful");
                return Optional.of(user);
            } else {
                System.out.println("Password does not match");
                return Optional.empty();
            }
        } else {
            System.out.println("No user found with email: " + email);
            return Optional.empty();
        }
    }
}






