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

    // ID Generation Logic
    private String generateRouteID(String source, String destination) {
        String srcPrefix = (source != null && source.length() >= 2) ? source.substring(0, 2) : "XX";
        String destPrefix = (destination != null && destination.length() >= 2) ? destination.substring(0, 2) : "XX";
        String prefix = (srcPrefix + destPrefix).toUpperCase();
        String newID = prefix + "1000"; 

        Connection con = DBUtil.getDBConnection("mysql");
        try {
            PreparedStatement ps = con.prepareStatement("SELECT ROUTEID FROM ATA_TBL_ROUTE WHERE ROUTEID LIKE ? ORDER BY ROUTEID DESC LIMIT 1");
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastID = rs.getString("ROUTEID");
                int num = Integer.parseInt(lastID.substring(4));
                newID = prefix + (num + 1);
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return newID;
    }

    public String createRoute(RouteBean routeBean) {
        String id = generateRouteID(routeBean.getSource(), routeBean.getDestination());
        try (Connection con = DBUtil.getDBConnection("mysql")) {
            String sql = "INSERT INTO ATA_TBL_ROUTE (ROUTEID, SOURCE, DESTINATION, DISTANCE, TRAVELDURATION) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, routeBean.getSource());
            ps.setString(3, routeBean.getDestination());
            ps.setInt(4, routeBean.getDistance());
            ps.setInt(5, routeBean.getTravelDuration());
            return ps.executeUpdate() > 0 ? id : "FAIL";
        } catch (Exception e) { return "FAIL"; }
    }

    // [FIXED] Now catches Integrity Constraint Violation
    public int deleteRoute(ArrayList<String> routeIDs) {
        int count = 0;
        Connection con = null;
        try {
            con = DBUtil.getDBConnection("mysql");
            String sql = "DELETE FROM ATA_TBL_ROUTE WHERE ROUTEID=?";
            PreparedStatement ps = con.prepareStatement(sql);

            for (String id : routeIDs) {
                ps.setString(1, id);
                count += ps.executeUpdate();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // [CRITICAL] This specific return value triggers the popup in AdminPanel
            return -1; 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
        return count;
    }

    public boolean modifyRoute(RouteBean routeBean) {
        try (Connection con = DBUtil.getDBConnection("mysql")) {
            String sql = "UPDATE ATA_TBL_ROUTE SET SOURCE=?, DESTINATION=?, DISTANCE=?, TRAVELDURATION=? WHERE ROUTEID=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, routeBean.getSource());
            ps.setString(2, routeBean.getDestination());
            ps.setInt(3, routeBean.getDistance());
            ps.setInt(4, routeBean.getTravelDuration());
            ps.setString(5, routeBean.getRouteID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public ArrayList<RouteBean> findAll() {
        ArrayList<RouteBean> list = new ArrayList<>();
        try (Connection con = DBUtil.getDBConnection("mysql")) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM ATA_TBL_ROUTE");
            while (rs.next()) {
                RouteBean rb = new RouteBean();
                rb.setRouteID(rs.getString("ROUTEID"));
                rb.setSource(rs.getString("SOURCE"));
                rb.setDestination(rs.getString("DESTINATION"));
                rb.setDistance(rs.getInt("DISTANCE"));
                rb.setTravelDuration(rs.getInt("TRAVELDURATION"));
                list.add(rb);
            }
        } catch (Exception e) {}
        return list;
    }
    
    public RouteBean findByID(String id) {
        try (Connection con = DBUtil.getDBConnection("mysql")) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ATA_TBL_ROUTE WHERE ROUTEID=?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RouteBean rb = new RouteBean();
                rb.setRouteID(rs.getString("ROUTEID"));
                rb.setSource(rs.getString("SOURCE"));
                rb.setDestination(rs.getString("DESTINATION"));
                rb.setDistance(rs.getInt("DISTANCE"));
                rb.setTravelDuration(rs.getInt("TRAVELDURATION"));
                return rb;
            }
        } catch (Exception e) {}
        return null;
    }
}