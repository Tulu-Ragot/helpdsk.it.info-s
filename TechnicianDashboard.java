package com.helpdesk.gui;

import com.helpdesk.model.Ticket;
import com.helpdesk.model.User;
import com.helpdesk.service.HelpDeskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TechnicianDashboard extends JPanel {
    private MainFrame mainFrame;
    private User currentUser;
    private HelpDeskService service;
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JCheckBox myTicketsOnly;

    public TechnicianDashboard(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;
        this.service = mainFrame.getService();

        setLayout(new BorderLayout());
        setBackground(ModernTheme.SECONDARY_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernTheme.PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Technician Dashboard");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Tech: " + currentUser.getName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(ModernTheme.FONT_BOLD);
        userPanel.add(userLabel);
        JButton logoutButton = new JButton("Logout");
        ModernTheme.styleSecondaryButton(logoutButton);
        logoutButton.addActionListener(e -> mainFrame.logout());
        userPanel.add(logoutButton);
        headerPanel.add(userPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center - Ticket List
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
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(ModernTheme.SECONDARY_COLOR);
        actionPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        myTicketsOnly = new JCheckBox("Show My Tickets Only");
        myTicketsOnly.setFont(ModernTheme.FONT_NORMAL);
        myTicketsOnly.setOpaque(false);
        myTicketsOnly.addActionListener(e -> refreshTable());
        actionPanel.add(myTicketsOnly);

        JButton assignButton = new JButton("Assign to Me");
        ModernTheme.styleButton(assignButton);
        assignButton.setBackground(ModernTheme.ACCENT_COLOR); // Highlight action
        assignButton.addActionListener(e -> assignTicket());
        actionPanel.add(assignButton);

        JButton updateButton = new JButton("Update Status");
        ModernTheme.styleButton(updateButton);
        updateButton.addActionListener(e -> updateStatus());
        actionPanel.add(updateButton);

        JButton refreshButton = new JButton("Refresh");
        ModernTheme.styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> refreshTable());
        actionPanel.add(refreshButton);

        add(actionPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Ticket> tickets = service.getAllTickets();

        if (myTicketsOnly.isSelected()) {
            tickets = tickets.stream()
                    .filter(t -> t.getAssigneeId() == currentUser.getId())
                    .collect(Collectors.toList());
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Ticket t : tickets) {
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

    private void assignTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow != -1) {
            int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
            if (service.assignTicket(ticketId, currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "Ticket assigned to you.");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign ticket.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a ticket.");
        }
    }

    private void updateStatus() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow != -1) {
            int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
            Ticket.Status[] statuses = Ticket.Status.values();
            Ticket.Status selectedStatus = (Ticket.Status) JOptionPane.showInputDialog(
                    this,
                    "Select new status:",
                    "Update Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statuses,
                    statuses[0]);

            if (selectedStatus != null) {
                if (service.updateTicketStatus(ticketId, selectedStatus)) {
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update status.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a ticket.");
        }
    }
}
