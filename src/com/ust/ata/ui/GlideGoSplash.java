package com.ust.ata.ui;

import javax.swing.*;
import java.awt.*;

public class GlideGoSplash extends JWindow {

    public GlideGoSplash() {
        // Size and Position
        setSize(600, 350); // Wider for a modern look
        setLocationRelativeTo(null); // Center on screen

        // Main Container with Theme Background
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Theme.PRIMARY); // Professional Blue
        content.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        // --- CENTER: LOGO & TITLE ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Transparent

        // [UPDATED] Icon changed to a Taxi/Vehicle symbol
        // Note: If this shows as a square box on your specific OS, change it to text like "ATA"
        JLabel lblIcon = new JLabel("🚖"); 
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 80)); // Use Emoji-compatible font
        lblIcon.setForeground(Theme.ACCENT); // Gold/Amber color

        JLabel lblTitle = new JLabel("GlideGo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Vehicle Booking & Fleet Management System");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(230, 230, 230));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(lblIcon, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        centerPanel.add(lblTitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        centerPanel.add(lblSubtitle, gbc);

        // --- SOUTH: LOADING BAR ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50)); // Padding

        JLabel lblLoading = new JLabel("Initializing Fleet Database...", SwingConstants.CENTER);
        lblLoading.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLoading.setForeground(new Color(200, 200, 200));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(Theme.DARK_PRIMARY);
        progressBar.setForeground(Theme.ACCENT);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(500, 6)); // Slim, modern bar

        bottomPanel.add(lblLoading, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(content);
    }

    public void showSplash() {
        setVisible(true);
        try {
            // Simulate loading assets/database
            Thread.sleep(3000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setVisible(false);
        dispose();
    }
}