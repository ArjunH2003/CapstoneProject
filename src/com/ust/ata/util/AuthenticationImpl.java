package com.ust.ata.util;

import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.dao.CredentialsDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationImpl implements Authentication {

    private CredentialsDAO credentialsDAO = new CredentialsDAO();

    @Override
    public boolean authenticate(CredentialsBean credentialsBean) {
        // The DAO returns "A", "C", "INVALID", or "FAIL".
        // The Interface requires a boolean (True if valid user, False otherwise).
        String result = credentialsDAO.authenticate(credentialsBean);
        
        if (result.equals("A") || result.equals("C")) {
            return true;
        }
        return false;
    }

    @Override
    public String authorize(String userID) {
        // Logic to fetch UserType based solely on UserID
        String userType = null;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT USERTYPE FROM ATA_TBL_CREDENTIALS WHERE USERID=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, userID);
            rs = ps.executeQuery();

            if (rs.next()) {
                userType = rs.getString("USERTYPE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return userType;
    }

    @Override
    public boolean changeLoginStatus(CredentialsBean credentialsBean, int loginStatus) {
        // Delegates to the DAO which handles the update logic
        return credentialsDAO.changeLoginStatus(credentialsBean, loginStatus);
    }
}