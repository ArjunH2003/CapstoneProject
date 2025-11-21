package com.ust.ata.dao;
import com.ust.ata.bean.VehicleBean;
import com.ust.ata.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VehicleDAO {

    /**
     * Generates a unique VehicleID.
     * Logic: First 2 letters of Vehicle Name + 4 digit auto-incremented number.
     * Example: Name="Volvo" -> Prefix="VO" -> ID="VO1000"
     * Reference: DD Appendix
     */
    private String generateVehicleID(String name) {
        // Handle cases where name is short
        String prefix = (name != null && name.length() >= 2) ? name.substring(0, 2).toUpperCase() : "XX";
        String newID = prefix + "1000"; // Default start ID

        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Find the highest ID currently existing with this prefix
            String sql = "SELECT VEHICLEID FROM ATA_TBL_VEHICLE WHERE VEHICLEID LIKE ? ORDER BY VEHICLEID DESC LIMIT 1";
            ps = con.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("VEHICLEID");
                // Extract the numeric part (index 2 onwards)
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
     * Creates a new Vehicle.
     * Reference: DD DAO Method Summary - String createXYZ(BeanObject)
     */
    public String createVehicle(VehicleBean vehicleBean) {
        String result = "FAIL";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String generatedID = generateVehicleID(vehicleBean.getName());

         // [FIX] Added ROUTEID and the 7th '?'
            String sql = "INSERT INTO ATA_TBL_VEHICLE (VEHICLEID, NAME, TYPE, REGISTRATIONNUMBER, SEATINGCAPACITY, FAREPERKM, ROUTEID) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, generatedID);
            ps.setString(2, vehicleBean.getName());
            ps.setString(3, vehicleBean.getType());
            ps.setString(4, vehicleBean.getRegistrationNumber());
            ps.setInt(5, vehicleBean.getSeatingCapacity());
            ps.setDouble(6, vehicleBean.getFarePerKM());
            ps.setString(7, vehicleBean.getRouteID());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                result = generatedID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "FAIL";
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    /**
     * Deletes vehicles based on a list of Vehicle IDs.
     * Reference: DD DAO Method Summary - int deleteXYZ(ArrayList<String>)
     */
    public int deleteVehicle(ArrayList<String> vehicleIDs) {
        int count = 0;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBUtil.getDBConnection("mysql");
            String sql = "DELETE FROM ATA_TBL_VEHICLE WHERE VEHICLEID=?";
            ps = con.prepareStatement(sql);

            for (String id : vehicleIDs) {
                ps.setString(1, id);
                count += ps.executeUpdate();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // [FIX] Return -1 to indicate "Cannot delete because used elsewhere"
            return -1; 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return count;
    }

    /**
     * Modifies an existing vehicle's details.
     * Reference: DD DAO Method Summary - boolean updateXYZ(BeanObject)
     */
    public boolean modifyVehicle(VehicleBean vehicleBean) {
        boolean success = false;
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = DBUtil.getDBConnection("mysql");
            
            // SQL Query: 6 columns to update + 1 for WHERE clause = 7 Parameters
            String sql = "UPDATE ATA_TBL_VEHICLE SET NAME=?, TYPE=?, REGISTRATIONNUMBER=?, SEATINGCAPACITY=?, FAREPERKM=?, ROUTEID=? WHERE VEHICLEID=?";
            
            ps = con.prepareStatement(sql);
            
            // 1. NAME (VARCHAR 20)
            ps.setString(1, vehicleBean.getName());
            
            // 2. TYPE (VARCHAR 8) - Ensure value is either 'AC' or 'NON AC'
            // Truncate if necessary to prevent error, though UI dropdown prevents this usually
            String type = vehicleBean.getType();
            if (type.length() > 8) type = type.substring(0, 8);
            ps.setString(2, type);
            
            // 3. REGISTRATIONNUMBER (VARCHAR 20)
            ps.setString(3, vehicleBean.getRegistrationNumber());
            
            // 4. SEATINGCAPACITY (INT 3)
            ps.setInt(4, vehicleBean.getSeatingCapacity());
            
            // 5. FAREPERKM (DECIMAL)
            ps.setDouble(5, vehicleBean.getFarePerKM());
            
            // 6. ROUTEID (VARCHAR 8)
            // Handle "None" from UI logic
            String rID = vehicleBean.getRouteID();
            if (rID == null || rID.equals("None") || rID.isEmpty()) {
                ps.setString(6, null); // Set to NULL in DB
            } else {
                ps.setString(6, rID);
            }
            
            // 7. VEHICLEID (VARCHAR 6) - This is the WHERE clause
            ps.setString(7, vehicleBean.getVehicleID());

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
     * Retrieves all vehicles.
     * Reference: DD DAO Method Summary - ArrayList<BeanObject> findAll()
     */
    public ArrayList<VehicleBean> findAll() {
        ArrayList<VehicleBean> vehicleList = new ArrayList<>();
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_VEHICLE";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                VehicleBean vehicle = new VehicleBean();
                vehicle.setVehicleID(rs.getString("VEHICLEID"));
                vehicle.setName(rs.getString("NAME"));
                vehicle.setType(rs.getString("TYPE"));
                vehicle.setRegistrationNumber(rs.getString("REGISTRATIONNUMBER"));
                vehicle.setSeatingCapacity(rs.getInt("SEATINGCAPACITY"));
                vehicle.setFarePerKM(rs.getDouble("FAREPERKM"));
                vehicle.setRouteID(rs.getString("ROUTEID"));
                vehicleList.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return vehicleList;
    }

    /**
     * Finds a specific vehicle by ID.
     * Reference: DD DAO Method Summary - BeanObject findByID(String)
     */
    public VehicleBean findByID(String vehicleID) {
        VehicleBean vehicle = null;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_VEHICLE WHERE VEHICLEID=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, vehicleID);
            rs = ps.executeQuery();

            if (rs.next()) {
                vehicle = new VehicleBean();
                vehicle.setVehicleID(rs.getString("VEHICLEID"));
                vehicle.setName(rs.getString("NAME"));
                vehicle.setType(rs.getString("TYPE"));
                vehicle.setRegistrationNumber(rs.getString("REGISTRATIONNUMBER"));
                vehicle.setSeatingCapacity(rs.getInt("SEATINGCAPACITY"));
                vehicle.setFarePerKM(rs.getDouble("FAREPERKM"));
                vehicle.setRouteID(rs.getString("ROUTEID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return vehicle;
    }
}