package com.ust.ata.util;


import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.bean.ProfileBean;
import com.ust.ata.dao.CredentialsDAO;

public class UserImpl implements User {

    private CredentialsDAO credentialsDAO = new CredentialsDAO();

    @Override
    public String login(CredentialsBean credentialsBean) {
        // The DAO authenticate method already returns "A", "C", "INVALID", or "FAIL"
        // strictly matching the DD requirements.
        String result = credentialsDAO.authenticate(credentialsBean);
        
        // If login is successful (A or C), update the login status to 1 (Logged In)
        if (result.equals("A") || result.equals("C")) {
            credentialsDAO.changeLoginStatus(credentialsBean, 1);
        }
        
        return result;
    }

    @Override
    public boolean logout(String userID) {
        // Create a temporary bean to handle the status update
        CredentialsBean cb = new CredentialsBean();
        cb.setUserID(userID);
        
        // Update login status to 0 (Logged Out)
        return credentialsDAO.changeLoginStatus(cb, 0);
    }

    @Override
    public String changePassword(CredentialsBean credentialsBean, String newPassword) {
        // Delegates to DAO which returns "SUCCESS", "FAIL", or "INVALID"
        return credentialsDAO.changePassword(credentialsBean, newPassword);
    }

    @Override
    public String register(ProfileBean profileBean) {
        // The DAO handles the transaction and ID generation logic
        // Returns the generated ID (6 chars) or "FAIL"
        // Default password is set to "password" inside DAO or passed here. 
        // Per typical flow, we assume a default or the user provides it. 
        // Here we pass a default password "user123" as the DD interface for register 
        // in User.java only accepts ProfileBean, implying a default password policy 
        // or logic handled internally.
        return credentialsDAO.register(profileBean, "user123");
    }
}