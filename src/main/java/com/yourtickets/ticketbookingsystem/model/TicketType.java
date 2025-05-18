package com.yourtickets.ticketbookingsystem.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TicketType {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int availableQuantity;
    
    // Reference to parent event
    private Long eventId;
    
    public TicketType() {
    }
    
    public TicketType(String name, String description, BigDecimal price, int availableQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }
}
