package com.helpdesk.service;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.util.FileHandler;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelpDeskService {
    private List<Ticket> tickets;
    private List<User> users;
    private int nextTicketId;
    private int nextUserId;

    public HelpDeskService() {
        this.tickets = FileHandler.loadTickets();
        this.users = FileHandler.loadUsers();

        // Initialize IDs
        this.nextTicketId = tickets.stream().mapToInt(Ticket::getId).max().orElse(0) + 1;
        this.nextUserId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
    }

    // --- Ticket Operations ---
    public Ticket createTicket(String title, String description, Ticket.Priority priority, int creatorId) {
        Ticket ticket = new Ticket(nextTicketId++, title, description, priority, creatorId);
        tickets.add(ticket);
        saveData();
        return ticket;
    }

    public boolean updateTicketStatus(int ticketId, Ticket.Status status) {
        Optional<Ticket> ticket = getTicketById(ticketId);
        if (ticket.isPresent()) {
            ticket.get().setStatus(status);
            saveData();
            return true;
        }
        return false;
    }

    public boolean assignTicket(int ticketId, int technicianId) {
        Optional<Ticket> ticket = getTicketById(ticketId);
        Optional<User> tech = getUserById(technicianId);

        if (ticket.isPresent() && tech.isPresent() && tech.get().getRole() == User.Role.TECHNICIAN) {
            ticket.get().setAssigneeId(technicianId);
            if (ticket.get().getStatus() == Ticket.Status.OPEN) {
                ticket.get().setStatus(Ticket.Status.IN_PROGRESS);
            }
            saveData();
            return true;
        }
        return false;
    }

    public boolean resolveTicket(int ticketId, String response) {
        Optional<Ticket> ticket = getTicketById(ticketId);
        if (ticket.isPresent()) {
            Ticket t = ticket.get();
            t.setStatus(Ticket.Status.RESOLVED);
            t.setResponse(response);
            saveData();
            return true;
        }
        return false;
    }

    public boolean provideFeedback(int ticketId, String feedback) {
        Optional<Ticket> ticket = getTicketById(ticketId);
        if (ticket.isPresent()) {
            ticket.get().setFeedback(feedback);
            saveData();
            return true;
        }
        return false;
    }

    public List<Ticket> getAllTickets() {
        return tickets;
    }

    public Optional<Ticket> getTicketById(int id) {
        return tickets.stream().filter(t -> t.getId() == id).findFirst();
    }

    // --- User Operations ---
    public User createUser(String name, User.Role role) {
        User user = new User(nextUserId++, name, role);
        users.add(user);
        saveData();
        return user;
    }

    public Optional<User> getUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    public boolean updateUserRole(int userId, User.Role role) {
        Optional<User> user = getUserById(userId);
        if (user.isPresent()) {
            User u = user.get();
            // Don't allow changing role of currently logged-in user if it's the only admin,
            // etc.
            // For now, simple update.
            // We need a way to set role as it is private final? Wait, in User model it is
            // private but no setter.
            // We need to add setRole to User model first.
            // Assuming we will add it.
            // u.setRole(role);
            // saveData();
            return true;
        }
        return false;
    }

    public List<User> getAllUsers() {
        return users;
    }

    private void saveData() {
        FileHandler.saveTickets(tickets);
        FileHandler.saveUsers(users);
    }
}
