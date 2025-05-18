package com.yourtickets.ticketbookingsystem.controller;

import com.yourtickets.ticketbookingsystem.model.Review;
import com.yourtickets.ticketbookingsystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/platform")
    public List<Review> getPlatformReviews() {
        return reviewService.getPlatformReviews();
    }

    @GetMapping("/event/{eventId}")
    public List<Review> getEventReviews(@PathVariable String eventId) {
        return reviewService.getEventReviews(eventId);
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        Review savedReview = reviewService.addReview(review);
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/reviews", savedReview);
        }
        return savedReview;
    }

    @PutMapping("/{id}")
    public Review updateReview(@PathVariable String id, @RequestBody Review review) {
        Review updatedReview = reviewService.updateReview(id, review);
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/reviews", updatedReview);
        }
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/reviews", "Review deleted: " + id);
        }
    }
}
