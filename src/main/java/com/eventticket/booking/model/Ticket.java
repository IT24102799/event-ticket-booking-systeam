package com.eventticket.booking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Base Ticket class demonstrating encapsulation
public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String eventName;
    private String userName;
    private String userEmail;
    private String userPhone;
    private LocalDateTime requestTime;
    private String status; // PENDING, CONFIRMED, CANCELED
    private double price;
    
    public Ticket() {
        this.requestTime = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Ticket(String eventName, String userName, String userEmail, String userPhone, double price) {
        this();
        this.id = generateId();
        this.eventName = eventName;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.price = price;
    }
    
    // Generate a unique ID based on timestamp
    private String generateId() {
        return "TKT" + System.currentTimeMillis();
    }
    
    // Process method that will be overridden (polymorphism)
    public void process() {
        this.status = "CONFIRMED";
        System.out.println("Processing standard ticket: " + this.id);
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEventName() {
        return eventName;
    }
    
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserPhone() {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getFormattedRequestTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return requestTime.format(formatter);
    }
    
    @Override
    public String toString() {
        return id + "," + eventName + "," + userName + "," + userEmail + "," + 
               userPhone + "," + getFormattedRequestTime() + "," + status + "," + price;
    }
    
    // Parse a ticket from a string (from text file)
    public static Ticket fromString(String str) {
        String[] parts = str.split(",");
        if (parts.length < 8) {
            return null;
        }
        
        Ticket ticket = new Ticket();
        ticket.setId(parts[0]);
        ticket.setEventName(parts[1]);
        ticket.setUserName(parts[2]);
        ticket.setUserEmail(parts[3]);
        ticket.setUserPhone(parts[4]);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ticket.setRequestTime(LocalDateTime.parse(parts[5], formatter));
        
        ticket.setStatus(parts[6]);
        ticket.setPrice(Double.parseDouble(parts[7]));
        
        // Determine the correct ticket type
        if (parts.length > 8 && parts[8].equals("VIP")) {
            VIPTicket vipTicket = new VIPTicket();
            vipTicket.setId(ticket.getId());
            vipTicket.setEventName(ticket.getEventName());
            vipTicket.setUserName(ticket.getUserName());
            vipTicket.setUserEmail(ticket.getUserEmail());
            vipTicket.setUserPhone(ticket.getUserPhone());
            vipTicket.setRequestTime(ticket.getRequestTime());
            vipTicket.setStatus(ticket.getStatus());
            vipTicket.setPrice(ticket.getPrice());
            vipTicket.setVipBenefits(parts.length > 9 ? parts[9] : "Standard VIP benefits");
            return vipTicket;
        }
        
        return ticket;
    }
}
