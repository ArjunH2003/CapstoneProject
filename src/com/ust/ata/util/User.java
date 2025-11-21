package com.ust.ata.util;

import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.bean.ProfileBean;

public interface User {
    String login(CredentialsBean credentialsBean);
    boolean logout(String userID);
    String changePassword(CredentialsBean credentialsBean, String newPassword);
    
    // [UPDATED] Now accepts password
    String register(ProfileBean profileBean, String password);
}