package com.ust.ata.util;

import com.ust.ata.bean.CredentialsBean;
import com.ust.ata.bean.ProfileBean;

public interface User {

    /**
     * Handles the login process.
     * Return value must be either: "A", "C", "FAIL", "INVALID"
     * A->Admin, C->Customer
     * Wrong username/password should return INVALID.
     * Reference: DD Page 13
     */
    String login(CredentialsBean credentialsBean);

    /**
     * Handles the logout process.
     * Reference: DD Page 13
     */
    boolean logout(String userID);

    /**
     * Changes the user password.
     * Return value must be either: "SUCCESS", "FAIL", "INVALID"
     * Reference: DD Page 13
     */
    String changePassword(CredentialsBean credentialsBean, String newPassword);

    /**
     * Registers a new user.
     * Return value must be either: <userid of length 6>, "FAIL"
     * Note: userid -> first 2 letter of first name followed by 4 digit auto generated number
     * Reference: DD Page 13
     */
    String register(ProfileBean profileBean);
}