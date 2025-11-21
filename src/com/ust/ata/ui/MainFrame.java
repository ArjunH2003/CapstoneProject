package com.ust.ata.ui;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    public static String sessionUserID;
    
    private LoginPanel loginPanel;

    public MainFrame() {
        setTitle("GlideGo - Travel Automation System");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Theme.BG_COLOR);

        // Initialize Panels
        loginPanel = new LoginPanel(this);

        // Add Panels
        mainPanel.add(new WelcomePanel(this), "WELCOME");
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(new RegisterPanel(this), "REGISTER");

        add(mainPanel);
        
        // Start directly at Welcome Screen
        cardLayout.show(mainPanel, "WELCOME");
    }

    // --- NAVIGATION METHODS ---

    public void showLogin(String roleType) {
        sessionUserID = null;
        loginPanel.setLoginMode(roleType); 
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showLogin() {
        sessionUserID = null;
        cardLayout.show(mainPanel, "WELCOME");
    }
    
    public void showWelcome() {
        cardLayout.show(mainPanel, "WELCOME");
    }

    public void showRegister() {
        cardLayout.show(mainPanel, "REGISTER");
    }

    public void showAdmin(String userID) {
        sessionUserID = userID;
        mainPanel.add(new AdminPanel(this), "ADMIN"); 
        cardLayout.show(mainPanel, "ADMIN");
    }

    public void showCustomer(String userID) {
        sessionUserID = userID;
        mainPanel.add(new CustomerPanel(this), "CUSTOMER"); 
        cardLayout.show(mainPanel, "CUSTOMER");
    }

    public static void main(String[] args) {
        try {
            // Keep the modern look
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("nimbusBase", new Color(240, 240, 240));
            UIManager.put("nimbusBlueGrey", new Color(220, 220, 220));
            UIManager.put("control", Theme.BG_COLOR);
        } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }

        // [REMOVED] Splash Screen logic deleted here.
        
        // Launch App Immediately
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}