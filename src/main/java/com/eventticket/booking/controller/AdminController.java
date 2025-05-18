package com.eventticket.booking.controller;

import com.eventticket.booking.model.Event;
import com.eventticket.booking.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eventticket.booking.model.Admin;
import com.eventticket.booking.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private EventService eventService;

    @GetMapping("/admin-dashboard")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("eventCount", events.size());
        return "dashboard-admin";
    }

    @GetMapping("/admin/events")
    public String manageEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "event-admin";
    }

    @Autowired
    private AdminService adminService;


    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
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
