package com.eventticket.booking.controller;

import com.eventticket.booking.model.Admin;
import com.eventticket.booking.model.Event;
import com.eventticket.booking.model.User;
import com.eventticket.booking.service.AdminService;
import com.eventticket.booking.service.EventService;
import com.eventticket.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin-login")
    public String adminLoginPage() {
        System.out.println("Handling /admin-login request");
        return "login-admin";
    }

    @GetMapping("/admin-dashboard")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        // Get events data
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("eventCount", events.size());

        // Get users data
        List<User> users = userService.getAllUsers();
        model.addAttribute("userCount", users.size());

        // Get recent users (up to 5)
        List<User> recentUsers = users.stream()
                .limit(5)
                .toList();
        model.addAttribute("recentUsers", recentUsers);

        System.out.println("Dashboard data: " + events.size() + " events, " + users.size() + " users, " + recentUsers.size() + " recent users");

        return "dashboard-admin";
    }

    @GetMapping("/admin/events")
    public String manageEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "event-admin";
    }

    @GetMapping("/admin/management")
    public String manageAdmins(Model model) {
        System.out.println("AdminController.manageAdmins() called");
        List<Admin> admins = adminService.getAllAdmins();
        System.out.println("Adding " + admins.size() + " admins to model");
        model.addAttribute("admins", admins);
        return "admin-management";
    }

    @Autowired
    private AdminService adminService;

    // REST API endpoints for admin operations
    @RestController
    @RequestMapping("/api/admins")
    public static class AdminApiController {

        @Autowired
        private AdminService adminService;

        @GetMapping
        public ResponseEntity<List<Admin>> getAllAdmins() {
            List<Admin> admins = adminService.getAllAdmins();
            System.out.println("API returning " + admins.size() + " admins");
            for (Admin admin : admins) {
                System.out.println("Admin: " + admin.getAdminId() + ", " + admin.getUserName());
            }
            return ResponseEntity.ok(admins);
        }

        @GetMapping("/{adminId}")
        public ResponseEntity<Admin> getAdminById(@PathVariable String adminId) {
            Optional<Admin> admin = adminService.getAdminById(adminId);
            return admin.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        }

        @PostMapping
        public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
            adminService.addAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin added successfully");
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
            try {
                String email = credentials.get("email");
                String password = credentials.get("password");

                System.out.println("Admin login attempt for email: " + email);

                Optional<Admin> authenticatedAdmin = adminService.authenticateAdmin(email, password);
                if (authenticatedAdmin.isPresent()) {
                    Admin admin = authenticatedAdmin.get();
                    System.out.println("Admin login successful for: " + admin.getUserName());
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "adminId", admin.getAdminId(),
                        "email", admin.getUserName()
                    ));
                } else {
                    System.out.println("Admin login failed: Invalid credentials for email: " + email);
                    return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
                }
            } catch (Exception e) {
                System.out.println("Error during admin login: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("error", "An error occurred during login"));
            }
        }

        @DeleteMapping("/{adminId}")
        public ResponseEntity<String> deleteAdmin(@PathVariable String adminId) {
            Optional<Admin> admin = adminService.getAdminById(adminId);
            if (admin.isPresent()) {
                adminService.deleteAdmin(adminId);
                return ResponseEntity.ok("Admin deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }

        @PutMapping("/{adminId}")
        public ResponseEntity<String> updateAdmin(@PathVariable String adminId, @RequestBody Admin updatedAdmin) {
            Optional<Admin> admin = adminService.getAdminById(adminId);
            if (admin.isPresent()) {
                updatedAdmin.setAdminId(adminId);
                adminService.updateAdmin(updatedAdmin);
                return ResponseEntity.ok("Admin updated successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
        }
    }
}
