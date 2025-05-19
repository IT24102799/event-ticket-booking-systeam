package com.eventticket.booking.model;

// Inheritance: VIPTicket extends Ticket
public class VIPTicket extends Ticket {
    private static final long serialVersionUID = 1L;
    
    private String vipBenefits;
    
    public VIPTicket() {
        super();
        this.vipBenefits = "Early entry, premium seating";
    }
    
    public VIPTicket(String eventName, String userName, String userEmail, String userPhone, 
                    double price, String vipBenefits) {
        super(eventName, userName, userEmail, userPhone, price);
        this.vipBenefits = vipBenefits;
    }
    
    public String getVipBenefits() {
        return vipBenefits;
    }
    
    public void setVipBenefits(String vipBenefits) {
        this.vipBenefits = vipBenefits;
    }
    
    // Polymorphism: Override the process method
    @Override
    public void process() {
        setStatus("CONFIRMED");
        System.out.println("Processing VIP ticket: " + getId() + " with benefits: " + vipBenefits);
    }
    
    @Override
    public String toString() {
        return super.toString() + ",VIP," + vipBenefits;
    }
}
