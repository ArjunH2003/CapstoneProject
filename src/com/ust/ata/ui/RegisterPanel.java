package com.ust.ata.ui;

import com.ust.ata.bean.ProfileBean;
import com.ust.ata.util.User;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private User userService = new UserImpl();

    // Form Fields
    private JTextField txtFname, txtLname, txtDob, txtStreet, txtLoc, txtCity, txtState, txtPin, txtMob, txtEmail;
    private JPasswordField txtPass, txtConfirmPass;
    private JComboBox<String> cbGender;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        // --- MAIN SCROLLABLE CONTAINER ---
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(Theme.BG_COLOR);
        
        // Wrap in ScrollPane in case screens are small
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // --- FORM CARD ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- TITLE ---
        JLabel lblTitle = new JLabel("Create Account");
        Theme.styleHeader(lblTitle);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; 
        gbc.insets = new Insets(0, 10, 20, 10);
        card.add(lblTitle, gbc);

        // --- SECTION 1: PERSONAL INFO ---
        addSectionHeader(card, "Personal Details", 1, gbc);
        
        txtFname = createStyledField();
        txtLname = createStyledField();
        addField(card, "First Name", txtFname, 0, 2, gbc);
        addField(card, "Last Name", txtLname, 1, 2, gbc);

        txtDob = createStyledField();
        txtDob.setText("yyyy-mm-dd"); // Placeholder
        txtDob.setForeground(Color.GRAY);
        // Add simple focus listener to clear placeholder
        txtDob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtDob.getText().equals("yyyy-mm-dd")) {
                    txtDob.setText(""); txtDob.setForeground(Theme.TEXT_DARK);
                }
            }
        });

        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setBackground(Color.WHITE);
        
        addField(card, "Date of Birth", txtDob, 0, 3, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        JLabel lblGender = new JLabel("Gender");
        lblGender.setFont(Theme.FONT_BOLD);
        card.add(lblGender, gbc);
        gbc.gridy = 4;
        card.add(cbGender, gbc);

        // --- SECTION 2: CONTACT ---
        addSectionHeader(card, "Contact Information", 5, gbc);
        
        txtMob = createStyledField();
        txtEmail = createStyledField();
        addField(card, "Mobile Number", txtMob, 0, 6, gbc);
        addField(card, "Email ID", txtEmail, 1, 6, gbc);

        txtStreet = createStyledField();
        txtLoc = createStyledField();
        addField(card, "Street", txtStreet, 0, 7, gbc);
        addField(card, "Location", txtLoc, 1, 7, gbc);

        txtCity = createStyledField();
        txtState = createStyledField();
        addField(card, "City", txtCity, 0, 8, gbc);
        addField(card, "State", txtState, 1, 8, gbc);
        
        txtPin = createStyledField();
        addField(card, "Pincode", txtPin, 0, 9, gbc);

        // --- SECTION 3: SECURITY ---
        addSectionHeader(card, "Security", 10, gbc);
        
        txtPass = new JPasswordField(15);
        Theme.styleTextField(txtPass);
        txtConfirmPass = new JPasswordField(15);
        Theme.styleTextField(txtConfirmPass);
        
        addField(card, "Password", txtPass, 0, 11, gbc);
        addField(card, "Confirm Password", txtConfirmPass, 1, 11, gbc);

        // --- BUTTONS ---
        gbc.gridx = 0; gbc.gridy = 13; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        
        JButton btnRegister = new JButton("Register Now");
        Theme.styleButton(btnRegister);
        btnRegister.setPreferredSize(new Dimension(200, 40));
        card.add(btnRegister, gbc);

        gbc.gridy = 14;
        gbc.insets = new Insets(5, 10, 10, 10);
        JButton btnBack = new JButton("Already have an account? Login");
        btnBack.setFont(Theme.FONT_REGULAR);
        btnBack.setForeground(Theme.PRIMARY);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnBack, gbc);

        // Add Card to Main Container
        mainContainer.add(card);

        // --- ACTIONS ---
        btnRegister.addActionListener(e -> registerAction());
        btnBack.addActionListener(e -> mainFrame.showLogin());
    }

    /**
     * Helper to create a styled text field.
     */
    private JTextField createStyledField() {
        JTextField txt = new JTextField(15);
        Theme.styleTextField(txt);
        return txt;
    }

    /**
     * Helper to add a Section Header.
     */
    private void addSectionHeader(JPanel panel, String text, int row, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.FONT_SUBHEADER);
        lbl.setForeground(Theme.PRIMARY);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(lbl, gbc);
        
        // Reset insets for next component
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridwidth = 1;
    }

    /**
     * Helper to add a Label + Field pair at specific grid coordinates.
     */
    private void addField(JPanel panel, String labelText, JComponent field, int col, int rowBase, GridBagConstraints gbc) {
        gbc.gridx = col; 
        gbc.gridy = rowBase; // The Label row (logical) is actually handled by adding offset if needed
        // But here, we stack label ON TOP of field for modern look? 
        // Let's stick to Label above Field for this layout.
        
        // We actually need 2 rows per logical row: one for labels, one for inputs
        // Let's simplify: Label and Input in same cell? No.
        // Let's assume the row passed is the starting Y index.
        
        // Actually, looking at the layout logic above, we are adding field-label pairs.
        // Let's place Label above Field.
        
        // Row calculation is tricky with GridBag if strictly creating helpers.
        // Simpler approach: Add Label at gridY, Field at gridY+1
        // But since we have 2 columns, this gets complex.
        
        // Direct Add logic:
        JPanel cell = new JPanel(new BorderLayout(0, 5));
        cell.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.FONT_BOLD);
        cell.add(lbl, BorderLayout.NORTH);
        cell.add(field, BorderLayout.CENTER);
        
        gbc.gridx = col;
        gbc.gridy = rowBase;
        panel.add(cell, gbc);
    }

    private void registerAction() {
        try {
            String pass = new String(txtPass.getPassword());
            String confirm = new String(txtConfirmPass.getPassword());

            if (pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty"); return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }
            // Basic empty checks for other fields
            if (txtFname.getText().isEmpty() || txtMob.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all mandatory fields"); return;
            }

            ProfileBean pb = new ProfileBean();
            pb.setFirstName(txtFname.getText());
            pb.setLastName(txtLname.getText());
            // Handle Date Parsing safely
            try {
                pb.setDateOfBirth(java.sql.Date.valueOf(txtDob.getText())); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date. Format: yyyy-mm-dd"); return;
            }
            
            pb.setGender((String) cbGender.getSelectedItem());
            pb.setStreet(txtStreet.getText());
            pb.setLocation(txtLoc.getText());
            pb.setCity(txtCity.getText());
            pb.setState(txtState.getText());
            pb.setPincode(txtPin.getText());
            pb.setMobileNo(txtMob.getText());
            pb.setEmailID(txtEmail.getText());

            String id = userService.register(pb, pass);

            if (!id.equals("FAIL")) {
                JOptionPane.showMessageDialog(this, "<html><h2 style='color:green'>Registration Successful!</h2>Your User ID is: <b>" + id + "</b><br>Please login to continue.</html>");
                mainFrame.showLogin();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Please try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}