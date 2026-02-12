package com.helpdesk.gui;

import com.helpdesk.model.User;
import com.helpdesk.service.HelpDeskService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private HelpDeskService service;
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        service = new HelpDeskService();

        setTitle("Help Desk System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add Login Panel
        LoginPanel loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "LOGIN");

        add(mainPanel);

        // Start with Login
        cardLayout.show(mainPanel, "LOGIN");
    }

    public HelpDeskService getService() {
        return service;
    }

    public void onLoginSuccess(User user) {
        this.currentUser = user;

        // Create appropriate dashboard based on role
        if (user.getRole() == User.Role.TECHNICIAN) {
            TechnicianDashboard techDashboard = new TechnicianDashboard(this, user);
            mainPanel.add(techDashboard, "DASHBOARD");
        } else {
            RequesterDashboard reqDashboard = new RequesterDashboard(this, user);
            mainPanel.add(reqDashboard, "DASHBOARD");
        }

        cardLayout.show(mainPanel, "DASHBOARD");
        setTitle("Help Desk System - " + user.getName() + " (" + user.getRole() + ")");
    }

    public void logout() {
        this.currentUser = null;
        // Remove dashboard to refresh it next time
        // In a more complex app, we might just hide it or reset it
        // For simplicity, we just switch back to login
        cardLayout.show(mainPanel, "LOGIN");
        setTitle("Help Desk System");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Global UI tweaks
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Panel.background", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setSize(1000, 700); // Larger size for better spacing
            frame.getContentPane().setBackground(ModernTheme.SECONDARY_COLOR);
            frame.setVisible(true);
        });
    }
}
