package com.yourtickets.ticketbookingsystem.controller;

import com.yourtickets.ticketbookingsystem.model.Event;
import com.yourtickets.ticketbookingsystem.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/")
    public String home(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/conditions")
    public String conditions() {
        return "conditions";
    }

    @GetMapping("/events")
    public String allEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @GetMapping("/events/{id}")
    public String eventDetails(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            return "event-details";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/events/create")
    public String showCreateForm(Model model) {
        Event event = new Event();
        event.setStartDateTime(LocalDateTime.now());
        event.setEndDateTime(LocalDateTime.now().plusHours(2));
        event.setPrice(BigDecimal.ZERO);
        event.setAvailableTickets(100);

        model.addAttribute("event", event);
        return "event-form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        Event savedEvent = eventService.createEvent(event);
        redirectAttributes.addFlashAttribute("message", "Event created successfully!");
        return "redirect:/events/" + savedEvent.getId();
    }

    @GetMapping("/events/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            model.addAttribute("event", eventOpt.get());
            return "event-form";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/events/{id}/update")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        Event updatedEvent = eventService.updateEvent(id, event);
        if (updatedEvent != null) {
            redirectAttributes.addFlashAttribute("message", "Event updated successfully!");
            return "redirect:/events/" + id;
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = eventService.deleteEvent(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Event deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete event!");
        }
        return "redirect:/";
    }
}
