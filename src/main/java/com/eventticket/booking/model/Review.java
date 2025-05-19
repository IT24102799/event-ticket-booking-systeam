package com.yourtickets.ticketbookingsystem.model;

public class Review {
    private String id;
    private String type; // "platform" or "event"
    private String eventId; // null for platform reviews
    private String userName;
    private int rating;
    private String comment;
    private String date;

    public Review() {
    }

    public Review(String id, String type, String eventId, String userName, int rating, String comment, String date) {
        this.id = id;
        this.type = type;
        this.eventId = eventId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
