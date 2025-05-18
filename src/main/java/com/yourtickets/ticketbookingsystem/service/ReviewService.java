package com.yourtickets.ticketbookingsystem.service;

import com.yourtickets.ticketbookingsystem.model.Review;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final String TEXT_FILE_PATH = "reviews.txt";
    private final List<Review> cachedReviews = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            File file = new File(TEXT_FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Created new reviews file at: " + file.getAbsolutePath());
                // Add some sample reviews
                addSampleReviews();
            } else {
                System.out.println("Using existing reviews file at: " + file.getAbsolutePath());
                // Load reviews from file
                cachedReviews.addAll(findAll());
            }
        } catch (IOException e) {
            System.err.println("Error with reviews file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addSampleReviews() {
        // Add a few sample platform reviews
        Review review1 = new Review();
        review1.setId(UUID.randomUUID().toString());
        review1.setType("platform");
        review1.setEventId(null);
        review1.setUserName("John Doe");
        review1.setRating(5);
        review1.setComment("Great platform! Very easy to use and find events.");
        review1.setDate(LocalDate.now().toString());
        
        Review review2 = new Review();
        review2.setId(UUID.randomUUID().toString());
        review2.setType("platform");
        review2.setEventId(null);
        review2.setUserName("Jane Smith");
        review2.setRating(4);
        review2.setComment("I love how simple it is to book tickets. Would recommend!");
        review2.setDate(LocalDate.now().minusDays(5).toString());
        
        save(review1);
        save(review2);
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(cachedReviews);
    }

    public List<Review> getPlatformReviews() {
        return cachedReviews.stream()
                .filter(review -> "platform".equals(review.getType()))
                .collect(Collectors.toList());
    }

    public List<Review> getEventReviews(String eventId) {
        return cachedReviews.stream()
                .filter(review -> "event".equals(review.getType()) && eventId.equals(review.getEventId()))
                .collect(Collectors.toList());
    }

    public Review addReview(Review review) {
        if (review.getId() == null) {
            review.setId(UUID.randomUUID().toString());
        }
        if (review.getDate() == null) {
            review.setDate(LocalDate.now().toString());
        }
        Review savedReview = save(review);
        cachedReviews.add(savedReview);
        return savedReview;
    }

    public Review updateReview(String id, Review review) {
        review.setId(id);
        Review updatedReview = save(review);
        
        // Update in cache
        for (int i = 0; i < cachedReviews.size(); i++) {
            if (cachedReviews.get(i).getId().equals(id)) {
                cachedReviews.set(i, updatedReview);
                break;
            }
        }
        
        return updatedReview;
    }

    public void deleteReview(String id) {
        List<Review> reviews = findAll();
        reviews = reviews.stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());
        writeReviews(reviews);
        
        // Remove from cache
        cachedReviews.removeIf(review -> review.getId().equals(id));
    }

    // Private helper methods
    private List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        File file = new File(TEXT_FILE_PATH);
        
        if (!file.exists()) {
            System.out.println("Reviews file not found, creating new file at: " + TEXT_FILE_PATH);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviews;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Review review = parseReview(line);
                    if (review != null) {
                        reviews.add(review);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private Review save(Review review) {
        List<Review> reviews = findAll();
        
        // Remove existing review with same ID if exists
        reviews = reviews.stream()
                .filter(r -> !r.getId().equals(review.getId()))
                .collect(Collectors.toList());
        
        // Add the new/updated review
        reviews.add(review);
        
        // Write all reviews back to file
        writeReviews(reviews);
        
        return review;
    }

    private Review parseReview(String line) {
        try {
            if (line == null || line.trim().isEmpty()) {
                return null;
            }
            
            String[] parts = line.split("\\|");
            if (parts.length < 7) {
                System.err.println("Invalid review format (not enough fields): " + line);
                return null;
            }
            
            Review review = new Review();
            review.setId(parts[0]);
            review.setType(parts[1]);
            review.setEventId("null".equals(parts[2]) ? null : parts[2]);
            review.setUserName(parts[3]);
            review.setRating(Integer.parseInt(parts[4]));
            review.setComment(parts[5]);
            review.setDate(parts[6]);
            return review;
        } catch (Exception e) {
            System.err.println("Error parsing review: " + line + " - " + e.getMessage());
            return null;
        }
    }

    private void writeReviews(List<Review> reviews) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEXT_FILE_PATH))) {
            for (Review review : reviews) {
                writer.write(formatReview(review));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatReview(Review review) {
        return String.join("|", 
                review.getId(),
                review.getType(),
                review.getEventId() == null ? "null" : review.getEventId(),
                review.getUserName(),
                String.valueOf(review.getRating()),
                review.getComment(),
                review.getDate());
    }
}
