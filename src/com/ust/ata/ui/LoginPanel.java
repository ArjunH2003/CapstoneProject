package com.ust.ata.ui;


import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.util.User;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private User userService = new UserImpl();

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("ATA Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel lblUser = new JLabel("User ID:");
        JTextField txtUser = new JTextField(15);
        JLabel lblPass = new JLabel("Password:");
        JPasswordField txtPass = new JPasswordField(15);
        JButton btnLogin = new JButton("Login");
        JButton btnReg = new JButton("New User? Register");

        // Layout logic
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; add(title, gbc);
        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=1; add(lblUser, gbc);
        gbc.gridx=1; gbc.gridy=1; add(txtUser, gbc);
        gbc.gridx=0; gbc.gridy=2; add(lblPass, gbc);
        gbc.gridx=1; gbc.gridy=2; add(txtPass, gbc);
        gbc.gridx=1; gbc.gridy=3; add(btnLogin, gbc);
        gbc.gridx=1; gbc.gridy=4; add(btnReg, gbc);

        btnLogin.addActionListener(e -> {
            CredentialsBean cb = new CredentialsBean();
            cb.setUserID(txtUser.getText());
            cb.setPassword(new String(txtPass.getPassword()));

            String role = userService.login(cb);

            if (role.equals("A")) {
                mainFrame.showAdmin(cb.getUserID());
            } else if (role.equals("C")) {
                mainFrame.showCustomer(cb.getUserID());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReg.addActionListener(e -> mainFrame.showRegister());
    }
}