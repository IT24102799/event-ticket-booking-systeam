package com.eventticket.booking.controller;

import com.eventticket.booking.model.Event;
import com.eventticket.booking.model.User;
import com.eventticket.booking.service.EventService;
import com.eventticket.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping("/admin-dashboard")
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        List<Event> events = eventService.getAllEvents();
        List<User> users = userService.getAllUsers();
        
        model.addAttribute("eventCount", events.size());
        model.addAttribute("userCount", users.size());
        
        // Get recent users (up to 5)
        List<User> recentUsers = users.stream()
                .sorted(Comparator.comparing(User::getId).reversed())
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentUsers", recentUsers);
        
        return "dashboard-admin";
    }

    @GetMapping("/admin/events")
    public String manageEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "event-admin";
    }

    @GetMapping("/admin/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user-admin";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user!");
        }
        return "redirect:/admin/users";
    }
}
