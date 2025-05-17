package com.eventticket.booking.service;

import com.eventticket.booking.model.Event;
import com.eventticket.booking.model.TicketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class TicketTypeService {
    private Map<Long, TicketType> ticketTypes;
    private AtomicLong ticketTypeIdCounter;
    private final ReentrantLock ticketTypeLock = new ReentrantLock();

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private EventService eventService;

    public void init() {
        this.ticketTypes = fileStorageService.loadTicketTypes();
        this.ticketTypeIdCounter = fileStorageService.loadTicketTypeCounter();
    }

    public List<TicketType> getAllTicketTypes() {
        return new ArrayList<>(ticketTypes.values());
    }

    public List<TicketType> getTicketTypesByEventId(Long eventId) {
        return ticketTypes.values().stream()
                .filter(ticketType -> ticketType.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }

    public Optional<TicketType> getTicketTypeById(Long id) {
        return Optional.ofNullable(ticketTypes.get(id));
    }

    public TicketType createTicketType(TicketType ticketType, Long eventId) {
        try {
            ticketTypeLock.lock();
            ticketType.setId(ticketTypeIdCounter.getAndIncrement());
            ticketType.setEventId(eventId);
            
            ticketTypes.put(ticketType.getId(), ticketType);
            fileStorageService.saveTicketTypes(ticketTypes);
            fileStorageService.saveTicketTypeCounter(ticketTypeIdCounter);
            
            // Update the event to include this ticket type
            Optional<Event> eventOpt = eventService.getEventById(eventId);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                event.addTicketType(ticketType);
                eventService.updateEvent(eventId, event);
            }
            
            return ticketType;
        } finally {
            ticketTypeLock.unlock();
        }
    }

    public TicketType updateTicketType(Long id, TicketType ticketTypeDetails) {
        try {
            ticketTypeLock.lock();
            TicketType existingTicketType = ticketTypes.get(id);
            if (existingTicketType != null) {
                existingTicketType.setName(ticketTypeDetails.getName());
                existingTicketType.setDescription(ticketTypeDetails.getDescription());
                existingTicketType.setPrice(ticketTypeDetails.getPrice());
                existingTicketType.setAvailableQuantity(ticketTypeDetails.getAvailableQuantity());
                
                ticketTypes.put(id, existingTicketType);
                fileStorageService.saveTicketTypes(ticketTypes);
                return existingTicketType;
            }
            return null;
        } finally {
            ticketTypeLock.unlock();
        }
    }

    public boolean deleteTicketType(Long id) {
        try {
            ticketTypeLock.lock();
            TicketType ticketType = ticketTypes.get(id);
            if (ticketType != null) {
                // Get the event and remove this ticket type
                Optional<Event> eventOpt = eventService.getEventById(ticketType.getEventId());
                if (eventOpt.isPresent()) {
                    Event event = eventOpt.get();
                    event.getTicketTypes().removeIf(tt -> tt.getId().equals(id));
                    if (event.getTicketTypes().isEmpty()) {
                        event.setHasMultipleTicketTypes(false);
                    }
                    eventService.updateEvent(event.getId(), event);
                }
                
                boolean removed = ticketTypes.remove(id) != null;
                if (removed) {
                    fileStorageService.saveTicketTypes(ticketTypes);
                }
                return removed;
            }
            return false;
        } finally {
            ticketTypeLock.unlock();
        }
    }
}
