package com.eventticket.booking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {
    
    @PostMapping("/contact")
    public String handleContactForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {
        
        // TODO: Add email service implementation
        
        redirectAttributes.addFlashAttribute("message", 
            "Thank you for your message. We'll get back to you soon!");
        return "redirect:/about";
    }
}
