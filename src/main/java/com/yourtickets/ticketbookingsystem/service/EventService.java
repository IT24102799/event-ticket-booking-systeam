package com.yourtickets.ticketbookingsystem.service;

import com.yourtickets.ticketbookingsystem.model.Event;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class EventService {
    private Map<Long, Event> events;
    private AtomicLong eventIdCounter;
    private final ReentrantLock eventLock = new ReentrantLock();



    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TicketTypeService ticketTypeService;

    @PostConstruct
    public void init() {
        this.events = fileStorageService.loadEvents();
        this.eventIdCounter = fileStorageService.loadCounter();

        // Initialize the ticket type service
        ticketTypeService.init();
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    public List<Event> getSortedEvents(EventSorter.SortCriteria criteria) {
        List<Event> eventList = new ArrayList<>(events.values());
        return EventSorter.mergeSort(eventList, criteria);
    }

    public Optional<Event> getEventById(Long id) {
        return Optional.ofNullable(events.get(id));
    }

    public Event createEvent(Event event) {
        try {
            eventLock.lock();
            event.setId(eventIdCounter.getAndIncrement());

            events.put(event.getId(), event);
            fileStorageService.saveEvents(events);
            fileStorageService.saveCounter(eventIdCounter);
            return event;
        } finally {
            eventLock.unlock();
        }
    }

    public Event updateEvent(Long id, Event eventDetails) {
        try {
            eventLock.lock();
            Event existingEvent = events.get(id);
            if (existingEvent != null) {


                existingEvent.setName(eventDetails.getName());
                existingEvent.setDescription(eventDetails.getDescription());
                existingEvent.setStartDateTime(eventDetails.getStartDateTime());
                existingEvent.setEndDateTime(eventDetails.getEndDateTime());
                existingEvent.setVenue(eventDetails.getVenue());
                existingEvent.setPrice(eventDetails.getPrice());

                existingEvent.setImageUrl(eventDetails.getImageUrl());
                events.put(id, existingEvent);
                fileStorageService.saveEvents(events);
                return existingEvent;
            }
            return null;
        } finally {
            eventLock.unlock();
        }
    }

    public boolean deleteEvent(Long id) {
        try {
            eventLock.lock();
            boolean removed = events.remove(id) != null;
            if (removed) {
                fileStorageService.saveEvents(events);
            }
            return removed;
        } finally {
            eventLock.unlock();
        }
    }


}