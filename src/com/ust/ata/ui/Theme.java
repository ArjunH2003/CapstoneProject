package com.ust.ata.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Theme {
    // --- BRAND COLORS ---
    // A professional "Tech Blue" palette
    public static final Color PRIMARY = new Color(33, 150, 243);    // Bright Blue
    public static final Color DARK_PRIMARY = new Color(25, 118, 210); // Darker Blue
    public static final Color ACCENT = new Color(255, 193, 7);      // Amber/Gold for highlights
    public static final Color BG_COLOR = new Color(245, 245, 245);  // Light Grey Background
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT_DARK = new Color(66, 66, 66);    // Dark Grey Text

    // --- FONTS ---
    // Segoe UI is standard on Windows, Arial is safe fallback
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- STYLING METHODS ---

    /**
     * Styles a button to look modern (Flat, colored background).
     */
    public static void styleButton(JButton btn) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false); // Removes the annoying dotted line
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(DARK_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY);
            }
        });
    }

    /**
     * Styles a text field with padding and a clean border.
     */
    public static void styleTextField(JTextField txt) {
        txt.setFont(FONT_REGULAR);
        Border line = BorderFactory.createLineBorder(new Color(200, 200, 200));
        Border empty = BorderFactory.createEmptyBorder(5, 8, 5, 8); // Internal padding
        txt.setBorder(BorderFactory.createCompoundBorder(line, empty));
    }

    /**
     * Styles the Main Header Label.
     */
    public static void styleHeader(JLabel lbl) {
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(DARK_PRIMARY);
    }
}