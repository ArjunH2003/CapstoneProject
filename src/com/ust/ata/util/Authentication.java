package com.ust.ata.util;

import com.ust.ata.bean.CredentialsBean;

public interface Authentication {
    
    /**
     * Performs the Authentication process.
     * Reference: DD Page 12 - boolean authenticate (Credentials Bean credentialsBean)
     */
    boolean authenticate(CredentialsBean credentialsBean);

    /**
     * Performs the Authorization process (Returning User Role).
     * Reference: DD Page 12 - String authorize(String userID)
     */
    String authorize(String userID);

    /**
     * Changes the login status of the user (1 for Logged In, 0 for Logged Out).
     * Reference: DD Page 13 - boolean changeLoginStatus (Credentials Bean credentialsBean, int loginStatus)
     */
    boolean changeLoginStatus(CredentialsBean credentialsBean, int loginStatus);
}