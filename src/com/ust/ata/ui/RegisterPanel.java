package com.ust.ata.ui;


import com.ust.ata.bean.ProfileBean;
import com.ust.ata.util.User;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import java.awt.*;
// The following two imports are needed for robust date parsing:
import java.text.SimpleDateFormat; 
import java.util.Date; 

public class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private User userService = new UserImpl();

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField txtFname = new JTextField();
        JTextField txtLname = new JTextField();
        JTextField txtDob = new JTextField("yyyy-mm-dd"); // Simplified Date handling
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male", "Female"});
        JTextField txtStreet = new JTextField();
        JTextField txtLoc = new JTextField();
        JTextField txtCity = new JTextField();
        JTextField txtState = new JTextField();
        JTextField txtPin = new JTextField();
        JTextField txtMob = new JTextField();
        JTextField txtEmail = new JTextField();

        formPanel.add(new JLabel("First Name:")); formPanel.add(txtFname);
        formPanel.add(new JLabel("Last Name:")); formPanel.add(txtLname);
        formPanel.add(new JLabel("Date of Birth:")); formPanel.add(txtDob);
        formPanel.add(new JLabel("Gender:")); formPanel.add(cbGender);
        formPanel.add(new JLabel("Street:")); formPanel.add(txtStreet);
        formPanel.add(new JLabel("Location:")); formPanel.add(txtLoc);
        formPanel.add(new JLabel("City:")); formPanel.add(txtCity);
        formPanel.add(new JLabel("State:")); formPanel.add(txtState);
        formPanel.add(new JLabel("Pincode:")); formPanel.add(txtPin);
        formPanel.add(new JLabel("Mobile:")); formPanel.add(txtMob);
        formPanel.add(new JLabel("Email:")); formPanel.add(txtEmail);

        JPanel btnPanel = new JPanel();
        JButton btnSubmit = new JButton("Register");
        JButton btnBack = new JButton("Back to Login");
        btnPanel.add(btnSubmit);
        btnPanel.add(btnBack);

        add(new JLabel("User Registration", SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnSubmit.addActionListener(e -> {
            
            // --- DATE PARSING FIX START ---
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); 
            java.sql.Date sqlDateOfBirth = null;

            try {
                // Parse the text field input string to a java.util.Date
                java.util.Date utilDate = sdf.parse(txtDob.getText().trim());
                
                // Convert the java.util.Date to the required java.sql.Date
                sqlDateOfBirth = new java.sql.Date(utilDate.getTime());
                
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid Date Format. Please use strictly YYYY-MM-DD (e.g., 1990-12-31).", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop execution if date parsing fails
            } 
            // --- DATE PARSING FIX END ---
            
            try {
                ProfileBean pb = new ProfileBean();
                pb.setFirstName(txtFname.getText());
                pb.setLastName(txtLname.getText());
                
                // Set the successfully parsed SQL date
                pb.setDateOfBirth(sqlDateOfBirth); 
                
                pb.setGender((String) cbGender.getSelectedItem());
                pb.setStreet(txtStreet.getText());
                pb.setLocation(txtLoc.getText());
                pb.setCity(txtCity.getText());
                pb.setState(txtState.getText());
                pb.setPincode(txtPin.getText());
                pb.setMobileNo(txtMob.getText());
                pb.setEmailID(txtEmail.getText());

                String id = userService.register(pb);

                if (!id.equals("FAIL")) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!\nYour User ID is: " + id + "\nDefault Password: user123");
                    mainFrame.showLogin();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed (System Error).");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration Failed due to invalid data or system error: " + ex.getMessage());
            }
        });

        btnBack.addActionListener(e -> mainFrame.showLogin());
    }
}