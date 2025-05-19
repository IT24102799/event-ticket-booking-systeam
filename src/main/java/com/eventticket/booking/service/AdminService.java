package com.eventticket.booking.service;

import com.eventticket.booking.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private FileStorageService fileStorageService;

    public List<Admin> getAllAdmins() {
        System.out.println("AdminService.getAllAdmins() called");
        List<Admin> admins = fileStorageService.readAdmins();
        System.out.println("Read " + admins.size() + " admins from storage");

        // Create a default admin if none exists
        if (admins.isEmpty()) {
            System.out.println("No admins found, creating default admin");
            Admin defaultAdmin = new Admin("admin1", "admin@yourtickets.lk", "admin123");
            addAdmin(defaultAdmin);
            admins = fileStorageService.readAdmins();
            System.out.println("After adding default admin, now have " + admins.size() + " admins");
        }

        return admins;
    }

    public Optional<Admin> getAdminById(String adminId) {
        return fileStorageService.readAdmins()
                .stream()
                .filter(admin -> admin.getAdminId().equals(adminId))
                .findFirst();
    }

    public void addAdmin(Admin newAdmin) {
        List<Admin> admins = fileStorageService.readAdmins();
        admins.add(newAdmin);
        fileStorageService.writeAdmins(admins);
    }

    public void deleteAdmin(String adminId) {
        List<Admin> admins = fileStorageService.readAdmins();
        admins.removeIf(admin -> admin.getAdminId().equals(adminId));
        fileStorageService.writeAdmins(admins);
    }

    public void updateAdmin(Admin updatedAdmin) {
        List<Admin> admins = fileStorageService.readAdmins();
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getAdminId().equals(updatedAdmin.getAdminId())) {
                admins.set(i, updatedAdmin);
                break;
            }
        }
        fileStorageService.writeAdmins(admins);
    }

    public Optional<Admin> authenticateAdmin(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        System.out.println("Authenticating admin with email: " + email);
        List<Admin> admins = getAllAdmins();
        System.out.println("Current admins in system: " + admins.size());

        // Print all admins for debugging
        admins.forEach(admin ->
            System.out.println("Admin in system: " + admin.getAdminId() + " - " + admin.getUserName() + " - " + admin.getPassword()));

        Optional<Admin> adminOpt = admins.stream()
                .filter(admin -> email.equals(admin.getUserName()))
                .findFirst();

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("Found admin with email: " + admin.getUserName());

            // Check if password matches
            if (password.equals(admin.getPassword())) {
                System.out.println("Password matches, authentication successful");
                return Optional.of(admin);
            } else {
                System.out.println("Password does not match");
                return Optional.empty();
            }
        } else {
            System.out.println("No admin found with email: " + email);
            return Optional.empty();
        }
    }

}
