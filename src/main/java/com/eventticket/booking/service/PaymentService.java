package com.eventticket.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import jakarta.annotation.PostConstruct;
import java.io.*;

@Service
public class PaymentService {
    private Map<String, Map<String, Object>> payments;
    private AtomicInteger paymentIdCounter;
    private final ReentrantLock paymentLock = new ReentrantLock();
    private final String PAYMENTS_FILE = "data/payments.txt";
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private TicketService ticketService;
    
    @PostConstruct
    public void init() {
        // Initialize with empty map
        this.payments = new ConcurrentHashMap<>();
        this.paymentIdCounter = new AtomicInteger(1);
        
        // Load existing payments from file
        loadPaymentsFromFile();
    }
    
    private void loadPaymentsFromFile() {
        File file = new File(PAYMENTS_FILE);
        if (!file.exists()) {
            // Create directory if it doesn't exist
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                try {
                    // Parse the payment data from the line
                    String[] parts = line.split("\\|");
                    if (parts.length < 3) continue;
                    
                    String paymentId = parts[0];
                    Map<String, Object> payment = new HashMap<>();
                    payment.put("id", paymentId);
                    payment.put("timestamp", Long.parseLong(parts[1]));
                    payment.put("status", parts[2]);
                    
                    // Add additional fields if available
                    if (parts.length > 3) payment.put("eventName", parts[3]);
                    if (parts.length > 4) payment.put("userName", parts[4]);
                    if (parts.length > 5) payment.put("total", parts[5]);
                    if (parts.length > 6) payment.put("ticketCount", parts[6]);
                    if (parts.length > 7) payment.put("eventId", parts[7]);
                    if (parts.length > 8) payment.put("ticketTypeName", parts[8]);
                    
                    payments.put(paymentId, payment);
                    
                    // Update counter to be greater than the highest ID
                    int idNumber = Integer.parseInt(paymentId.substring(4));
                    if (idNumber >= paymentIdCounter.get()) {
                        paymentIdCounter.set(idNumber + 1);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing payment line: " + line);
                    e.printStackTrace();
                }
            }
            System.out.println("Loaded " + payments.size() + " payments from file");
        } catch (IOException e) {
            System.err.println("Error loading payments from file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void savePaymentsToFile() {
        try {
            // Create directory if it doesn't exist
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            File file = new File(PAYMENTS_FILE);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map<String, Object> payment : payments.values()) {
                    // Format: id|timestamp|status|eventName|userName|total|ticketCount|eventId|ticketTypeName
                    StringBuilder line = new StringBuilder();
                    line.append(payment.getOrDefault("id", "")).append("|");
                    line.append(payment.getOrDefault("timestamp", "")).append("|");
                    line.append(payment.getOrDefault("status", "COMPLETED")).append("|");
                    line.append(payment.getOrDefault("eventName", "")).append("|");
                    line.append(payment.getOrDefault("userName", "")).append("|");
                    line.append(payment.getOrDefault("total", "0.00")).append("|");
                    line.append(payment.getOrDefault("ticketCount", "1")).append("|");
                    line.append(payment.getOrDefault("eventId", "")).append("|");
                    line.append(payment.getOrDefault("ticketTypeName", "General Admission"));
                    
                    writer.write(line.toString());
                    writer.newLine();
                }
            }
            System.out.println("Saved " + payments.size() + " payments to file");
        } catch (IOException e) {
            System.err.println("Error saving payments to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Map<String, Object> processPayment(Map<String, Object> paymentDetails) {
        paymentLock.lock();
        try {
            String paymentId = "PAY-" + String.format("%03d", paymentIdCounter.getAndIncrement());
            
            // Add timestamp and ID
            Map<String, Object> payment = new HashMap<>(paymentDetails);
            payment.put("id", paymentId);
            payment.put("timestamp", System.currentTimeMillis());
            payment.put("status", "COMPLETED");
            
            // Store payment
            payments.put(paymentId, payment);
            
            // Create tickets for this payment
            ticketService.createTicketsFromPayment(paymentDetails, paymentId);
            
            // Save to file
            savePaymentsToFile();
            
            return payment;
        } finally {
            paymentLock.unlock();
        }
    }
    
    public List<Map<String, Object>> getAllPayments() {
        return new ArrayList<>(payments.values());
    }
    
    public boolean deletePayment(String paymentId) {
        boolean removed = payments.remove(paymentId) != null;
        if (removed) {
            savePaymentsToFile();
        }
        return removed;
    }
    
    public Map<String, Object> getPaymentById(String id) {
        return payments.get(id);
    }
}
