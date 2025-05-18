package com.yourtickets.ticketbookingsystem.controller;

import com.yourtickets.ticketbookingsystem.model.Event;
import com.yourtickets.ticketbookingsystem.model.Review;
import com.yourtickets.ticketbookingsystem.service.EventService;
import com.yourtickets.ticketbookingsystem.service.ReviewService;
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

    @Autowired
    private ReviewService reviewService;

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
    @GetMapping("/admin/reviews")
    public String ReviewManage(Model model) {
        List<Event> events = eventService.getAllEvents();
        List<Review> allReviews = reviewService.getAllReviews();
        List<Review> platformReviews = reviewService.getPlatformReviews();

        model.addAttribute("events", events);
        model.addAttribute("allReviews", allReviews);
        model.addAttribute("platformReviews", platformReviews);

        return "reviews-admin";
    }
}
