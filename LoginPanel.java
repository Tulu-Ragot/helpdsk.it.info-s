package com.helpdesk.gui;

import com.helpdesk.model.User;
import com.helpdesk.service.HelpDeskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private HelpDeskService service;
    private JTextField idField;
    private JTextField nameField;
    private JComboBox<String> roleCombo;
    private JComboBox<String> modeCombo; // Login or Register

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.service = mainFrame.getService();
        setLayout(new BorderLayout()); // Use consistent layout
        setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for flexibility
        centerPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Help Desk System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Slightly larger title
        titleLabel.setForeground(ModernTheme.PRIMARY_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        // Mode Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel modeLabel = new JLabel("Mode:");
        ModernTheme.styleLabel(modeLabel);
        centerPanel.add(modeLabel, gbc);

        gbc.gridx = 1;
        modeCombo = new JComboBox<>(new String[] { "Login", "Register" });
        modeCombo.addActionListener(e -> updateFields());
        modeCombo.setFont(ModernTheme.FONT_NORMAL);
        centerPanel.add(modeCombo, gbc);

        // Login Fields
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel idLabel = new JLabel("User ID:");
        ModernTheme.styleLabel(idLabel);
        centerPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        idField = new JTextField(15);
        ModernTheme.styleTextField(idField);
        centerPanel.add(idField, gbc);

        // Register Fields (Initially hidden)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Name:");
        ModernTheme.styleLabel(nameLabel);
        centerPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(15);
        ModernTheme.styleTextField(nameField);
        centerPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel roleLabel = new JLabel("Role:");
        ModernTheme.styleLabel(roleLabel);
        centerPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[] { "REQUESTER", "TECHNICIAN" });
        roleCombo.setFont(ModernTheme.FONT_NORMAL);
        centerPanel.add(roleCombo, gbc);

        // Submit Button
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; // Right align button
        JButton submitButton = new JButton("Submit");
        ModernTheme.styleButton(submitButton);
        submitButton.addActionListener(e -> handleSubmit());
        centerPanel.add(submitButton, gbc);

        add(centerPanel, BorderLayout.CENTER);

        updateFields(); // Set initial state
    }

    private void updateFields() {
        boolean isRegister = modeCombo.getSelectedItem().equals("Register");
        idField.setEnabled(!isRegister);
        nameField.setEnabled(isRegister);
        roleCombo.setEnabled(isRegister);

        // Add subtle visual cue regarding active field
        if (isRegister) {
            idField.setBackground(new Color(240, 240, 240));
            nameField.setBackground(Color.WHITE);
        } else {
            idField.setBackground(Color.WHITE);
            nameField.setBackground(new Color(240, 240, 240));
        }
    }

    private void handleSubmit() {
        if (modeCombo.getSelectedItem().equals("Login")) {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                Optional<User> user = service.getUserById(id);
                if (user.isPresent()) {
                    mainFrame.onLoginSuccess(user.get());
                } else {
                    JOptionPane.showMessageDialog(this, "User ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid User ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User.Role role = User.Role.valueOf((String) roleCombo.getSelectedItem());
            User newUser = service.createUser(name, role);

            JOptionPane.showMessageDialog(this, "User created! Your ID is: " + newUser.getId(), "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.onLoginSuccess(newUser);
        }
    }
}
