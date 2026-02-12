package com.helpdesk.util;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String TICKETS_FILE = "tickets.txt";
    private static final String USERS_FILE = "users.txt";

    public static void saveTickets(List<Ticket> tickets) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TICKETS_FILE))) {
            for (Ticket t : tickets) {
                // Format:
                // id|title|description|status|priority|creatorId|assigneeId|createdAt|response|feedback
                writer.printf("%d|%s|%s|%s|%s|%d|%d|%d|%s|%s%n",
                        t.getId(), t.getTitle(), t.getDescription(),
                        t.getStatus(), t.getPriority(), t.getCreatorId(), t.getAssigneeId(), t.getCreatedAt(),
                        t.getResponse() == null ? "" : t.getResponse(),
                        t.getFeedback() == null ? "" : t.getFeedback());
            }
        } catch (IOException e) {
            System.err.println("Error saving tickets: " + e.getMessage());
        }
    }

    public static List<Ticket> loadTickets() {
        List<Ticket> tickets = new ArrayList<>();
        File file = new File(TICKETS_FILE);
        if (!file.exists())
            return tickets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    long createdAt = (parts.length >= 8) ? Long.parseLong(parts[7]) : System.currentTimeMillis();
                    Ticket t = new Ticket(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            Ticket.Priority.valueOf(parts[4]),
                            Integer.parseInt(parts[5]),
                            createdAt);
                    t.setStatus(Ticket.Status.valueOf(parts[3]));
                    t.setAssigneeId(Integer.parseInt(parts[6]));

                    if (parts.length >= 9)
                        t.setResponse(parts[8]);
                    if (parts.length >= 10)
                        t.setFeedback(parts[9]);

                    tickets.add(t);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
        return tickets;
    }

    public static void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                // Format: id|name|role
                writer.printf("%d|%s|%s%n", u.getId(), u.getName(), u.getRole());
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists())
            return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    users.add(new User(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            User.Role.valueOf(parts[2])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }
}
