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

    // Data Passing
    private JTextField bookVehID = new JTextField();
    private JTextField bookRouteID = new JTextField();
    private JTextField bookFare = new JTextField();
    private JTextField txtFoundRouteID = new JTextField(10);

    public CustomerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search Vehicles", createSearchPanel(tabbedPane));
        tabbedPane.addTab("Book Vehicle", createBookingPanel());
        tabbedPane.addTab("View Status", createViewBookingPanel());
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_COLOR);
        JLabel title = new JLabel(" Customer Dashboard");
        Theme.styleHeader(title);
        JButton btnLogout = new JButton("Logout");
        Theme.styleButton(btnLogout);
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.addActionListener(e -> { new UserImpl().logout(MainFrame.sessionUserID); mainFrame.showLogin(); });
        header.add(title, BorderLayout.WEST); header.add(btnLogout, BorderLayout.EAST);

        add(tabbedPane, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    // --- 1. SEARCH ---
    private JPanel createSearchPanel(JTabbedPane tabs) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel top = new JPanel(new GridLayout(2, 1));
        JPanel inputs = new JPanel(new FlowLayout());
        JTextField src = new JTextField(8), dest = new JTextField(8);
        JComboBox<String> type = new JComboBox<>(new String[]{"AC", "NON AC"});
        JButton btnFind = new JButton("Find");
        inputs.add(new JLabel("Src:")); inputs.add(src); inputs.add(new JLabel("Dst:")); inputs.add(dest);
        inputs.add(new JLabel("Type:")); inputs.add(type); inputs.add(btnFind);

        JPanel ctx = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ctx.setBorder(BorderFactory.createTitledBorder("Route Context"));
        txtFoundRouteID.setEditable(false); txtFoundRouteID.setForeground(Color.BLUE);
        JLabel dist = new JLabel("Distance: -");
        ctx.add(new JLabel("Route ID:")); ctx.add(txtFoundRouteID); ctx.add(Box.createHorizontalStrut(20)); ctx.add(dist);
        top.add(inputs); top.add(ctx);

        String[] cols = {"Vehicle ID", "Name", "Type", "Seats", "Cost (Rs)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        JButton btnBook = new JButton("Book Selected");
        final RouteBean[] route = {null};

        btnFind.addActionListener(e -> {
            ArrayList<RouteBean> routes = customerService.viewAllRoutes();
            route[0] = null;
            for(RouteBean r : routes) if(r.getSource().equalsIgnoreCase(src.getText()) && r.getDestination().equalsIgnoreCase(dest.getText())) { route[0]=r; break; }
            
            if(route[0] == null) {
                txtFoundRouteID.setText("Not Found"); model.setRowCount(0);
                JOptionPane.showMessageDialog(this, "No Route Found"); return;
            }
            txtFoundRouteID.setText(route[0].getRouteID()); dist.setText("Dist: " + route[0].getDistance());
            
            model.setRowCount(0);
            for(VehicleBean v : customerService.viewVehiclesByType((String)type.getSelectedItem())) {
                // FOREIGN KEY LOGIC
                if(v.getRouteID() != null && v.getRouteID().equals(route[0].getRouteID())) {
                    double cost = route[0].getDistance() * v.getFarePerKM();
                    model.addRow(new Object[]{v.getVehicleID(), v.getName(), v.getType(), v.getSeatingCapacity(), String.format("%.2f", cost)});
                }
            }
            if(model.getRowCount() == 0) JOptionPane.showMessageDialog(this, "Route found, but no vehicles assigned.");
        });

        btnBook.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) return;
            bookVehID.setText((String)model.getValueAt(r, 0));
            bookRouteID.setText(txtFoundRouteID.getText());
            bookFare.setText((String)model.getValueAt(r, 4));
            tabs.setSelectedIndex(1);
        });

        panel.add(top, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bot = new JPanel(); bot.add(btnBook); panel.add(bot, BorderLayout.SOUTH);
        return panel;
    }

    // --- 2. BOOKING ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JTextField date = new JTextField("yyyy-mm-dd"), board = new JTextField(), drop = new JTextField();
        JButton confirm = new JButton("Confirm Booking");
        bookVehID.setEditable(false); bookRouteID.setEditable(false); bookFare.setEditable(false);

        panel.add(new JLabel("Vehicle ID:")); panel.add(bookVehID);
        panel.add(new JLabel("Route ID:")); panel.add(bookRouteID);
        panel.add(new JLabel("Cost:")); panel.add(bookFare);
        panel.add(new JLabel("Date (yyyy-mm-dd):")); panel.add(date);
        panel.add(new JLabel("Boarding:")); panel.add(board);
        panel.add(new JLabel("Drop:")); panel.add(drop);
        panel.add(new JLabel("")); panel.add(confirm);

        confirm.addActionListener(e -> {
            try {
                ReservationBean rb = new ReservationBean();
                rb.setUserID(MainFrame.sessionUserID);
                rb.setVehicleID(bookVehID.getText()); rb.setRouteID(bookRouteID.getText());
                rb.setTotalFare(Double.parseDouble(bookFare.getText()));
                rb.setBookingDate(new java.sql.Date(System.currentTimeMillis()));
                rb.setJourneyDate(java.sql.Date.valueOf(date.getText()));
                rb.setBoardingPoint(board.getText()); rb.setDropPoint(drop.getText());
                
                String id = customerService.bookVehicle(rb);
                if(!id.equals("FAIL")) {
                    JOptionPane.showMessageDialog(this, "Success! Booking ID: " + id);
                    bookVehID.setText(""); bookRouteID.setText(""); bookFare.setText("");
                } else JOptionPane.showMessageDialog(this, "Failed");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });
        return panel;
    }

    // --- 3. STATUS ---
    private JPanel createViewBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inp = new JPanel();
        JTextField id = new JTextField(10); JButton chk = new JButton("Check");
        inp.add(new JLabel("Res ID:")); inp.add(id); inp.add(chk);
        
        JTextArea txt = new JTextArea(); txt.setEditable(false); txt.setFont(new Font("Monospaced", Font.BOLD, 14));
        
        chk.addActionListener(e -> {
            ReservationBean rb = customerService.viewBookingDetails(id.getText());
            if(rb != null && rb.getUserID().equals(MainFrame.sessionUserID)) {
                String driverInfo = "PENDING";
                if(rb.getDriverID() != null) {
                    DriverBean d = new com.ust.ata.dao.DriverDAO().findByID(rb.getDriverID());
                    if(d!=null) driverInfo = d.getName() + " (" + d.getMobileNo() + ")";
                }
                txt.setText("ID: " + rb.getReservationID() + "\nStatus: " + rb.getBookingStatus() + 
                            "\nRoute: " + rb.getRouteID() + "\nFare: " + rb.getTotalFare() +
                            "\nDriver: " + driverInfo);
            } else txt.setText("Not Found / Access Denied");
        });
        
        panel.add(inp, BorderLayout.NORTH); panel.add(new JScrollPane(txt), BorderLayout.CENTER);
        return panel;
    }
}





































