package com.yourtickets.ticketbookingsystem.model;

import com.yourtickets.ticketbookingsystem.model.TicketType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Event {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String venue;
    private BigDecimal price; // Default price for backward compatibility
    private int availableTickets; // Default available tickets for backward compatibility
    private String imageUrl;

    // List of ticket types for this event
    private List<TicketType> ticketTypes = new ArrayList<>();

    // Flag to indicate if this event has multiple ticket types
    private boolean hasMultipleTicketTypes = false;

    // Getters and setters are provided by Lombok @Getter and @Setter annotations



    public Event() {
    }

    public Event(String name, String description, LocalDateTime startDateTime,
                 LocalDateTime endDateTime, String venue, BigDecimal price,
                 int availableTickets, String imageUrl) {
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.venue = venue;
        this.price = price;
        this.availableTickets = availableTickets;
        this.imageUrl = imageUrl;
        this.hasMultipleTicketTypes = false;
    }

    /**
     * Adds a ticket type to this event
     * @param ticketType The ticket type to add
     */
    public void addTicketType(TicketType ticketType) {
        if (ticketType != null) {
            ticketType.setEventId(this.id);
            this.ticketTypes.add(ticketType);
            this.hasMultipleTicketTypes = true;
        }
    }

    /**
     * Gets the total number of available tickets across all ticket types
     * @return The total number of available tickets
     */
    public int getTotalAvailableTickets() {
        if (!hasMultipleTicketTypes) {
            return this.availableTickets;
        }

        int total = 0;
        for (TicketType ticketType : ticketTypes) {
            total += ticketType.getAvailableQuantity();
        }
        return total;
    }
}