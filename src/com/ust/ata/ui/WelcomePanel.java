package com.ust.ata.ui;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    private MainFrame mainFrame;

    public WelcomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridx = 0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Header ---
        JLabel lblTitle = new JLabel("Welcome to GlideGo", SwingConstants.CENTER);
        Theme.styleHeader(lblTitle);
        
        JLabel lblSub = new JLabel("Select your role to continue", SwingConstants.CENTER);
        lblSub.setFont(Theme.FONT_REGULAR);
        lblSub.setForeground(Color.GRAY);

        // --- Buttons ---
        JButton btnAdmin = new JButton("Administrator");
        Theme.styleButton(btnAdmin);
        btnAdmin.setBackground(Theme.DARK_PRIMARY); // Darker blue for Admin

        JButton btnCustomer = new JButton("Customer");
        Theme.styleButton(btnCustomer);

        // --- Layout ---
        gbc.gridy = 0; card.add(lblTitle, gbc);
        gbc.gridy = 1; card.add(lblSub, gbc);
        
        gbc.gridy = 2; 
        gbc.insets = new Insets(30, 10, 10, 10);
        card.add(btnAdmin, gbc);
        
        gbc.gridy = 3; 
        gbc.insets = new Insets(5, 10, 10, 10);
        card.add(btnCustomer, gbc);

        add(card);

        // --- ACTIONS ---
        // Pass the selected role to the Login Screen
        btnAdmin.addActionListener(e -> mainFrame.showLogin("ADMIN"));
        btnCustomer.addActionListener(e -> mainFrame.showLogin("CUSTOMER"));
    }
}