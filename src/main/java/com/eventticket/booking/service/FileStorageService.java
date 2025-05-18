package com.eventticket.booking.service;

import com.eventticket.booking.model.Admin;
import com.eventticket.booking.model.Event;
import com.eventticket.booking.model.TicketType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FileStorageService {
    private final String DATA_DIR = System.getProperty("user.dir") + File.separator + "data";
    private final String EVENTS_FILE = "events.txt";
    private final String TICKET_TYPES_FILE = "ticket_types.txt";
    private final String COUNTER_FILE = "counter.txt";
    private final String TICKET_TYPE_COUNTER_FILE = "ticket_type_counter.txt";
    private final String ADMINS_FILE = "admins.txt";

    private final ObjectMapper objectMapper;

    public FileStorageService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Configure Jackson to ignore unknown properties
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Create data directory if it doesn't exist
        createDataDirectory();
    }

    private void createDataDirectory() {
        File directory = new File(DATA_DIR);
        System.out.println("Data directory path: " + directory.getAbsolutePath());
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            System.out.println("Data directory created: " + created);
        } else {
            System.out.println("Data directory already exists");
        }
    }

    public Map<Long, Event> loadEvents() {
        try {
            File file = new File(DATA_DIR + File.separator + EVENTS_FILE);
            if (!file.exists()) {
                return new ConcurrentHashMap<>();
            }

            Event[] events = objectMapper.readValue(file, Event[].class);
            Map<Long, Event> eventMap = new ConcurrentHashMap<>();
            for (Event event : events) {
                eventMap.put(event.getId(), event);
            }
            return eventMap;
        } catch (IOException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    public void saveEvents(Map<Long, Event> events) {
        try {
            File file = new File(DATA_DIR + File.separator + EVENTS_FILE);
            System.out.println("Saving events to: " + file.getAbsolutePath());
            System.out.println("Events to save: " + events.size());
            objectMapper.writeValue(file, events.values());
            System.out.println("Events saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public AtomicLong loadCounter() {
        try {
            File file = new File(DATA_DIR + File.separator + COUNTER_FILE);
            if (!file.exists()) {
                return new AtomicLong(1);
            }

            Long value = objectMapper.readValue(file, Long.class);
            return new AtomicLong(value);
        } catch (IOException e) {
            e.printStackTrace();
            return new AtomicLong(1);
        }
    }

    public void saveCounter(AtomicLong counter) {
        try {
            File file = new File(DATA_DIR + File.separator + COUNTER_FILE);
            objectMapper.writeValue(file, counter.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Long, TicketType> loadTicketTypes() {
        try {
            File file = new File(DATA_DIR + File.separator + TICKET_TYPES_FILE);
            if (!file.exists()) {
                return new ConcurrentHashMap<>();
            }

            TicketType[] ticketTypes = objectMapper.readValue(file, TicketType[].class);
            Map<Long, TicketType> ticketTypeMap = new ConcurrentHashMap<>();
            for (TicketType ticketType : ticketTypes) {
                ticketTypeMap.put(ticketType.getId(), ticketType);
            }
            return ticketTypeMap;
        } catch (IOException e) {
            e.printStackTrace();
            return new ConcurrentHashMap<>();
        }
    }

    public void saveTicketTypes(Map<Long, TicketType> ticketTypes) {
        try {
            File file = new File(DATA_DIR + File.separator + TICKET_TYPES_FILE);
            System.out.println("Saving ticket types to: " + file.getAbsolutePath());
            System.out.println("Ticket types to save: " + ticketTypes.size());
            objectMapper.writeValue(file, ticketTypes.values());
            System.out.println("Ticket types saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving ticket types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public AtomicLong loadTicketTypeCounter() {
        try {
            File file = new File(DATA_DIR + File.separator + TICKET_TYPE_COUNTER_FILE);
            if (!file.exists()) {
                return new AtomicLong(1);
            }

            Long value = objectMapper.readValue(file, Long.class);
            return new AtomicLong(value);
        } catch (IOException e) {
            e.printStackTrace();
            return new AtomicLong(1);
        }
    }

    public void saveTicketTypeCounter(AtomicLong counter) {
        try {
            File file = new File(DATA_DIR + File.separator + TICKET_TYPE_COUNTER_FILE);
            objectMapper.writeValue(file, counter.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Admin> readAdmins() {

        try {
            File file = new File(DATA_DIR + File.separator + ADMINS_FILE);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            Admin[] admins = objectMapper.readValue(file, Admin[].class);
            List<Admin> adminList = new ArrayList<>();
            for (Admin admin : admins) {
                adminList.add(admin);
            }
            return adminList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public void writeAdmins(List<Admin> admins) {

        try {
            File file = new File(DATA_DIR + File.separator + ADMINS_FILE);
            objectMapper.writeValue(file, admins);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
