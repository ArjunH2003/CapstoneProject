package com.ust.ata.ui;

import com.ust.ata.bean.*;
import com.ust.ata.service.Administrator;
import com.ust.ata.service.AdministratorImpl;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminPanel extends JPanel {
    private MainFrame mainFrame;
    private Administrator adminService = new AdministratorImpl();

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Vehicles", createVehiclePanel());
        tabbedPane.addTab("Manage Drivers", createDriverPanel());
        tabbedPane.addTab("Manage Routes", createRoutePanel());
        tabbedPane.addTab("Bookings & Allotment", createBookingPanel());

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new UserImpl().logout(MainFrame.sessionUserID);
            mainFrame.showLogin();
        });

        add(tabbedPane, BorderLayout.CENTER);
        add(btnLogout, BorderLayout.NORTH);
    }

    // --- VEHICLE TAB ---
    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Name", "Type", "Reg No", "Seats", "Fare/KM"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        // Form
        JPanel form = new JPanel(new FlowLayout());
        JTextField txtName = new JTextField(10), txtReg = new JTextField(8), txtSeats = new JTextField(3), txtFare = new JTextField(4);
        JComboBox<String> cbType = new JComboBox<>(new String[]{"AC", "NON AC"});
        JButton btnAdd = new JButton("Add");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnModify = new JButton("Modify Selected");

        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Type:")); form.add(cbType);
        form.add(new JLabel("Reg:")); form.add(txtReg);
        form.add(new JLabel("Seats:")); form.add(txtSeats);
        form.add(new JLabel("Fare:")); form.add(txtFare);
        form.add(btnAdd); form.add(btnRefresh); form.add(btnDelete); form.add(btnModify);

        // Listeners
        btnAdd.addActionListener(e -> {
            try {
                VehicleBean vb = new VehicleBean();
                vb.setName(txtName.getText()); 
                vb.setType((String)cbType.getSelectedItem());
                vb.setRegistrationNumber(txtReg.getText()); 
                vb.setSeatingCapacity(Integer.parseInt(txtSeats.getText()));
                vb.setFarePerKM(Double.parseDouble(txtFare.getText()));
                
                String result = adminService.addVehicle(vb);
                if(result.equals("FAIL")) JOptionPane.showMessageDialog(this, "Failed to add Vehicle");
                else {
                    JOptionPane.showMessageDialog(this, "Vehicle Added: " + result);
                    btnRefresh.doClick();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Input");
            }
        });

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            // Uses the viewAllVehicles method added in the previous step
            ArrayList<VehicleBean> list = adminService.viewAllVehicles();
            if (list != null) {
                for (VehicleBean v : list)
                    model.addRow(new Object[]{v.getVehicleID(), v.getName(), v.getType(), v.getRegistrationNumber(), v.getSeatingCapacity(), v.getFarePerKM()});
            }
        });

        btnDelete.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if(rows.length == 0) return;
            ArrayList<String> ids = new ArrayList<>();
            for(int r : rows) ids.add((String)model.getValueAt(r, 0));
            adminService.deleteVehicle(ids);
            btnRefresh.doClick();
        });

        btnModify.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) return;
            String id = (String)model.getValueAt(r, 0);
            VehicleBean vb = adminService.viewVehicle(id);
            if(vb != null) {
                String newFare = JOptionPane.showInputDialog("Enter new Fare for " + vb.getName(), vb.getFarePerKM());
                if(newFare != null) {
                    vb.setFarePerKM(Double.parseDouble(newFare));
                    adminService.modifyVehicle(vb);
                    btnRefresh.doClick();
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // --- DRIVER TAB (Includes the Duplicate Fix) ---
    private JPanel createDriverPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Name", "License", "Mobile", "City"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        JPanel form = new JPanel();
        JTextField txtName = new JTextField(10), txtLic = new JTextField(10), txtMob = new JTextField(10);
        JButton btnAdd = new JButton("Add Driver");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete");

        form.add(new JLabel("Name")); form.add(txtName);
        form.add(new JLabel("License")); form.add(txtLic);
        form.add(new JLabel("Mobile")); form.add(txtMob);
        form.add(btnAdd); form.add(btnRefresh); form.add(btnDelete);

        btnAdd.addActionListener(e -> {
            DriverBean db = new DriverBean();
            db.setName(txtName.getText()); 
            db.setLicenseNumber(txtLic.getText()); 
            db.setMobileNo(txtMob.getText());
            // Default values for address fields as per simple UI input
            db.setStreet("Default St"); db.setLocation("Default Loc"); 
            db.setCity("City"); db.setState("State"); db.setPincode("000000");
            
            String result = adminService.addDriver(db);
            
            // [FIX] Handle Duplicate License Error
            if (result.equals("DUPLICATE")) {
                JOptionPane.showMessageDialog(this, "Error: License Number already exists!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            } else if (result.equals("FAIL")) {
                JOptionPane.showMessageDialog(this, "Failed to add driver.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Driver Added Successfully! ID: " + result);
                btnRefresh.doClick();
            }
        });

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            // Ensure you add `viewAllDrivers()` to Administrator interface returning driverDAO.findAll()
            // If you haven't added it yet, this line will need that method.
            // For now, assuming you added it:
             // ArrayList<DriverBean> list = adminService.viewAllDrivers();
             // for(DriverBean d : list) model.addRow(new Object[]{d.getDriverID(), d.getName(), d.getLicenseNumber(), d.getMobileNo(), d.getCity()});
            
            // If you haven't added viewAllDrivers to Service, you can temporarily use DAO directly here:
            com.ust.ata.dao.DriverDAO tempDao = new com.ust.ata.dao.DriverDAO();
            for(DriverBean d : tempDao.findAll()) {
                model.addRow(new Object[]{d.getDriverID(), d.getName(), d.getLicenseNumber(), d.getMobileNo(), d.getCity()});
            }
        });
        
        btnDelete.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if(rows.length == 0) return;
            ArrayList<String> ids = new ArrayList<>();
            for(int r : rows) ids.add((String)model.getValueAt(r, 0));
            adminService.deleteDriver(ids);
            btnRefresh.doClick();
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // --- ROUTE TAB ---
    private JPanel createRoutePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Source", "Dest", "Dist", "Dur"}, 0);
        JTable table = new JTable(model);
        
        JPanel form = new JPanel();
        JTextField txtSrc = new JTextField(8), txtDest = new JTextField(8), txtDist = new JTextField(5), txtDur = new JTextField(3);
        JButton btnAdd = new JButton("Add Route");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete");
        
        form.add(new JLabel("Src")); form.add(txtSrc); form.add(new JLabel("Dest")); form.add(txtDest);
        form.add(new JLabel("Dist")); form.add(txtDist); form.add(new JLabel("Dur")); form.add(txtDur);
        form.add(btnAdd); form.add(btnRefresh); form.add(btnDelete);

        btnAdd.addActionListener(e -> {
            try {
                RouteBean rb = new RouteBean();
                rb.setSource(txtSrc.getText()); rb.setDestination(txtDest.getText());
                rb.setDistance(Integer.parseInt(txtDist.getText()));
                rb.setTravelDuration(Integer.parseInt(txtDur.getText()));
                
                String res = adminService.addRoute(rb);
                if(!res.equals("FAIL")) {
                    JOptionPane.showMessageDialog(this, "Route Added: " + res);
                    btnRefresh.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Route");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Distance/Duration must be numbers");
            }
        });
        
        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            // Using DAO directly if service method missing, otherwise use adminService.viewAllRoutes()
            com.ust.ata.dao.RouteDAO tempDao = new com.ust.ata.dao.RouteDAO();
            for(RouteBean r : tempDao.findAll()) {
                model.addRow(new Object[]{r.getRouteID(), r.getSource(), r.getDestination(), r.getDistance(), r.getTravelDuration()});
            }
        });

        btnDelete.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if(rows.length == 0) return;
            ArrayList<String> ids = new ArrayList<>();
            for(int r : rows) ids.add((String)model.getValueAt(r, 0));
            adminService.deleteRoute(ids);
            btnRefresh.doClick();
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // --- BOOKING & ALLOTMENT TAB ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Res ID", "User", "Route", "Date", "Status", "Driver"}, 0);
        JTable table = new JTable(model);

        JPanel ctrl = new JPanel();
        JTextField txtDriverID = new JTextField(8);
        JButton btnAllot = new JButton("Allot Driver ID to Selected Booking");
        JButton btnView = new JButton("View All Bookings"); 

        ctrl.add(new JLabel("Driver ID:")); ctrl.add(txtDriverID); ctrl.add(btnAllot); ctrl.add(btnView);

        btnView.addActionListener(e -> {
            model.setRowCount(0);
            // Using DAO directly to fetch all bookings for Admin view as Service interface in DD was specific to filters
            com.ust.ata.dao.ReservationDAO tempDao = new com.ust.ata.dao.ReservationDAO();
            for(ReservationBean rb : tempDao.findAll()) {
                 model.addRow(new Object[]{rb.getReservationID(), rb.getUserID(), rb.getRouteID(), rb.getJourneyDate(), rb.getBookingStatus(), rb.getDriverID()});
            }
        });

        btnAllot.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking first");
                return;
            }
            String resID = (String)model.getValueAt(r, 0);
            String driverID = txtDriverID.getText();
            if(driverID.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Enter Driver ID");
                 return;
            }
            boolean success = adminService.allotDriver(resID, driverID);
            if(success) {
                JOptionPane.showMessageDialog(this, "Driver Allotted Successfully");
                btnView.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Allot Driver (Check IDs)");
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(ctrl, BorderLayout.SOUTH);
        return panel;
    }
}