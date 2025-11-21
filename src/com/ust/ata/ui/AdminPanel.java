package com.ust.ata.ui;

import com.ust.ata.bean.*;
import com.ust.ata.dao.RouteDAO;
import com.ust.ata.service.Administrator;
import com.ust.ata.service.AdministratorImpl;
import com.ust.ata.util.UserImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AdminPanel extends JPanel {
    private MainFrame mainFrame;
    private Administrator adminService = new AdministratorImpl();

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG_COLOR);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_REGULAR);
        
        tabbedPane.addTab("Manage Vehicles", createVehiclePanel());
        tabbedPane.addTab("Manage Drivers", createDriverPanel());
        tabbedPane.addTab("Manage Routes", createRoutePanel());
        tabbedPane.addTab("Bookings & Allotment", createBookingPanel());

        // Header / Logout
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_COLOR);
        JLabel title = new JLabel(" Administrator Dashboard");
        Theme.styleHeader(title);
        
        JButton btnLogout = new JButton("Logout");
        Theme.styleButton(btnLogout);
        btnLogout.setBackground(new Color(220, 53, 69)); // Red for Logout
        btnLogout.addActionListener(e -> {
            new UserImpl().logout(MainFrame.sessionUserID);
            mainFrame.showLogin();
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        add(tabbedPane, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);
    }

    // --- 1. VEHICLE MANAGEMENT ---
    private JPanel createVehiclePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] cols = {"ID", "Name", "Type", "Reg No", "Seats", "Fare/KM", "Assigned Route"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        
        JPanel form = new JPanel(new GridLayout(2, 6, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Vehicle Details"));
        
        JTextField txtName = new JTextField();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"AC", "NON AC"});
        JTextField txtReg = new JTextField();
        JTextField txtSeats = new JTextField();
        JTextField txtFare = new JTextField();
        JComboBox<String> cbRoute = new JComboBox<>();
        
        // Populate Routes
        cbRoute.addItem("None");
        for(RouteBean r : new RouteDAO().findAll()) cbRoute.addItem(r.getRouteID());

        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Type:")); form.add(cbType);
        form.add(new JLabel("Reg No:")); form.add(txtReg);
        form.add(new JLabel("Seats:")); form.add(txtSeats);
        form.add(new JLabel("Fare:")); form.add(txtFare);
        form.add(new JLabel("Route:")); form.add(cbRoute);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnModify = new JButton("Update");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete");
        btnPanel.add(btnAdd); btnPanel.add(btnModify); btnPanel.add(btnRefresh); btnPanel.add(btnDelete);

        // Auto-Fill
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtName.setText((String) model.getValueAt(row, 1));
                    cbType.setSelectedItem((String) model.getValueAt(row, 2));
                    txtReg.setText((String) model.getValueAt(row, 3));
                    txtSeats.setText(model.getValueAt(row, 4).toString());
                    txtFare.setText(model.getValueAt(row, 5).toString());
                    String r = (String) model.getValueAt(row, 6);
                    cbRoute.setSelectedItem(r != null ? r : "None");
                }
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                VehicleBean vb = new VehicleBean();
                vb.setName(txtName.getText()); vb.setType((String)cbType.getSelectedItem());
                vb.setRegistrationNumber(txtReg.getText()); 
                vb.setSeatingCapacity(Integer.parseInt(txtSeats.getText()));
                vb.setFarePerKM(Double.parseDouble(txtFare.getText()));
                if(!"None".equals(cbRoute.getSelectedItem())) vb.setRouteID((String)cbRoute.getSelectedItem());

                String res = adminService.addVehicle(vb);
                if(!res.equals("FAIL")) { JOptionPane.showMessageDialog(this, "Added: " + res); btnRefresh.doClick(); }
                else JOptionPane.showMessageDialog(this, "Failed");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        btnModify.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) return;
            try {
                VehicleBean vb = new VehicleBean();
                vb.setVehicleID((String)model.getValueAt(row, 0));
                vb.setName(txtName.getText()); vb.setType((String)cbType.getSelectedItem());
                vb.setRegistrationNumber(txtReg.getText()); 
                vb.setSeatingCapacity(Integer.parseInt(txtSeats.getText()));
                vb.setFarePerKM(Double.parseDouble(txtFare.getText()));
                if(!"None".equals(cbRoute.getSelectedItem())) vb.setRouteID((String)cbRoute.getSelectedItem());
                
                if(adminService.modifyVehicle(vb)) { JOptionPane.showMessageDialog(this, "Updated"); btnRefresh.doClick(); }
                else JOptionPane.showMessageDialog(this, "Failed");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            cbRoute.removeAllItems(); cbRoute.addItem("None");
            for(RouteBean r : new RouteDAO().findAll()) cbRoute.addItem(r.getRouteID());
            for(VehicleBean v : adminService.viewAllVehicles())
                model.addRow(new Object[]{v.getVehicleID(), v.getName(), v.getType(), v.getRegistrationNumber(), v.getSeatingCapacity(), v.getFarePerKM(), v.getRouteID()});
        });

        btnDelete.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if(rows.length == 0) return;
            if(JOptionPane.showConfirmDialog(this, "Confirm Delete?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ArrayList<String> ids = new ArrayList<>();
                for(int r : rows) ids.add((String)model.getValueAt(r, 0));
                int res = adminService.deleteVehicle(ids);
                if(res == -1) JOptionPane.showMessageDialog(this, "Cannot Delete: Vehicle Allocated", "Error", JOptionPane.ERROR_MESSAGE);
                else { JOptionPane.showMessageDialog(this, "Deleted " + res); btnRefresh.doClick(); }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER); south.add(btnPanel, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // --- 2. DRIVER MANAGEMENT ---
    private JPanel createDriverPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Name", "License", "Mobile", "City", "Location"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        
        JPanel form = new JPanel(new GridLayout(0, 4, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Driver Details"));
        JTextField txtName = new JTextField(), txtMob = new JTextField(), txtLic = new JTextField();
        JTextField txtLoc = new JTextField(), txtCity = new JTextField(), txtPin = new JTextField();
        
        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Mobile:")); form.add(txtMob);
        form.add(new JLabel("License:")); form.add(txtLic);
        form.add(new JLabel("Location:")); form.add(txtLoc);
        form.add(new JLabel("City:")); form.add(txtCity);
        form.add(new JLabel("Pincode:")); form.add(txtPin);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnModify = new JButton("Update");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDelete = new JButton("Delete");
        btnPanel.add(btnAdd); btnPanel.add(btnModify); btnPanel.add(btnRefresh); btnPanel.add(btnDelete);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row != -1) {
                    txtName.setText((String) model.getValueAt(row, 1));
                    txtLic.setText((String) model.getValueAt(row, 2));
                    txtMob.setText((String) model.getValueAt(row, 3));
                    txtCity.setText((String) model.getValueAt(row, 4));
                    txtLoc.setText((String) model.getValueAt(row, 5));
                }
            }
        });

        btnAdd.addActionListener(e -> {
            if(txtName.getText().isEmpty()) return;
            DriverBean db = new DriverBean();
            db.setName(txtName.getText()); db.setMobileNo(txtMob.getText()); db.setLicenseNumber(txtLic.getText());
            db.setLocation(txtLoc.getText()); db.setCity(txtCity.getText()); db.setPincode(txtPin.getText());
            db.setStreet("NA"); db.setState("NA");
            String res = adminService.addDriver(db);
            if(res.equals("DUPLICATE")) JOptionPane.showMessageDialog(this, "License Exists!");
            else if(!res.equals("FAIL")) { JOptionPane.showMessageDialog(this, "Added: " + res); btnRefresh.doClick(); }
        });

        btnModify.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) return;
            DriverBean db = new DriverBean();
            db.setDriverID((String)model.getValueAt(row, 0));
            db.setName(txtName.getText()); db.setMobileNo(txtMob.getText()); db.setLicenseNumber(txtLic.getText());
            db.setLocation(txtLoc.getText()); db.setCity(txtCity.getText()); db.setPincode(txtPin.getText());
            db.setStreet("NA"); db.setState("NA");
            if(adminService.modifyDriver(db)) { JOptionPane.showMessageDialog(this, "Updated"); btnRefresh.doClick(); }
        });

        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            for(DriverBean d : new com.ust.ata.dao.DriverDAO().findAll())
                model.addRow(new Object[]{d.getDriverID(), d.getName(), d.getLicenseNumber(), d.getMobileNo(), d.getCity(), d.getLocation()});
        });

        btnDelete.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if(rows.length > 0 && JOptionPane.showConfirmDialog(this, "Confirm Delete?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ArrayList<String> ids = new ArrayList<>();
                for(int r : rows) ids.add((String)model.getValueAt(r, 0));
                int res = adminService.deleteDriver(ids);
                if(res == -1) JOptionPane.showMessageDialog(this, "Cannot Delete: Driver Allocated", "Error", JOptionPane.ERROR_MESSAGE);
                else { JOptionPane.showMessageDialog(this, "Deleted " + res); btnRefresh.doClick(); }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER); south.add(btnPanel, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // --- 3. ROUTE MANAGEMENT ---
    private JPanel createRoutePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Source", "Dest", "Dist", "Dur"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        
        JPanel form = new JPanel();
        JTextField txtSrc = new JTextField(8), txtDest = new JTextField(8), txtDist = new JTextField(5), txtDur = new JTextField(3);
        form.add(new JLabel("Src:")); form.add(txtSrc); form.add(new JLabel("Dest:")); form.add(txtDest);
        form.add(new JLabel("Dist:")); form.add(txtDist); form.add(new JLabel("Dur:")); form.add(txtDur);
        
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add"); JButton btnUpd = new JButton("Update"); 
        JButton btnRef = new JButton("Refresh"); JButton btnDel = new JButton("Delete");
        btnPanel.add(btnAdd); btnPanel.add(btnUpd); btnPanel.add(btnRef); btnPanel.add(btnDel);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if(r != -1) {
                    txtSrc.setText((String)model.getValueAt(r, 1)); txtDest.setText((String)model.getValueAt(r, 2));
                    txtDist.setText(model.getValueAt(r, 3).toString()); txtDur.setText(model.getValueAt(r, 4).toString());
                }
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                RouteBean rb = new RouteBean();
                rb.setSource(txtSrc.getText()); rb.setDestination(txtDest.getText());
                rb.setDistance(Integer.parseInt(txtDist.getText())); rb.setTravelDuration(Integer.parseInt(txtDur.getText()));
                String res = adminService.addRoute(rb);
                if(!res.equals("FAIL")) { JOptionPane.showMessageDialog(this, "Added: " + res); btnRef.doClick(); }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        btnUpd.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row == -1) return;
            try {
                RouteBean rb = new RouteBean();
                rb.setRouteID((String)model.getValueAt(row, 0));
                rb.setSource(txtSrc.getText()); rb.setDestination(txtDest.getText());
                rb.setDistance(Integer.parseInt(txtDist.getText())); rb.setTravelDuration(Integer.parseInt(txtDur.getText()));
                if(adminService.modifyRoute(rb)) { JOptionPane.showMessageDialog(this, "Updated"); btnRef.doClick(); }
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Invalid Input"); }
        });

        btnRef.addActionListener(e -> {
            model.setRowCount(0);
            for(RouteBean r : new RouteDAO().findAll()) 
                model.addRow(new Object[]{r.getRouteID(), r.getSource(), r.getDestination(), r.getDistance(), r.getTravelDuration()});
        });

        btnDel.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if(rows.length > 0 && JOptionPane.showConfirmDialog(this, "Confirm Delete?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                ArrayList<String> ids = new ArrayList<>();
                for(int r : rows) ids.add((String)model.getValueAt(r, 0));
                int res = adminService.deleteRoute(ids);
                if(res == -1) JOptionPane.showMessageDialog(this, "Cannot Delete: Allocated", "Error", JOptionPane.ERROR_MESSAGE);
                else { JOptionPane.showMessageDialog(this, "Deleted " + res); btnRef.doClick(); }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER); south.add(btnPanel, BorderLayout.SOUTH);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // --- 4. BOOKING ALLOTMENT ---
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Res ID", "User", "Route", "Date", "Status", "Driver"}, 0);
        JTable table = new JTable(model);
        
        JPanel ctrl = new JPanel();
        JTextField txtDriver = new JTextField(8);
        JButton btnAllot = new JButton("Allot Driver");
        JButton btnView = new JButton("Load Bookings");
        ctrl.add(new JLabel("Driver ID:")); ctrl.add(txtDriver); ctrl.add(btnAllot); ctrl.add(btnView);

        btnView.addActionListener(e -> {
            model.setRowCount(0);
            for(ReservationBean rb : new com.ust.ata.dao.ReservationDAO().findAll())
                model.addRow(new Object[]{rb.getReservationID(), rb.getUserID(), rb.getRouteID(), rb.getJourneyDate(), rb.getBookingStatus(), rb.getDriverID()});
        });

        btnAllot.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Select Booking"); return; }
            if(adminService.allotDriver((String)model.getValueAt(r, 0), txtDriver.getText())) {
                JOptionPane.showMessageDialog(this, "Allocated"); btnView.doClick();
            } else JOptionPane.showMessageDialog(this, "Failed");
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(ctrl, BorderLayout.SOUTH);
        return panel;
    }
}