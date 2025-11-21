package com.ust.ata.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Payment {

    // Attributes as per DD 
    private String creditCardNumber;
    private String validFrom;
    private String validTo;
    private double balance;

    // Default Constructor
    public Payment() {}

    // Parameterized Constructor
    public Payment(String creditCardNumber, String validFrom, String validTo, double balance) {
        this.creditCardNumber = creditCardNumber;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.balance = balance;
    }

    // Getters and Setters
    public String getCreditCardNumber() { return creditCardNumber; }
    public void setCreditCardNumber(String creditCardNumber) { this.creditCardNumber = creditCardNumber; }

    public String getValidFrom() { return validFrom; }
    public void setValidFrom(String validFrom) { this.validFrom = validFrom; }

    public String getValidTo() { return validTo; }
    public void setValidTo(String validTo) { this.validTo = validTo; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    /**
     * Checks if a credit card exists for a specific user.
     * Reference: DD Page 13 - boolean findByCardNumber (String userID, String cardNumber)
     */
    public boolean findByCardNumber(String userID, String cardNumber) {
        boolean exists = false;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Check against ATA_TBL_CREDITCARD defined in Appendix [cite: 769]
            String sql = "SELECT CREDITCARDNUMBER FROM ATA_TBL_CREDITCARD WHERE USERID=? AND CREDITCARDNUMBER=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, userID);
            ps.setString(2, cardNumber);
            rs = ps.executeQuery();

            if (rs.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return exists;
    }

    /**
     * Processes the payment.
     * Reference: DD Page 13 - String process (Payment payment)
     * Logic: Validates the card details and checks/deducts balance.
     */
    public String process(Payment payment) {
        String result = "FAIL";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1. Verify Card Validity and Balance
            String sqlCheck = "SELECT CREDITBALANCE FROM ATA_TBL_CREDITCARD WHERE CREDITCARDNUMBER=? AND VALIDFROM=? AND VALIDTO=?";
            ps = con.prepareStatement(sqlCheck);
            ps.setString(1, payment.getCreditCardNumber());
            ps.setString(2, payment.getValidFrom());
            ps.setString(3, payment.getValidTo());
            rs = ps.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("CREDITBALANCE");
                
                // Check if sufficient balance exists
                if (currentBalance >= payment.getBalance()) {
                    // 2. Deduct Balance (Simulating a transaction)
                    // Note: In a real scenario, we would update the table.
                    // The DD only asks for a "process" method returning String.
                    
                    String sqlUpdate = "UPDATE ATA_TBL_CREDITCARD SET CREDITBALANCE=? WHERE CREDITCARDNUMBER=?";
                    PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                    psUpdate.setDouble(1, currentBalance - payment.getBalance());
                    psUpdate.setString(2, payment.getCreditCardNumber());
                    
                    int rows = psUpdate.executeUpdate();
                    if (rows > 0) {
                        result = "SUCCESS";
                    }
                } else {
                    result = "INSUFFICIENT_FUNDS"; // Custom message for clarity
                }
            } else {
                result = "INVALID_CARD"; // Card details didn't match
            }

        } catch (SQLException e) {
            e.printStackTrace();
            result = "FAIL";
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }
}