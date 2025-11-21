package com.ust.ata.ui;

import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.util.User;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import java.awt.*;


public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private User userService = new UserImpl();
    
    // UI Components (Class Level for Dynamic Updating)
    private JLabel lblTitle;
    private JLabel lblUser; // [NEW] Promoted to class level
    private JTextField txtUser;
    private JPasswordField txtPass;
    private String currentMode = "CUSTOMER"; 

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Theme.BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        lblTitle = new JLabel("Login", SwingConstants.CENTER);
        Theme.styleHeader(lblTitle);

        // Inputs
        lblUser = new JLabel("User ID"); // Initial default
        lblUser.setFont(Theme.FONT_REGULAR);
        
        txtUser = new JTextField(20); 
        Theme.styleTextField(txtUser);

        JLabel lblPass = new JLabel("Password"); 
        lblPass.setFont(Theme.FONT_REGULAR);
        
        txtPass = new JPasswordField(20); 
        Theme.styleTextField(txtPass);

        JButton btnLogin = new JButton("Login"); 
        Theme.styleButton(btnLogin);

        // Links
        JButton btnReg = new JButton("Create Account");
        styleLinkButton(btnReg);
        
        JButton btnBack = new JButton("← Back to Role Selection");
        styleLinkButton(btnBack);
        btnBack.setForeground(Color.GRAY);

        // Layout Adding
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; card.add(lblTitle, gbc);
        
        gbc.gridwidth=1; 
        gbc.gridy=1; card.add(lblUser, gbc); // Label
        gbc.gridy=2; card.add(txtUser, gbc); // Field
        
        gbc.gridy=3; card.add(lblPass, gbc); // Label
        gbc.gridy=4; card.add(txtPass, gbc); // Field
        
        gbc.gridy=5; gbc.gridwidth=2; card.add(btnLogin, gbc);
        gbc.gridy=6; card.add(btnReg, gbc);
        gbc.gridy=7; card.add(btnBack, gbc);

        add(card);

        // --- LOGIC ---
        btnLogin.addActionListener(e -> {
            CredentialsBean cb = new CredentialsBean();
            cb.setUserID(txtUser.getText());
            cb.setPassword(new String(txtPass.getPassword()));
            
            String role = userService.login(cb);

            // Strict Role Check
            if (currentMode.equals("ADMIN")) {
                if (role.equals("A")) {
                    mainFrame.showAdmin(cb.getUserID());
                } else if (role.equals("C")) {
                    JOptionPane.showMessageDialog(this, "This is a Customer account.\nPlease use the Customer Login.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } 
            else { // CUSTOMER MODE
                if (role.equals("C")) {
                    mainFrame.showCustomer(cb.getUserID());
                } else if (role.equals("A")) {
                    JOptionPane.showMessageDialog(this, "This is an Admin account.\nPlease use the Admin Login.", "Access Denied", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnReg.addActionListener(e -> mainFrame.showRegister());
        btnBack.addActionListener(e -> mainFrame.showWelcome());
    }

    /**
     * Updates UI text and clears fields when switching roles.
     */
    public void setLoginMode(String mode) {
        this.currentMode = mode;
        
        // Clear fields
        txtUser.setText("");
        txtPass.setText("");
        
        if (mode.equals("ADMIN")) {
            lblTitle.setText("Admin Login");
            lblTitle.setForeground(Theme.DARK_PRIMARY);
            lblUser.setText("Admin ID"); // [FIX] Changes label to Admin ID
        } else {
            lblTitle.setText("Customer Login");
            lblTitle.setForeground(Theme.PRIMARY);
            lblUser.setText("User ID"); // [FIX] Changes label back to User ID
        }
    }

    private void styleLinkButton(JButton btn) {
        btn.setFont(Theme.FONT_REGULAR);
        btn.setForeground(Theme.PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}






























//package com.ust.ata.ui;
//
//
//import com.ust.ata.bean.CredentialsBean;
//import com.ust.ata.util.User;
//import com.ust.ata.util.UserImpl;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class LoginPanel extends JPanel {
//    private MainFrame mainFrame;
//    private User userService = new UserImpl();
//
//    public LoginPanel(MainFrame mainFrame) {
//        this.mainFrame = mainFrame;
//        setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10);
//
//        JLabel title = new JLabel("ATA Login");
//        title.setFont(new Font("Arial", Font.BOLD, 24));
//
//        JLabel lblUser = new JLabel("User ID:");
//        JTextField txtUser = new JTextField(15);
//        JLabel lblPass = new JLabel("Password:");
//        JPasswordField txtPass = new JPasswordField(15);
//        JButton btnLogin = new JButton("Login");
//        JButton btnReg = new JButton("New User? Register");
//
//        // Layout logic
//        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; add(title, gbc);
//        gbc.gridwidth=1;
//        gbc.gridx=0; gbc.gridy=1; add(lblUser, gbc);
//        gbc.gridx=1; gbc.gridy=1; add(txtUser, gbc);
//        gbc.gridx=0; gbc.gridy=2; add(lblPass, gbc);
//        gbc.gridx=1; gbc.gridy=2; add(txtPass, gbc);
//        gbc.gridx=1; gbc.gridy=3; add(btnLogin, gbc);
//        gbc.gridx=1; gbc.gridy=4; add(btnReg, gbc);
//
//        btnLogin.addActionListener(e -> {
//            CredentialsBean cb = new CredentialsBean();
//            cb.setUserID(txtUser.getText());
//            cb.setPassword(new String(txtPass.getPassword()));
//
//            String role = userService.login(cb);
//
//            if (role.equals("A")) {
//                mainFrame.showAdmin(cb.getUserID());
//            } else if (role.equals("C")) {
//                mainFrame.showCustomer(cb.getUserID());
//            } else {
//                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        btnReg.addActionListener(e -> mainFrame.showRegister());
//    }
//}