//package com.ust.ata.ui;
//
//import com.ust.ata.bean.*;
//import com.ust.ata.service.Customer;
//import com.ust.ata.service.CustomerImpl;
//import com.ust.ata.util.UserImpl;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.ArrayList;
//
//public class CustomerPanel extends JPanel {
//    private MainFrame mainFrame;
//    private Customer customerService = new CustomerImpl();
//
//    public CustomerPanel(MainFrame mainFrame) {
//        this.mainFrame = mainFrame;
//        setLayout(new BorderLayout());
//
//        JTabbedPane tabbedPane = new JTabbedPane();
//        tabbedPane.addTab("Search Vehicles", createSearchPanel());
//        tabbedPane.addTab("Book Vehicle", createBookingPanel());
//        
//        JButton btnLogout = new JButton("Logout");
//        btnLogout.addActionListener(e -> {
//            new UserImpl().logout(MainFrame.sessionUserID);
//            mainFrame.showLogin();
//        });
//
//        add(tabbedPane, BorderLayout.CENTER);
//        add(btnLogout, BorderLayout.NORTH);
//    }
//
//    private JPanel createSearchPanel() {
//        JPanel panel = new JPanel(new BorderLayout());
//        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Seats", "Fare"}, 0);
//        JTable table = new JTable(model);
//
//        JPanel ctrl = new JPanel();
//        JComboBox<String> cbType = new JComboBox<>(new String[]{"AC", "NON AC"});
//        JButton btnSearch = new JButton("Search by Type");
//        ctrl.add(cbType); ctrl.add(btnSearch);
//
//        btnSearch.addActionListener(e -> {
//            model.setRowCount(0);
//            ArrayList<VehicleBean> list = customerService.viewVehiclesByType((String)cbType.getSelectedItem());
//            for(VehicleBean v : list)
//                model.addRow(new Object[]{v.getVehicleID(), v.getName(), v.getType(), v.getSeatingCapacity(), v.getFarePerKM()});
//        });
//
//        panel.add(new JScrollPane(table), BorderLayout.CENTER);
//        panel.add(ctrl, BorderLayout.NORTH);
//        return panel;
//    }
//
//    private JPanel createBookingPanel() {
//        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
//        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
//
//        JTextField txtVehID = new JTextField();
//        JTextField txtRouteID = new JTextField();
//        JTextField txtDate = new JTextField("yyyy-mm-dd");
//        JTextField txtBoard = new JTextField();
//        JTextField txtDrop = new JTextField();
//        JButton btnBook = new JButton("Confirm Booking");
//
//        panel.add(new JLabel("Vehicle ID:")); panel.add(txtVehID);
//        panel.add(new JLabel("Route ID:")); panel.add(txtRouteID);
//        panel.add(new JLabel("Journey Date (yyyy-mm-dd):")); panel.add(txtDate);
//        panel.add(new JLabel("Boarding Point:")); panel.add(txtBoard);
//        panel.add(new JLabel("Drop Point:")); panel.add(txtDrop);
//        panel.add(new JLabel("")); panel.add(btnBook);
//
//        btnBook.addActionListener(e -> {
//            try {
//                ReservationBean rb = new ReservationBean();
//                rb.setUserID(MainFrame.sessionUserID);
//                rb.setVehicleID(txtVehID.getText());
//                rb.setRouteID(txtRouteID.getText());
//                
//                // [FIX] Use java.sql.Date to ensure compatibility with both Util and SQL date types
//                rb.setBookingDate(new java.sql.Date(System.currentTimeMillis())); 
//                
//                // Validate and Parse Journey Date
//                rb.setJourneyDate(java.sql.Date.valueOf(txtDate.getText()));
//                
//                rb.setBoardingPoint(txtBoard.getText());
//                rb.setDropPoint(txtDrop.getText());
//                rb.setTotalFare(500.00); // Logic should calculate this based on distance
//
//                String id = customerService.bookVehicle(rb);
//                if(id != null && !id.equals("FAIL")) {
//                    JOptionPane.showMessageDialog(this, "Booking Successful! ID: " + id);
//                } else {
//                    JOptionPane.showMessageDialog(this, "Booking Failed.");
//                }
//            } catch(IllegalArgumentException ex) {
//                JOptionPane.showMessageDialog(this, "Invalid Date Format. Use yyyy-mm-dd");
//            } catch(Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage());
//            }
//        });
//
//        return panel;
//    }
//}