package com.ust.ata.dao;

import com.ust.ata.bean.ProfileBean;
import com.ust.ata.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProfileDAO {

    public String createProfile(ProfileBean profileBean) {
        String result = "FAIL";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO ATA_TBL_USER_PROFILE " +
                         "(USERID, FIRSTNAME, LASTNAME, DATEOFBIRTH, GENDER, STREET, LOCATION, CITY, STATE, PINCODE, MOBILENO, EMAILID) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, profileBean.getUserID());
            ps.setString(2, profileBean.getFirstName());
            ps.setString(3, profileBean.getLastName());
            ps.setDate(4, profileBean.getDateOfBirth()); // FIX: Use Date directly
            ps.setString(5, profileBean.getGender());
            ps.setString(6, profileBean.getStreet());
            ps.setString(7, profileBean.getLocation());
            ps.setString(8, profileBean.getCity());
            ps.setString(9, profileBean.getState());
            ps.setString(10, profileBean.getPincode());
            ps.setString(11, profileBean.getMobileNo());
            ps.setString(12, profileBean.getEmailID());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                result = "SUCCESS";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    public int deleteProfile(ArrayList<String> userIDs) {
        int count = 0;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ATA_TBL_USER_PROFILE WHERE USERID=?";
            ps = con.prepareStatement(sql);

            for (String id : userIDs) {
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

    public boolean updateProfile(ProfileBean profileBean) {
        boolean success = false;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ATA_TBL_USER_PROFILE SET FIRSTNAME=?, LASTNAME=?, DATEOFBIRTH=?, GENDER=?, " +
                         "STREET=?, LOCATION=?, CITY=?, STATE=?, PINCODE=?, MOBILENO=?, EMAILID=? WHERE USERID=?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, profileBean.getFirstName());
            ps.setString(2, profileBean.getLastName());
            ps.setDate(3, profileBean.getDateOfBirth()); // FIX: Use Date directly
            ps.setString(4, profileBean.getGender());
            ps.setString(5, profileBean.getStreet());
            ps.setString(6, profileBean.getLocation());
            ps.setString(7, profileBean.getCity());
            ps.setString(8, profileBean.getState());
            ps.setString(9, profileBean.getPincode());
            ps.setString(10, profileBean.getMobileNo());
            ps.setString(11, profileBean.getEmailID());
            ps.setString(12, profileBean.getUserID());

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

    public ProfileBean findByID(String userID) {
        ProfileBean profile = null;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_USER_PROFILE WHERE USERID=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                profile = new ProfileBean();
                profile.setUserID(rs.getString("USERID"));
                profile.setFirstName(rs.getString("FIRSTNAME"));
                profile.setLastName(rs.getString("LASTNAME"));
                profile.setDateOfBirth(rs.getDate("DATEOFBIRTH"));
                profile.setGender(rs.getString("GENDER"));
                profile.setStreet(rs.getString("STREET"));
                profile.setLocation(rs.getString("LOCATION"));
                profile.setCity(rs.getString("CITY"));
                profile.setState(rs.getString("STATE"));
                profile.setPincode(rs.getString("PINCODE"));
                profile.setMobileNo(rs.getString("MOBILENO"));
                profile.setEmailID(rs.getString("EMAILID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return profile;
    }

    public ArrayList<ProfileBean> findAll() {
        ArrayList<ProfileBean> profileList = new ArrayList<>();
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_USER_PROFILE";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                ProfileBean profile = new ProfileBean();
                profile.setUserID(rs.getString("USERID"));
                profile.setFirstName(rs.getString("FIRSTNAME"));
                profile.setLastName(rs.getString("LASTNAME"));
                profile.setDateOfBirth(rs.getDate("DATEOFBIRTH"));
                profile.setGender(rs.getString("GENDER"));
                profile.setStreet(rs.getString("STREET"));
                profile.setLocation(rs.getString("LOCATION"));
                profile.setCity(rs.getString("CITY"));
                profile.setState(rs.getString("STATE"));
                profile.setPincode(rs.getString("PINCODE"));
                profile.setMobileNo(rs.getString("MOBILENO"));
                profile.setEmailID(rs.getString("EMAILID"));
                
                profileList.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return profileList;
    }
}