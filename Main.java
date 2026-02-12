package com.helpdesk;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.HelpDeskService;

import java.util.Scanner;
import java.util.List;
import java.util.Optional;

public class Main {
    private static HelpDeskService service;
    private static Scanner scanner;
    private static User currentUser;

    public static void main(String[] args) {
        service = new HelpDeskService();
        scanner = new Scanner(System.in);

        System.out.println("Welcome to the Help Desk System");

        login();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Enter choice: ");
            switch (choice) {
                case 1:
                    createTicket();
                    break;
                case 2:
                    viewMyTickets();
                    break;
                case 3:
                    if (currentUser.getRole() == User.Role.TECHNICIAN) {
                        viewAllTickets();
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                case 4:
                    if (currentUser.getRole() == User.Role.TECHNICIAN) {
                        updateTicketStatus();
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                case 5:
                    if (currentUser.getRole() == User.Role.TECHNICIAN) {
                        assignTicket();
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                case 0:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void login() {
        while (currentUser == null) {
            System.out.println("\n--- Login ---");
            System.out.println("1. Login with existing User ID");
            System.out.println("2. Create new User");
            int choice = getIntInput("Enter choice: ");

            if (choice == 1) {
                int id = getIntInput("Enter User ID: ");
                Optional<User> user = service.getUserById(id);
                if (user.isPresent()) {
                    currentUser = user.get();
                    System.out.println("Logged in as: " + currentUser.getName());
                } else {
                    System.out.println("User not found.");
                }
            } else if (choice == 2) {
                System.out.print("Enter Name: ");
                String name = scanner.nextLine();
                System.out.println("Select Role: 1. Requester, 2. Technician");
                int roleChoice = getIntInput("Choice: ");
                User.Role role = (roleChoice == 2) ? User.Role.TECHNICIAN : User.Role.REQUESTER;
                currentUser = service.createUser(name, role);
                System.out.println("User created! Your ID is: " + currentUser.getId());
                System.out.println("Logged in as: " + currentUser.getName());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Create Ticket");
        System.out.println("2. View My Tickets (Created/Assigned)");
        if (currentUser.getRole() == User.Role.TECHNICIAN) {
            System.out.println("3. View All Tickets");
            System.out.println("4. Update Ticket Status");
            System.out.println("5. Assign Ticket");
        }
        System.out.println("0. Exit");
    }

    private static void createTicket() {
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Description: ");
        String desc = scanner.nextLine();
        System.out.println("Priority: 1. LOW, 2. MEDIUM, 3. HIGH");
        int p = getIntInput("Choice: ");
        Ticket.Priority priority = Ticket.Priority.LOW;
        if (p == 2)
            priority = Ticket.Priority.MEDIUM;
        if (p == 3)
            priority = Ticket.Priority.HIGH;

        Ticket t = service.createTicket(title, desc, priority, currentUser.getId());
        System.out.println("Ticket created: " + t);
    }

    private static void viewMyTickets() {
        List<Ticket> tickets = service.getAllTickets();
        System.out.println("\n--- My Tickets ---");
        for (Ticket t : tickets) {
            if (t.getCreatorId() == currentUser.getId() || t.getAssigneeId() == currentUser.getId()) {
                System.out.println(t);
            }
        }
    }

    private static void viewAllTickets() {
        List<Ticket> tickets = service.getAllTickets();
        System.out.println("\n--- All Tickets ---");
        for (Ticket t : tickets) {
            System.out.println(t);
        }
    }

    private static void updateTicketStatus() {
        int id = getIntInput("Enter Ticket ID: ");
        System.out.println("Status: 1. OPEN, 2. IN_PROGRESS, 3. RESOLVED");
        int s = getIntInput("Choice: ");
        Ticket.Status status = Ticket.Status.OPEN;
        if (s == 2)
            status = Ticket.Status.IN_PROGRESS;
        if (s == 3)
            status = Ticket.Status.RESOLVED;

        if (service.updateTicketStatus(id, status)) {
            System.out.println("Updated successfully.");
        } else {
            System.out.println("Ticket not found.");
        }
    }

    private static void assignTicket() {
        int tId = getIntInput("Enter Ticket ID: ");
        int uId = getIntInput("Enter Technician User ID: ");
        if (service.assignTicket(tId, uId)) {
            System.out.println("Assigned successfully.");
        } else {
            System.out.println("Failed. Check ID or if user is a technician.");
        }
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Enter a number.");
            scanner.next(); // consume bad input
        }
        int i = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return i;
    }
}
