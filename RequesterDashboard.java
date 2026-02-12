package com.helpdesk.gui;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.HelpDeskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RequesterDashboard extends JPanel {
    private MainFrame mainFrame;
    private User currentUser;
    private HelpDeskService service;
    private JTable ticketTable;
    private DefaultTableModel tableModel;

    public RequesterDashboard(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        this.service = mainFrame.getService();

        setLayout(new BorderLayout());
        setBackground(ModernTheme.SECONDARY_COLOR); // Light background

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Ticket Dashboard");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("User: " + currentUser.getName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(ModernTheme.FONT_BOLD);
        userPanel.add(userLabel);

        JButton logoutButton = new JButton("Logout");
        ModernTheme.styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(e -> mainFrame.logout());
        userPanel.add(logoutButton);

        headerPanel.add(userPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center - Ticket Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        tablePanel.setBackground(ModernTheme.SECONDARY_COLOR);

        String[] columnNames = { "ID", "Title", "Status", "Priority", "Created", "Assigned To" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(tableModel);
        ModernTheme.styleTable(ticketTable);

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Bottom - Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(ModernTheme.SECONDARY_COLOR);
        actionPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton refreshButton = new JButton("Refresh List");
        ModernTheme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> refreshTable());
        actionPanel.add(refreshButton);

        JButton createButton = new JButton("Create New Ticket");
        ModernTheme.styleButton(createButton); // Primary action
        createButton.addActionListener(e -> openCreateTicketDialog());
        actionPanel.add(createButton);

        add(actionPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Ticket> tickets = service.getAllTickets();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Ticket t : tickets) {
            if (t.getCreatorId() == currentUser.getId()) {
                tableModel.addRow(new Object[] {
                        t.getId(),
                        t.getTitle(),
                        t.getStatus(),
                        t.getPriority(),
                        sdf.format(new java.util.Date(t.getCreatedAt())),
                        (t.getAssigneeId() == 0 ? "Unassigned" : "Tech #" + t.getAssigneeId())
                });
            }
        }
    }

    private void openCreateTicketDialog() {
        JDialog dialog = new JDialog(mainFrame, "Create New Ticket", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Subject:");
        ModernTheme.styleLabel(titleLabel);
        formPanel.add(titleLabel, gbc);

        gbc.gridy++;
        JTextField titleField = new JTextField();
        ModernTheme.styleTextField(titleField);
        titleField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(titleField, gbc);

        gbc.gridy++;
        JLabel descLabel = new JLabel("Description:");
        ModernTheme.styleLabel(descLabel);
        formPanel.add(descLabel, gbc);

        gbc.gridy++;
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setFont(ModernTheme.FONT_NORMAL);
        descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        descArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descArea), gbc);

        gbc.gridy++;
        JLabel priorityLabel = new JLabel("Priority:");
        ModernTheme.styleLabel(priorityLabel);
        formPanel.add(priorityLabel, gbc);

        gbc.gridy++;
        JComboBox<Ticket.Priority> priorityCombo = new JComboBox<>(Ticket.Priority.values());
        priorityCombo.setFont(ModernTheme.FONT_NORMAL);
        priorityCombo.setBackground(Color.WHITE);
        formPanel.add(priorityCombo, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton cancelButton = new JButton("Cancel");
        ModernTheme.styleSecondaryButton(cancelButton);
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton submitButton = new JButton("Submit Ticket");
        ModernTheme.styleButton(submitButton);
        submitButton.addActionListener(e -> {
            String title = titleField.getText();
            String desc = descArea.getText();
            Ticket.Priority priority = (Ticket.Priority) priorityCombo.getSelectedItem();

            if (!title.isEmpty() && !desc.isEmpty()) {
                service.createTicket(title, desc, priority, currentUser.getId());
                refreshTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
            }
        });
        buttonPanel.add(submitButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
