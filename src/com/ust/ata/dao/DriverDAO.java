package com.ust.ata.dao;

import com.ust.ata.bean.DriverBean;
import com.ust.ata.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DriverDAO {

    /**
     * Generates a unique DriverID based on the rule:
     * First 2 letters of Name + 4 digit auto-incremented number.
     * Reference: DD 
     */
    private String generateDriverID(String name) {
        if (name == null || name.length() < 2) {
            name = "XX"; // Fallback if name is too short
        }
        
        String prefix = name.substring(0, 2).toUpperCase();
        String newID = prefix + "1000"; // Default start ID

        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Find the highest ID currently existing with this prefix
            String sql = "SELECT DRIVERID FROM ATA_TBL_DRIVER WHERE DRIVERID LIKE ? ORDER BY DRIVERID DESC LIMIT 1";
            ps = con.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("DRIVERID");
                // Extract the numeric part (substring from index 2) and increment
                int num = Integer.parseInt(lastID.substring(2));
                num++;
                newID = prefix + num;
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return newID;
    }

    /**
     * Creates a new Driver entry in the database.
     * Reference: DD DAO Method Summary 
     */
    public String createDriver(DriverBean driverBean) {
        String result = "FAIL";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String generatedID = generateDriverID(driverBean.getName());

            String sql = "INSERT INTO ATA_TBL_DRIVER (DRIVERID, NAME, STREET, LOCATION, CITY, STATE, PINCODE, MOBILENO, LICENSENUMBER) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, generatedID);
            ps.setString(2, driverBean.getName());
            ps.setString(3, driverBean.getStreet());
            ps.setString(4, driverBean.getLocation());
            ps.setString(5, driverBean.getCity());
            ps.setString(6, driverBean.getState());
            ps.setString(7, driverBean.getPincode());
            ps.setString(8, driverBean.getMobileNo());
            ps.setString(9, driverBean.getLicenseNumber());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                result = generatedID;
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // [FIX] Catch duplicate entry error specifically
            System.err.println("Duplicate License Number detected.");
            return "DUPLICATE"; 
        } catch (SQLException e) {
            e.printStackTrace();
            result = "FAIL";
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    /**
     * Deletes a list of drivers based on their IDs.
     * Reference: DD DAO Method Summary - int deleteXYZ(ArrayList<String>) 
     */
    public int deleteDriver(ArrayList<String> driverIDs) {
        int count = 0;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ATA_TBL_DRIVER WHERE DRIVERID=?";
            ps = con.prepareStatement(sql);

            for (String id : driverIDs) {
                ps.setString(1, id);
                count += ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return count;
    }

    /**
     * Modifies an existing driver's details.
     * Reference: DD DAO Method Summary - boolean updateXYZ(BeanObject) 
     */
    public boolean modifyDriver(DriverBean driverBean) {
        boolean success = false;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ATA_TBL_DRIVER SET NAME=?, STREET=?, LOCATION=?, CITY=?, STATE=?, PINCODE=?, MOBILENO=?, LICENSENUMBER=? " +
                         "WHERE DRIVERID=?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, driverBean.getName());
            ps.setString(2, driverBean.getStreet());
            ps.setString(3, driverBean.getLocation());
            ps.setString(4, driverBean.getCity());
            ps.setString(5, driverBean.getState());
            ps.setString(6, driverBean.getPincode());
            ps.setString(7, driverBean.getMobileNo());
            ps.setString(8, driverBean.getLicenseNumber());
            ps.setString(9, driverBean.getDriverID());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    /**
     * Retrieves all drivers from the database.
     * Reference: DD DAO Method Summary - ArrayList<BeanObject> findAll() 
     */
    public ArrayList<DriverBean> findAll() {
        ArrayList<DriverBean> driverList = new ArrayList<>();
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_DRIVER";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                DriverBean driver = new DriverBean();
                driver.setDriverID(rs.getString("DRIVERID"));
                driver.setName(rs.getString("NAME"));
                driver.setStreet(rs.getString("STREET"));
                driver.setLocation(rs.getString("LOCATION"));
                driver.setCity(rs.getString("CITY"));
                driver.setState(rs.getString("STATE"));
                driver.setPincode(rs.getString("PINCODE"));
                driver.setMobileNo(rs.getString("MOBILENO"));
                driver.setLicenseNumber(rs.getString("LICENSENUMBER"));
                
                driverList.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return driverList;
    }

    /**
     * Finds a specific driver by ID.
     * Reference: DD DAO Method Summary - BeanObject findByID(String) 
     */
    public DriverBean findByID(String driverID) {
        DriverBean driver = null;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_DRIVER WHERE DRIVERID=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, driverID);
            rs = ps.executeQuery();

            if (rs.next()) {
                driver = new DriverBean();
                driver.setDriverID(rs.getString("DRIVERID"));
                driver.setName(rs.getString("NAME"));
                driver.setStreet(rs.getString("STREET"));
                driver.setLocation(rs.getString("LOCATION"));
                driver.setCity(rs.getString("CITY"));
                driver.setState(rs.getString("STATE"));
                driver.setPincode(rs.getString("PINCODE"));
                driver.setMobileNo(rs.getString("MOBILENO"));
                driver.setLicenseNumber(rs.getString("LICENSENUMBER"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return driver;
    }
}