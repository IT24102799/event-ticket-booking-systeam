package com.eventticket.booking.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Ticket {
    private String id;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String userName;
    private String ticketTypeName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String paymentId;
    private LocalDateTime purchaseDate;
    private String status; // "CONFIRMED", "CANCELLED", "PENDING"
    
    public Ticket() {
        this.purchaseDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Ticket(Long eventId, String eventName, Long userId, String userName, 
                 String ticketTypeName, int quantity, BigDecimal unitPrice, String paymentId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.userId = userId;
        this.userName = userName;
        this.ticketTypeName = ticketTypeName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.paymentId = paymentId;
        this.purchaseDate = LocalDateTime.now();
        this.status = "CONFIRMED";
    }
}