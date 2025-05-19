package com.eventticket.booking.service;

import com.eventticket.booking.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import java.util.stream.Collectors;

@Service
public class TicketService {
    private Map<String, Ticket> tickets = new ConcurrentHashMap<>();
    private AtomicInteger ticketCounter = new AtomicInteger(1);
    private final ReentrantLock ticketLock = new ReentrantLock();
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private TicketTypeService ticketTypeService;
    
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }
    
    public List<Ticket> getTicketsByUserId(Long userId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    public List<Ticket> getTicketsByEventId(Long eventId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }
    
    public List<Ticket> getTicketsByPaymentId(String paymentId) {
        return tickets.values().stream()
                .filter(ticket -> paymentId.equals(ticket.getPaymentId()))
                .collect(Collectors.toList());
    }
    
    public Optional<Ticket> getTicketById(String id) {
        return Optional.ofNullable(tickets.get(id));
    }
    
    public Ticket createTicket(Ticket ticket) {
        ticketLock.lock();
        try {
            String ticketId = "TKT-" + String.format("%06d", ticketCounter.getAndIncrement());
            ticket.setId(ticketId);
            
            // Calculate total price if not set
            if (ticket.getTotalPrice() == null && ticket.getUnitPrice() != null) {
                ticket.setTotalPrice(ticket.getUnitPrice().multiply(BigDecimal.valueOf(ticket.getQuantity())));
            }
            
            // Set purchase date if not set
            if (ticket.getPurchaseDate() == null) {
                ticket.setPurchaseDate(LocalDateTime.now());
            }
            
            tickets.put(ticketId, ticket);
            return ticket;
        } finally {
            ticketLock.unlock();
        }
    }
    
    public Ticket updateTicket(String id, Ticket ticketDetails) {
        ticketLock.lock();
        try {
            Ticket existingTicket = tickets.get(id);
            if (existingTicket != null) {
                // Update fields but preserve ID
                ticketDetails.setId(id);
                tickets.put(id, ticketDetails);
                return ticketDetails;
            }
            return null;
        } finally {
            ticketLock.unlock();
        }
    }
    
    public boolean deleteTicket(String id) {
        ticketLock.lock();
        try {
            return tickets.remove(id) != null;
        } finally {
            ticketLock.unlock();
        }
    }
    
    public List<Ticket> createTicketsFromPayment(Map<String, Object> paymentDetails, String paymentId) {
        List<Ticket> createdTickets = new ArrayList<>();
        
        try {
            Long eventId = Long.valueOf(paymentDetails.get("eventId").toString());
            String eventName = paymentDetails.get("eventName").toString();
            Long userId = Long.valueOf(paymentDetails.get("userId").toString());
            String userName = paymentDetails.getOrDefault("userName", "Guest").toString();
            int ticketCount = Integer.parseInt(paymentDetails.get("ticketCount").toString());
            BigDecimal unitPrice = new BigDecimal(paymentDetails.get("unitPrice").toString());
            String ticketTypeName = paymentDetails.getOrDefault("ticketTypeName", "General Admission").toString();
            
            Ticket ticket = new Ticket(eventId, eventName, userId, userName, 
                                      ticketTypeName, ticketCount, unitPrice, paymentId);
            
            createdTickets.add(createTicket(ticket));
            
        } catch (Exception e) {
            // Log error
            System.err.println("Error creating tickets from payment: " + e.getMessage());
        }
        
        return createdTickets;

    }
}
