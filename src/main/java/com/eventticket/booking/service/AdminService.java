package com.eventticket.booking.service;

import com.eventticket.booking.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private List<Admin> admins = new ArrayList<>();

    @Autowired
    private FileStorageService fileStorageService;

    public List<Admin> getAllAdmins() {
        return fileStorageService.readAdmins();
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

}
