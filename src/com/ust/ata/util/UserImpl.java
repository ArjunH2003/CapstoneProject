package com.ust.ata.util;

import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.bean.ProfileBean;
import com.ust.ata.dao.CredentialsDAO;

public class UserImpl implements User {

    private CredentialsDAO credentialsDAO = new CredentialsDAO();

    @Override
    public String login(CredentialsBean credentialsBean) {
        String result = credentialsDAO.authenticate(credentialsBean);
        if (result.equals("A") || result.equals("C")) {
            credentialsDAO.changeLoginStatus(credentialsBean, 1);
        }
        return result;
    }

    @Override
    public boolean logout(String userID) {
        CredentialsBean cb = new CredentialsBean();
        cb.setUserID(userID);
        return credentialsDAO.changeLoginStatus(cb, 0);
    }

    @Override
    public String changePassword(CredentialsBean credentialsBean, String newPassword) {
        return credentialsDAO.changePassword(credentialsBean, newPassword);
    }

    @Override
    // [UPDATED] Now passes the user-defined password to the DAO
    public String register(ProfileBean profileBean, String password) {
        return credentialsDAO.register(profileBean, password);
    }
}