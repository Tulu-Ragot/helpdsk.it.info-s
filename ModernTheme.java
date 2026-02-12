package com.helpdesk.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernTheme {
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(51, 102, 204); // Nice Blue
    public static final Color PRIMARY_DARK_COLOR = new Color(41, 82, 163);
    public static final Color SECONDARY_COLOR = new Color(245, 245, 245); // Light Gray BG
    public static final Color TEXT_COLOR = new Color(51, 51, 51); // Dark Gray Text
    public static final Color TEXT_LIGHT_COLOR = new Color(119, 119, 119); // Muted Text
    public static final Color WHITE = Color.WHITE;
    public static final Color HOVER_COLOR = new Color(230, 240, 255);
    public static final Color ACCENT_COLOR = new Color(255, 87, 34); // Orange for highlights

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // Styling Methods
    public static void styleButton(JButton button) {
        button.setFont(FONT_BOLD);
        button.setForeground(WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Custom painting for flat look
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button);
        button.setBackground(TEXT_LIGHT_COLOR);
        button.setForeground(WHITE);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(TEXT_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(TEXT_LIGHT_COLOR);
            }
        });
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_NORMAL);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 8, 5, 8)));
    }

    public static void styleLabel(JLabel label) {
        label.setFont(FONT_NORMAL);
        label.setForeground(TEXT_COLOR);
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(HOVER_COLOR);
        table.setSelectionForeground(TEXT_COLOR);

        // Header
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(PRIMARY_COLOR); // Dark header
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setBorder(null);
        ((javax.swing.table.DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);
    }
}
