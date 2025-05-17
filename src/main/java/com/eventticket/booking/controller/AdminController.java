package com.eventticket.booking.controller;

import com.eventticket.booking.model.Event;
import com.eventticket.booking.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}
