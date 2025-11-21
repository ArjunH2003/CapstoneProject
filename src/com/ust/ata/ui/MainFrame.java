package com.ust.ata.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    public static String sessionUserID; // Stores the logged-in user's ID

    public MainFrame() {
        setTitle("Automation of Travel Agency (ATA)");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the Panels to the CardLayout
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(new RegisterPanel(this), "REGISTER");
        // Admin and Customer panels are added dynamically upon login to ensure data freshness

        add(mainPanel);
        setVisible(true);
    }

    public void showLogin() {
        sessionUserID = null;
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showRegister() {
        cardLayout.show(mainPanel, "REGISTER");
    }

    public void showAdmin(String userID) {
        sessionUserID = userID;
        mainPanel.add(new AdminPanel(this), "ADMIN"); // Re-instantiate to refresh data
        cardLayout.show(mainPanel, "ADMIN");
    }

    public void showCustomer(String userID) {
        sessionUserID = userID;
        mainPanel.add(new CustomerPanel(this), "CUSTOMER"); // Re-instantiate to refresh data
        cardLayout.show(mainPanel, "CUSTOMER");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}