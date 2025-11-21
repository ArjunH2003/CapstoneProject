package com.ust.ata.dao;

import com.ust.ata.bean.*;
import com.ust.ata.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CredentialsDAO {

    /**
     * Authenticates the user against ATA_TBL_USER_CREDENTIALS.
     * Returns "A" (Admin), "C" (Customer), "FAIL" (Error), or "INVALID" (Wrong credentials).
     */
    public String authenticate(CredentialsBean credentialsBean) {
        String userType = "INVALID";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT USERTYPE FROM ATA_TBL_CREDENTIALS WHERE USERID=? AND PASSWORD=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, credentialsBean.getUserID());
            ps.setString(2, credentialsBean.getPassword());
            rs = ps.executeQuery();

            if (rs.next()) {
                userType = rs.getString("USERTYPE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "FAIL";
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return userType;
    }

    /**
     * Generates UserID: First 2 letters of First Name + 4 digit auto-incremented number.
     */
    private String generateUserID(String firstName) {
        String prefix = firstName.substring(0, 2).toUpperCase();
        String newID = prefix + "1000"; // Default start
        Connection con = DBUtil.getDBConnection("mysql");
        
        try {
            // Fetch the highest ID starting with this prefix
            String sql = "SELECT USERID FROM ATA_TBL_CREDENTIALS WHERE USERID LIKE ? ORDER BY USERID DESC LIMIT 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("USERID");
                // Extract the numeric part and increment
                int num = Integer.parseInt(lastID.substring(2));
                num++;
                newID = prefix + num;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) {}
        }
        return newID;
    }

    /**
     * Registers a new user. 
     * Inserts into both ATA_TBL_USER_CREDENTIALS and ATA_TBL_USER_PROFILE.
     * Uses Transaction Management to ensure data integrity[cite: 187, 657].
     */
    public String register(ProfileBean profileBean, String password) {
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement psCred = null;
        PreparedStatement psProfile = null;
        String generatedUserID = "FAIL";

        try {
            // 1. Generate ID
            generatedUserID = generateUserID(profileBean.getFirstName());

            // 2. Disable Auto-Commit for Transaction
            con.setAutoCommit(false);

            // 3. Insert into Credentials Table
            String sqlCred = "INSERT INTO ATA_TBL_CREDENTIALS (USERID, PASSWORD, USERTYPE, LOGINSTATUS) VALUES (?, ?, ?, ?)";
            psCred = con.prepareStatement(sqlCred);
            psCred.setString(1, generatedUserID);
            psCred.setString(2, password);
            psCred.setString(3, "C"); // Default to 'C' for Customer registration
            psCred.setInt(4, 0); // Default LoginStatus 0 (Logged Out)
            psCred.executeUpdate();

            // 4. Insert into Profile Table [cite: 741]
            String sqlProfile = "INSERT INTO ATA_TBL_USER_PROFILE " +
                    "(USERID, FIRSTNAME, LASTNAME, DATEOFBIRTH, GENDER, STREET, LOCATION, CITY, STATE, PINCODE, MOBILENO, EMAILID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            psProfile = con.prepareStatement(sqlProfile);
            psProfile.setString(1, generatedUserID);
            psProfile.setString(2, profileBean.getFirstName());
            psProfile.setString(3, profileBean.getLastName());
            psProfile.setDate(4, new java.sql.Date(profileBean.getDateOfBirth().getTime()));
            psProfile.setString(5, profileBean.getGender());
            psProfile.setString(6, profileBean.getStreet());
            psProfile.setString(7, profileBean.getLocation());
            psProfile.setString(8, profileBean.getCity());
            psProfile.setString(9, profileBean.getState());
            psProfile.setString(10, profileBean.getPincode());
            psProfile.setString(11, profileBean.getMobileNo());
            psProfile.setString(12, profileBean.getEmailID());
            psProfile.executeUpdate();

            // 5. Commit Transaction
            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // Rollback if any error occurs
            } catch (SQLException ex) { ex.printStackTrace(); }
            return "FAIL";
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true); // Reset auto-commit
                    con.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return generatedUserID;
    }

    /**
     * Changes the user's password.
     * Returns "SUCCESS", "FAIL", or "INVALID".
     */
    public String changePassword(CredentialsBean credentialsBean, String newPassword) {
        Connection con = DBUtil.getDBConnection("mysql");
        try {
            // First verify the old password exists
            String authCheck = authenticate(credentialsBean);
            if (authCheck.equals("INVALID") || authCheck.equals("FAIL")) {
                return "INVALID";
            }

            String sql = "UPDATE ATA_TBL_CREDENTIALS SET PASSWORD=? WHERE USERID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setString(2, credentialsBean.getUserID());
            
            int rows = ps.executeUpdate();
            return (rows > 0) ? "SUCCESS" : "FAIL";

        } catch (SQLException e) {
            e.printStackTrace();
            return "FAIL";
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Updates the login status (1 for logged in, 0 for logged out).
     * Used by Authentication interface[cite: 653].
     */
    public boolean changeLoginStatus(CredentialsBean credentialsBean, int loginStatus) {
        Connection con = DBUtil.getDBConnection("mysql");
        try {
            String sql = "UPDATE ATA_TBL_CREDENTIALS SET LOGINSTATUS=? WHERE USERID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, loginStatus);
            ps.setString(2, credentialsBean.getUserID());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) {}
        }
    }
}