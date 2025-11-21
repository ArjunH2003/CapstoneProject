package com.ust.ata.dao;

import com.ust.ata.bean.ReservationBean;
import com.ust.ata.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ReservationDAO {

    /**
     * Generates a unique Reservation ID.
     * Format: "RB" (Reservation Booking) + 4 digit auto-incremented number.
     * Fits strictly within VARCHAR(6).
     */
    private String generateReservationID() {
        String prefix = "RB";
        String newID = prefix + "1000"; // Default start

        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT RESERVATIONID FROM ATA_TBL_RESERVATION WHERE RESERVATIONID LIKE ? ORDER BY RESERVATIONID DESC LIMIT 1";
            ps = con.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("RESERVATIONID");
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
     * Book a vehicle (Create Reservation).
     * Reference: DD Customer Interface - String bookVehicle(ReservationBean)
     */
    public String bookVehicle(ReservationBean reservationBean) {
        String result = "FAIL";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String generatedID = generateReservationID();

            String sql = "INSERT INTO ATA_TBL_RESERVATION " +
                         "(RESERVATIONID, USERID, VEHICLEID, ROUTEID, BOOKINGDATE, JOURNEYDATE, DRIVERID, BOOKINGSTATUS, TOTALFARE, BOARDINGPOINT, DROPPOINT) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, generatedID);
            ps.setString(2, reservationBean.getUserID());
            ps.setString(3, reservationBean.getVehicleID());
            ps.setString(4, reservationBean.getRouteID());
            ps.setDate(5, new java.sql.Date(reservationBean.getBookingDate().getTime()));
            ps.setDate(6, new java.sql.Date(reservationBean.getJourneyDate().getTime()));
            ps.setString(7, null); // Driver is allotted later by Admin
            ps.setString(8, "BOOKED"); // Default status
            ps.setDouble(9, reservationBean.getTotalFare());
            ps.setString(10, reservationBean.getBoardingPoint());
            ps.setString(11, reservationBean.getDropPoint());

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
     * Cancel a booking.
     * Reference: DD Customer Interface - boolean cancelBooking(String userID, String reservationID)
     */
    public boolean cancelBooking(String userID, String reservationID) {
        boolean success = false;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            // Only allow cancellation if the User ID matches (Security check)
            String sql = "UPDATE ATA_TBL_RESERVATION SET BOOKINGSTATUS='CANCELLED' WHERE RESERVATIONID=? AND USERID=?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, reservationID);
            ps.setString(2, userID);

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
     * View details of a specific booking.
     * Reference: DD Customer Interface - ReservationBean viewBookingDetails(String reservationID)
     */
    public ReservationBean findByID(String reservationID) {
        ReservationBean rb = null;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_RESERVATION WHERE RESERVATIONID=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, reservationID);
            rs = ps.executeQuery();

            if (rs.next()) {
                rb = new ReservationBean();
                rb.setReservationID(rs.getString("RESERVATIONID"));
                rb.setUserID(rs.getString("USERID"));
                rb.setVehicleID(rs.getString("VEHICLEID"));
                rb.setRouteID(rs.getString("ROUTEID"));
                rb.setBookingDate(rs.getDate("BOOKINGDATE"));
                rb.setJourneyDate(rs.getDate("JOURNEYDATE"));
                rb.setDriverID(rs.getString("DRIVERID"));
                rb.setBookingStatus(rs.getString("BOOKINGSTATUS"));
                rb.setTotalFare(rs.getDouble("TOTALFARE"));
                rb.setBoardingPoint(rs.getString("BOARDINGPOINT"));
                rb.setDropPoint(rs.getString("DROPPOINT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return rb;
    }

    /**
     * Allot a driver to a specific reservation.
     * Reference: DD Administrator Interface - boolean allotDriver(String reservationID, String driverID)
     */
    public boolean allotDriver(String reservationID, String driverID) {
        boolean success = false;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ATA_TBL_RESERVATION SET DRIVERID=? WHERE RESERVATIONID=?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, driverID);
            ps.setString(2, reservationID);

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
     * Admin functionality to view bookings filtered by Journey Date and Route.
     * Reference: DD Administrator Interface
     */
    public ArrayList<ReservationBean> viewBookingDetails(Date journeyDate, String source, String destination) {
        ArrayList<ReservationBean> list = new ArrayList<>();
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Join logic might be needed if filtering strictly by Source/Dest names, 
            // but strictly following DAO structure, we assume logic handles IDs or join query.
            // Here we use a JOIN to filter by Route details based on the inputs.
            String sql = "SELECT R.* FROM ATA_TBL_RESERVATION R " +
                         "JOIN ATA_TBL_ROUTE RT ON R.ROUTEID = RT.ROUTEID " +
                         "WHERE R.JOURNEYDATE=? AND RT.SOURCE=? AND RT.DESTINATION=?";
            
            ps = con.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(journeyDate.getTime()));
            ps.setString(2, source);
            ps.setString(3, destination);
            
            rs = ps.executeQuery();

            while (rs.next()) {
                ReservationBean rb = new ReservationBean();
                rb.setReservationID(rs.getString("RESERVATIONID"));
                rb.setUserID(rs.getString("USERID"));
                rb.setVehicleID(rs.getString("VEHICLEID"));
                rb.setRouteID(rs.getString("ROUTEID"));
                rb.setBookingDate(rs.getDate("BOOKINGDATE"));
                rb.setJourneyDate(rs.getDate("JOURNEYDATE"));
                rb.setDriverID(rs.getString("DRIVERID"));
                rb.setBookingStatus(rs.getString("BOOKINGSTATUS"));
                rb.setTotalFare(rs.getDouble("TOTALFARE"));
                rb.setBoardingPoint(rs.getString("BOARDINGPOINT"));
                rb.setDropPoint(rs.getString("DROPPOINT"));
                list.add(rb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return list;
    }
    
    /**
     * Helper method to get all reservations (useful for generic Admin View)
     */
    public ArrayList<ReservationBean> findAll() {
        ArrayList<ReservationBean> list = new ArrayList<>();
        Connection con = DBUtil.getDBConnection("mysql");
        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ATA_TBL_RESERVATION");
            while (rs.next()) {
                ReservationBean rb = new ReservationBean();
                rb.setReservationID(rs.getString("RESERVATIONID"));
                rb.setUserID(rs.getString("USERID"));
                rb.setVehicleID(rs.getString("VEHICLEID"));
                rb.setRouteID(rs.getString("ROUTEID"));
                rb.setBookingDate(rs.getDate("BOOKINGDATE"));
                rb.setJourneyDate(rs.getDate("JOURNEYDATE"));
                rb.setDriverID(rs.getString("DRIVERID"));
                rb.setBookingStatus(rs.getString("BOOKINGSTATUS"));
                rb.setTotalFare(rs.getDouble("TOTALFARE"));
                rb.setBoardingPoint(rs.getString("BOARDINGPOINT"));
                rb.setDropPoint(rs.getString("DROPPOINT"));
                list.add(rb);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}