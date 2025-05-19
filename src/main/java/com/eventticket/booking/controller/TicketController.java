package com.eventticket.booking.controller;


import com.eventticket.booking.model.RegularTicket;
import com.eventticket.booking.model.Ticket;
import com.eventticket.booking.model.VIPTicket;
import com.eventticket.booking.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Tickets home page
    @GetMapping("/tickets-home")
    public String ticketsHome(Model model) {
        model.addAttribute("pageTitle", "Tickets Home");

        // Get all tickets for display
        List<Ticket> allTickets = ticketService.getAllTickets();
        model.addAttribute("tickets", allTickets);

        // Count tickets by status
        long pendingCount = allTickets.stream()
                .filter(t -> "PENDING".equals(t.getStatus()))
                .count();

        long processedCount = allTickets.stream()
                .filter(t -> "PROCESSED".equals(t.getStatus()))
                .count();

        model.addAttribute("totalCount", allTickets.size());
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("processedCount", processedCount);

        return "tickets-home";
    }

    // Show ticket request form
    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("pageTitle", "Request Ticket");
        model.addAttribute("regularTicket", new RegularTicket());
        model.addAttribute("vipTicket", new VIPTicket());
        return "request-form";
    }

    // Process regular ticket request
    @PostMapping("/request/regular")
    public String processRegularTicketRequest(@ModelAttribute RegularTicket ticket,
                                            RedirectAttributes redirectAttributes) {
        try {
            ticketService.createTicket(ticket);
            redirectAttributes.addFlashAttribute("message", "Regular ticket request submitted successfully!");
            return "redirect:/confirmation?id=" + ticket.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create ticket: " + e.getMessage());
            return "redirect:/request";
        }
    }

    // Process VIP ticket request
    @PostMapping("/request/vip")
    public String processVIPTicketRequest(@ModelAttribute VIPTicket ticket,
                                        RedirectAttributes redirectAttributes) {
        try {
            ticketService.createTicket(ticket);
            redirectAttributes.addFlashAttribute("message", "VIP ticket request submitted successfully!");
            return "redirect:/confirmation?id=" + ticket.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create ticket: " + e.getMessage());
            return "redirect:/request";
        }
    }

    // Show ticket confirmation page
    @GetMapping("/confirmation")
    public String showConfirmation(@RequestParam String id, Model model, RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketService.getTicketById(id);
        if (ticket == null) {
            redirectAttributes.addFlashAttribute("error", "Ticket not found");
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Ticket Confirmation");
        model.addAttribute("ticket", ticket);
        return "confirmation";
    }

    // Show ticket queue status
    @GetMapping("/queue")
    public String showQueue(Model model) {
        model.addAttribute("pageTitle", "Queue Status");
        model.addAttribute("tickets", ticketService.getAllTickets());
        return "queue";
    }

    // Show all tickets
    @GetMapping("/tickets")
    public String showAllTickets(Model model) {
        model.addAttribute("pageTitle", "All Tickets");
        model.addAttribute("tickets", ticketService.getAllTickets());
        return "ticket-list";
    }

    // Process next ticket in queue
    @PostMapping("/process-next")
    public String processNextTicket(RedirectAttributes redirectAttributes) {
        try {
            Ticket processed = ticketService.processNextTicket();
            if (processed != null) {
                redirectAttributes.addFlashAttribute("message",
                    "Processed ticket: " + processed.getId() + " for " + processed.getUserName());
            } else {
                redirectAttributes.addFlashAttribute("message", "No pending tickets in the queue.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process ticket: " + e.getMessage());
        }
        return "redirect:/queue";
    }

    // Update ticket status
    @PostMapping("/update-status")
    public String updateTicketStatus(@RequestParam String id,
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes) {
        try {
            boolean updated = ticketService.updateTicketStatus(id, status);
            if (updated) {
                redirectAttributes.addFlashAttribute("message", "Ticket status updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to update ticket status: Ticket not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update ticket status: " + e.getMessage());
        }
        return "redirect:/tickets";
    }

    // Delete ticket
    @PostMapping("/delete")
    public String deleteTicket(@RequestParam String id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = ticketService.deleteTicket(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("message", "Ticket deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to delete ticket: Ticket not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete ticket: " + e.getMessage());
        }
        return "redirect:/tickets";
    }
}
