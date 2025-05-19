package com.eventticket.booking.model;

// Inheritance: RegularTicket extends Ticket
public class RegularTicket extends Ticket {
    private static final long serialVersionUID = 1L;
    
    private String section;
    
    public RegularTicket() {
        super();
        this.section = "General";
    }
    
    public RegularTicket(String eventName, String userName, String userEmail, String userPhone, 
                        double price, String section) {
        super(eventName, userName, userEmail, userPhone, price);
        this.section = section;
    }
    
    public String getSection() {
        return section;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    // Polymorphism: Override the process method
    @Override
    public void process() {
        setStatus("CONFIRMED");
        System.out.println("Processing Regular ticket: " + getId() + " for section: " + section);
    }
    
    @Override
    public String toString() {
        return super.toString() + ",REGULAR," + section;
    }
}
