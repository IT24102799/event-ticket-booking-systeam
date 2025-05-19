package com.eventticket.booking.controller;

import com.eventticket.booking.model.Ticket;
import com.eventticket.booking.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        return ticketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<Ticket>> getTicketsByPaymentId(@PathVariable String paymentId) {
        List<Ticket> tickets = ticketService.getTicketsByPaymentId(paymentId);
        return ResponseEntity.ok(tickets);
    }
}