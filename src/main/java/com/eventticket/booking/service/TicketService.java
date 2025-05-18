package com.eventticket.booking.service;


import com.eventticket.booking.model.Ticket;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {
    private static final String TICKETS_FILE = "tickets.txt";
    
    // Create a new ticket and save it to the file
    public Ticket createTicket(Ticket ticket) {
        // Ensure the file exists
        createFileIfNotExists();
        
        // Write the ticket to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TICKETS_FILE, true))) {
            writer.write(ticket.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save ticket", e);
        }
        
        return ticket;
    }
    
    // Get all tickets in FIFO order (implementing Queue concept)
    public List<Ticket> getAllTickets() {
        createFileIfNotExists();
        
        try {
            return Files.lines(Paths.get(TICKETS_FILE))
                    .map(Ticket::fromString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Get a specific ticket by ID
    public Ticket getTicketById(String id) {
        return getAllTickets().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Update a ticket's status
    public boolean updateTicketStatus(String id, String status) {
        List<Ticket> tickets = getAllTickets();
        boolean updated = false;
        
        for (Ticket ticket : tickets) {
            if (ticket.getId().equals(id)) {
                ticket.setStatus(status);
                updated = true;
                break;
            }
        }
        
        if (updated) {
            saveAllTickets(tickets);
        }
        
        return updated;
    }
    
    // Delete a ticket
    public boolean deleteTicket(String id) {
        List<Ticket> tickets = getAllTickets();
        boolean removed = tickets.removeIf(t -> t.getId().equals(id));
        
        if (removed) {
            saveAllTickets(tickets);
        }
        
        return removed;
    }
    
    // Process the next ticket in the queue (FIFO)
    public Ticket processNextTicket() {
        List<Ticket> tickets = getAllTickets();
        
        // Find the first pending ticket
        Optional<Ticket> nextTicket = tickets.stream()
                .filter(t -> "PENDING".equals(t.getStatus()))
                .findFirst();
        
        if (nextTicket.isPresent()) {
            Ticket ticket = nextTicket.get();
            ticket.process(); // Polymorphic call
            saveAllTickets(tickets);
            return ticket;
        }
        
        return null;
    }
    
    // Helper method to save all tickets back to the file
    private void saveAllTickets(List<Ticket> tickets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TICKETS_FILE))) {
            for (Ticket ticket : tickets) {
                writer.write(ticket.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update tickets file", e);
        }
    }
    
    // Helper method to create the file if it doesn't exist
    private void createFileIfNotExists() {
        File file = new File(TICKETS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create tickets file", e);
            }
        }
    }
}
