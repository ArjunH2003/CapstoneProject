package com.ust.ata.ui;

import com.ust.ata.bean.*;
import com.ust.ata.service.Customer;
import com.ust.ata.service.CustomerImpl;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class CustomerPanel extends JPanel {
    private MainFrame mainFrame;
    private Customer customerService = new CustomerImpl();

    public CustomerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search Vehicles", createSearchPanel());
        tabbedPane.addTab("Book Vehicle", createBookingPanel());
        
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new UserImpl().logout(MainFrame.sessionUserID);
            mainFrame.showLogin();
        });

        add(tabbedPane, BorderLayout.CENTER);
        add(btnLogout, BorderLayout.NORTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Seats", "Fare"}, 0);
        JTable table = new JTable(model);

        JPanel ctrl = new JPanel();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"AC", "NON AC"});
        JButton btnSearch = new JButton("Search by Type");
        ctrl.add(cbType); ctrl.add(btnSearch);

        btnSearch.addActionListener(e -> {
            model.setRowCount(0);
            ArrayList<VehicleBean> list = customerService.viewVehiclesByType((String)cbType.getSelectedItem());
            for(VehicleBean v : list)
                model.addRow(new Object[]{v.getVehicleID(), v.getName(), v.getType(), v.getSeatingCapacity(), v.getFarePerKM()});
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(ctrl, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField txtVehID = new JTextField();
        JTextField txtRouteID = new JTextField();
        JTextField txtDate = new JTextField("yyyy-mm-dd");
        JTextField txtBoard = new JTextField();
        JTextField txtDrop = new JTextField();
        JButton btnBook = new JButton("Confirm Booking");

        panel.add(new JLabel("Vehicle ID:")); panel.add(txtVehID);
        panel.add(new JLabel("Route ID:")); panel.add(txtRouteID);
        panel.add(new JLabel("Journey Date (yyyy-mm-dd):")); panel.add(txtDate);
        panel.add(new JLabel("Boarding Point:")); panel.add(txtBoard);
        panel.add(new JLabel("Drop Point:")); panel.add(txtDrop);
        panel.add(new JLabel("")); panel.add(btnBook);

        btnBook.addActionListener(e -> {
            try {
                ReservationBean rb = new ReservationBean();
                rb.setUserID(MainFrame.sessionUserID);
                rb.setVehicleID(txtVehID.getText());
                rb.setRouteID(txtRouteID.getText());
                
                // [FIX] Use java.sql.Date to ensure compatibility with both Util and SQL date types
                rb.setBookingDate(new java.sql.Date(System.currentTimeMillis())); 
                
                // Validate and Parse Journey Date
                rb.setJourneyDate(java.sql.Date.valueOf(txtDate.getText()));
                
                rb.setBoardingPoint(txtBoard.getText());
                rb.setDropPoint(txtDrop.getText());
                rb.setTotalFare(500.00); // Logic should calculate this based on distance

                String id = customerService.bookVehicle(rb);
                if(id != null && !id.equals("FAIL")) {
                    JOptionPane.showMessageDialog(this, "Booking Successful! ID: " + id);
                } else {
                    JOptionPane.showMessageDialog(this, "Booking Failed.");
                }
            } catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Use yyyy-mm-dd");
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage());
            }
        });

        return panel;
    }
}