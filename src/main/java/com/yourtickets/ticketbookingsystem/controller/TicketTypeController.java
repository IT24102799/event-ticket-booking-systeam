package com.yourtickets.ticketbookingsystem.controller;

import com.yourtickets.ticketbookingsystem.model.Event;
import com.yourtickets.ticketbookingsystem.model.TicketType;
import com.yourtickets.ticketbookingsystem.service.EventService;
import com.yourtickets.ticketbookingsystem.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events/{eventId}/ticket-types")
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeService;

    @Autowired
    private EventService eventService;

    @GetMapping
    public String listTicketTypes(@PathVariable Long eventId, Model model) {
        Optional<Event> eventOpt = eventService.getEventById(eventId);
        if (!eventOpt.isPresent()) {
            return "redirect:/events";
        }

        Event event = eventOpt.get();
        List<TicketType> ticketTypes = ticketTypeService.getTicketTypesByEventId(eventId);

        model.addAttribute("event", event);
        model.addAttribute("ticketTypes", ticketTypes);
        model.addAttribute("newTicketType", new TicketType());

        return "ticket-types";
    }

    @PostMapping
    public String createTicketType(
            @PathVariable Long eventId,
            @ModelAttribute TicketType ticketType,
            RedirectAttributes redirectAttributes) {

        Optional<Event> eventOpt = eventService.getEventById(eventId);
        if (!eventOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        TicketType savedTicketType = ticketTypeService.createTicketType(ticketType, eventId);
        redirectAttributes.addFlashAttribute("message", "Ticket type created successfully!");

        return "redirect:/events/" + eventId + "/ticket-types";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long eventId,
            @PathVariable Long id,
            Model model) {

        Optional<Event> eventOpt = eventService.getEventById(eventId);
        Optional<TicketType> ticketTypeOpt = ticketTypeService.getTicketTypeById(id);

        if (!eventOpt.isPresent() || !ticketTypeOpt.isPresent()) {
            return "redirect:/events";
        }

        Event event = eventOpt.get();
        TicketType ticketType = ticketTypeOpt.get();

        // Verify this ticket type belongs to this event
        if (!ticketType.getEventId().equals(eventId)) {
            return "redirect:/events/" + eventId + "/ticket-types";
        }

        model.addAttribute("event", event);
        model.addAttribute("ticketType", ticketType);

        return "ticket-type-form";
    }

    @PostMapping("/{id}")
    public String updateTicketType(
            @PathVariable Long eventId,
            @PathVariable Long id,
            @ModelAttribute TicketType ticketType,
            RedirectAttributes redirectAttributes) {

        Optional<TicketType> existingTicketTypeOpt = ticketTypeService.getTicketTypeById(id);
        if (!existingTicketTypeOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Ticket type not found!");
            return "redirect:/events/" + eventId + "/ticket-types";
        }

        // Ensure we're updating the correct ticket type
        TicketType existingTicketType = existingTicketTypeOpt.get();
        if (!existingTicketType.getEventId().equals(eventId)) {
            redirectAttributes.addFlashAttribute("error", "Ticket type does not belong to this event!");
            return "redirect:/events/" + eventId + "/ticket-types";
        }

        // Preserve the event ID
        ticketType.setEventId(eventId);

        TicketType updatedTicketType = ticketTypeService.updateTicketType(id, ticketType);
        if (updatedTicketType != null) {
            redirectAttributes.addFlashAttribute("message", "Ticket type updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update ticket type!");
        }

        return "redirect:/events/" + eventId + "/ticket-types";
    }

    @PostMapping("/{id}/delete")
    public String deleteTicketType(
            @PathVariable Long eventId,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Optional<TicketType> ticketTypeOpt = ticketTypeService.getTicketTypeById(id);
        if (!ticketTypeOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Ticket type not found!");
            return "redirect:/events/" + eventId + "/ticket-types";
        }

        // Ensure we're deleting the correct ticket type
        TicketType ticketType = ticketTypeOpt.get();
        if (!ticketType.getEventId().equals(eventId)) {
            redirectAttributes.addFlashAttribute("error", "Ticket type does not belong to this event!");
            return "redirect:/events/" + eventId + "/ticket-types";
        }

        boolean deleted = ticketTypeService.deleteTicketType(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Ticket type deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete ticket type!");
        }

        return "redirect:/events/" + eventId + "/ticket-types";
    }
}
