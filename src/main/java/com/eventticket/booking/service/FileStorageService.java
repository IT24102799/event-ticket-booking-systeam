package com.eventticket.booking.service;

import com.eventticket.booking.model.Admin;
import com.eventticket.booking.model.Event;
import com.eventticket.booking.model.TicketType;
import com.eventticket.booking.model.User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FileStorageService {
    private final String DATA_DIR = System.getProperty("user.dir") + File.separator + "data";
    private final String EVENTS_FILE = "events.txt";
    private final String USERS_FILE = "users.txt";
    private final String COUNTER_FILE = "counter.txt";
    private final String USER_COUNTER_FILE = "user_counter.txt";
    private final String TICKET_TYPES_FILE = "ticket_types.txt";
    private final String TICKET_TYPE_COUNTER_FILE = "ticket_type_counter.txt";
    private final String ADMINS_FILE = "admins.txt";

    public FileStorageService() {
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
        Map<Long, Event> eventMap = new ConcurrentHashMap<>();
        File file = new File(DATA_DIR + File.separator + EVENTS_FILE);

        if (!file.exists()) {
            return eventMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String jsonContent = content.toString().trim();

            // Check if the content is in JSON format
            if (jsonContent.startsWith("[") && jsonContent.endsWith("]")) {
                // Parse JSON array
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

                    Event[] events = mapper.readValue(jsonContent, Event[].class);
                    for (Event event : events) {
                        eventMap.put(event.getId(), event);
                    }
                    System.out.println("Loaded " + eventMap.size() + " events from JSON file");
                } catch (Exception e) {
                    System.err.println("Error parsing JSON events: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Try the old pipe-delimited format as fallback
                String[] lines = jsonContent.split("\n");
                for (String dataLine : lines) {
                    if (dataLine.trim().isEmpty()) continue;

                    String[] parts = dataLine.split("\\|");
                    if (parts.length >= 7) {
                        Event event = new Event();
                        event.setId(Long.parseLong(parts[0]));
                        event.setName(parts[1]);
                        event.setDescription(parts[2]);
                        event.setStartDateTime(LocalDateTime.parse(parts[3]));
                        event.setEndDateTime(LocalDateTime.parse(parts[4]));
                        event.setVenue(parts[5]);
                        event.setPrice(new BigDecimal(parts[6]));

                        if (parts.length > 7) {
                            event.setImageUrl(parts[7]);
                        }

                        eventMap.put(event.getId(), event);
                    }
                }
                System.out.println("Loaded " + eventMap.size() + " events from pipe-delimited file");
            }
        } catch (IOException e) {
            System.err.println("Error loading events: " + e.getMessage());
            e.printStackTrace();
        }

        return eventMap;
    }

    public void saveEvents(Map<Long, Event> events) {
        try {
            File file = new File(DATA_DIR + File.separator + EVENTS_FILE);
            System.out.println("Saving events to: " + file.getAbsolutePath());
            System.out.println("Events to save: " + events.size());

            // Save as JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, events.values());
            System.out.println("Events saved successfully in JSON format");
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<Long, User> loadUsers() {
        Map<Long, User> userMap = new ConcurrentHashMap<>();
        File file = new File(DATA_DIR + File.separator + USERS_FILE);
        System.out.println("Attempting to load users from: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());

        if (!file.exists()) {
            System.out.println("Users file not found, returning empty map");
            return userMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    User user = new User();
                    user.setId(Long.parseLong(parts[0]));
                    user.setName(parts[1]);
                    user.setEmail(parts[2]);
                    user.setPhone(parts[3]);
                    user.setPassword(parts[4]);

                    if (parts.length > 5) {
                        user.setRole(parts[5]);
                    } else {
                        user.setRole("USER");
                    }

                    userMap.put(user.getId(), user);
                }
            }
            System.out.println("Loaded " + userMap.size() + " users from file");
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }

        return userMap;
    }

    public void saveUsers(Map<Long, User> users) {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                System.out.println("Creating data directory: " + dataDir.getAbsolutePath());
                dataDir.mkdirs();
            }

            File file = new File(DATA_DIR + File.separator + USERS_FILE);
            System.out.println("Saving users to file: " + file.getAbsolutePath());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (User user : users.values()) {
                    // Format: id|name|email|phone|password|role
                    String line = String.format("%d|%s|%s|%s|%s|%s",
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getPassword(),
                            user.getRole());

                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Successfully saved " + users.size() + " users to file");
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public AtomicLong loadCounter() {
        try {
            File file = new File(DATA_DIR + File.separator + COUNTER_FILE);
            if (!file.exists()) {
                return new AtomicLong(1);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return new AtomicLong(Long.parseLong(line.trim()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading counter: " + e.getMessage());
            e.printStackTrace();
        }

        return new AtomicLong(1);
    }

    public void saveCounter(AtomicLong counter) {
        try {
            File file = new File(DATA_DIR + File.separator + COUNTER_FILE);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.valueOf(counter.get()));
            }
        } catch (IOException e) {
            System.err.println("Error saving counter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public AtomicLong loadUserCounter() {
        try {
            File file = new File(DATA_DIR + File.separator + USER_COUNTER_FILE);
            if (!file.exists()) {
                return new AtomicLong(1);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return new AtomicLong(Long.parseLong(line.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading user counter: " + e.getMessage());
            e.printStackTrace();
        }

        return new AtomicLong(1);
    }

    public void saveUserCounter(AtomicLong counter) {
        try {
            File file = new File(DATA_DIR + File.separator + USER_COUNTER_FILE);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.valueOf(counter.get()));
            }
        } catch (IOException e) {
            System.err.println("Error saving user counter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<Long, TicketType> loadTicketTypes() {
        Map<Long, TicketType> ticketTypeMap = new ConcurrentHashMap<>();
        File file = new File(DATA_DIR + File.separator + TICKET_TYPES_FILE);
        System.out.println("Attempting to load ticket types from: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());

        if (!file.exists()) {
            System.out.println("Ticket types file not found, returning empty map");
            return ticketTypeMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    TicketType ticketType = new TicketType();
                    ticketType.setId(Long.parseLong(parts[0]));
                    ticketType.setName(parts[1]);
                    ticketType.setDescription(parts[2]);
                    ticketType.setPrice(new BigDecimal(parts[3]));
                    ticketType.setAvailableQuantity(Integer.parseInt(parts[4]));

                    if (parts.length > 5) {
                        ticketType.setEventId(Long.parseLong(parts[5]));
                    }

                    ticketTypeMap.put(ticketType.getId(), ticketType);
                }
            }
            System.out.println("Loaded " + ticketTypeMap.size() + " ticket types from file");
        } catch (IOException e) {
            System.err.println("Error loading ticket types: " + e.getMessage());
            e.printStackTrace();
        }

        return ticketTypeMap;
    }

    public void saveTicketTypes(Map<Long, TicketType> ticketTypes) {
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                System.out.println("Creating data directory: " + dataDir.getAbsolutePath());
                dataDir.mkdirs();
            }

            File file = new File(DATA_DIR + File.separator + TICKET_TYPES_FILE);
            System.out.println("Saving ticket types to file: " + file.getAbsolutePath());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (TicketType ticketType : ticketTypes.values()) {
                    // Format: id|name|description|price|availableQuantity|eventId
                    String line = String.format("%d|%s|%s|%s|%d|%d",
                            ticketType.getId(),
                            ticketType.getName(),
                            ticketType.getDescription(),
                            ticketType.getPrice().toString(),
                            ticketType.getAvailableQuantity(),
                            ticketType.getEventId());

                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("Successfully saved " + ticketTypes.size() + " ticket types to file");
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

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    return new AtomicLong(Long.parseLong(line.trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading ticket type counter: " + e.getMessage());
            e.printStackTrace();
        }

        return new AtomicLong(1);
    }

    public void saveTicketTypeCounter(AtomicLong counter) {
        try {
            File file = new File(DATA_DIR + File.separator + TICKET_TYPE_COUNTER_FILE);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.valueOf(counter.get()));
            }
        } catch (IOException e) {
            System.err.println("Error saving ticket type counter: " + e.getMessage());
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
