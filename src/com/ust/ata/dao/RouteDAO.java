package com.ust.ata.dao;

import com.ust.ata.bean.RouteBean;
import com.ust.ata.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RouteDAO {

    /**
     * Generates a unique RouteID.
     * Logic: First 2 letters of Source + First 2 letters of Destination + 4 digit number.
     * Example: Source="Delhi", Destination="Agra" -> Prefix="DEAG" -> ID="DEAG1000"
     * Reference: DD Appendix 
     */
    private String generateRouteID(String source, String destination) {
        // Ensure source/dest have at least 2 chars (Fallback to "XX" if not)
        String srcPrefix = (source != null && source.length() >= 2) ? source.substring(0, 2) : "XX";
        String destPrefix = (destination != null && destination.length() >= 2) ? destination.substring(0, 2) : "XX";
        
        String prefix = (srcPrefix + destPrefix).toUpperCase();
        String newID = prefix + "1000"; // Default start ID

        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Find the highest ID currently existing with this specific Source-Dest prefix
            String sql = "SELECT ROUTEID FROM ATA_TBL_ROUTE WHERE ROUTEID LIKE ? ORDER BY ROUTEID DESC LIMIT 1";
            ps = con.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            rs = ps.executeQuery();

            if (rs.next()) {
                String lastID = rs.getString("ROUTEID");
                // Extract the numeric part (index 4 onwards because prefix is 4 chars)
                int num = Integer.parseInt(lastID.substring(4));
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
     * Creates a new Route.
     * Reference: DD DAO Method Summary - String createXYZ(BeanObject) [cite: 501]
     */
    public String createRoute(RouteBean routeBean) {
        String result = "FAIL";
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String generatedID = generateRouteID(routeBean.getSource(), routeBean.getDestination());

            String sql = "INSERT INTO ATA_TBL_ROUTE (ROUTEID, SOURCE, DESTINATION, DISTANCE, TRAVELDURATION) " +
                         "VALUES (?, ?, ?, ?, ?)";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, generatedID);
            ps.setString(2, routeBean.getSource());
            ps.setString(3, routeBean.getDestination());
            ps.setInt(4, routeBean.getDistance());
            ps.setInt(5, routeBean.getTravelDuration());

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
     * Deletes routes based on a list of Route IDs.
     * Reference: DD DAO Method Summary - int deleteXYZ(ArrayList<String>) [cite: 501]
     */
    public int deleteRoute(ArrayList<String> routeIDs) {
        int count = 0;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM ATA_TBL_ROUTE WHERE ROUTEID=?";
            ps = con.prepareStatement(sql);

            for (String id : routeIDs) {
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

    /**
     * Modifies an existing route's details.
     * Reference: DD DAO Method Summary - boolean updateXYZ(BeanObject) [cite: 501]
     * Matches Service Interface: boolean modifyRoute(RouteBean) [cite: 497]
     */
    public boolean modifyRoute(RouteBean routeBean) {
        boolean success = false;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE ATA_TBL_ROUTE SET SOURCE=?, DESTINATION=?, DISTANCE=?, TRAVELDURATION=? " +
                         "WHERE ROUTEID=?";
            
            ps = con.prepareStatement(sql);
            ps.setString(1, routeBean.getSource());
            ps.setString(2, routeBean.getDestination());
            ps.setInt(3, routeBean.getDistance());
            ps.setInt(4, routeBean.getTravelDuration());
            ps.setString(5, routeBean.getRouteID());

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
     * Retrieves all routes.
     * Reference: DD DAO Method Summary - ArrayList<BeanObject> findAll() [cite: 501]
     */
    public ArrayList<RouteBean> findAll() {
        ArrayList<RouteBean> routeList = new ArrayList<>();
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_ROUTE";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                RouteBean route = new RouteBean();
                route.setRouteID(rs.getString("ROUTEID"));
                route.setSource(rs.getString("SOURCE"));
                route.setDestination(rs.getString("DESTINATION"));
                route.setDistance(rs.getInt("DISTANCE"));
                route.setTravelDuration(rs.getInt("TRAVELDURATION"));
                
                routeList.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return routeList;
    }

    /**
     * Finds a specific route by ID.
     * Reference: DD DAO Method Summary - BeanObject findByID(String) [cite: 501]
     */
    public RouteBean findByID(String routeID) {
        RouteBean route = null;
        Connection con = DBUtil.getDBConnection("mysql");
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM ATA_TBL_ROUTE WHERE ROUTEID=?";
            ps = con.prepareStatement(sql);
            ps.setString(1, routeID);
            rs = ps.executeQuery();

            if (rs.next()) {
                route = new RouteBean();
                route.setRouteID(rs.getString("ROUTEID"));
                route.setSource(rs.getString("SOURCE"));
                route.setDestination(rs.getString("DESTINATION"));
                route.setDistance(rs.getInt("DISTANCE"));
                route.setTravelDuration(rs.getInt("TRAVELDURATION"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return route;
    }
}