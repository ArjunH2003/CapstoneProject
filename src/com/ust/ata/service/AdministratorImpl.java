package com.ust.ata.service;


import com.ust.ata.bean.DriverBean;
import com.ust.ata.bean.ReservationBean;
import com.ust.ata.bean.RouteBean;
import com.ust.ata.bean.VehicleBean;
import com.ust.ata.dao.DriverDAO;
import com.ust.ata.dao.ReservationDAO;
import com.ust.ata.dao.RouteDAO;
import com.ust.ata.dao.VehicleDAO;

import java.util.ArrayList;
import java.util.Date;

public class AdministratorImpl implements Administrator {

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private DriverDAO driverDAO = new DriverDAO();
    private RouteDAO routeDAO = new RouteDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    // --- Vehicle ---
    public String addVehicle(VehicleBean vehicleBean) {
        return vehicleDAO.createVehicle(vehicleBean);
    }

    public int deleteVehicle(ArrayList<String> vehicleID) {
        return vehicleDAO.deleteVehicle(vehicleID);
    }

    public boolean modifyVehicle(VehicleBean vehicleBean) {
        return vehicleDAO.modifyVehicle(vehicleBean);
    }

    public VehicleBean viewVehicle(String vehicleID) {
        return vehicleDAO.findByID(vehicleID);
    }

    // [FIX] Implementation required for the UI table
    public ArrayList<VehicleBean> viewAllVehicles() {
        return vehicleDAO.findAll();
    }

    // --- Driver ---
    public String addDriver(DriverBean driverBean) {
        return driverDAO.createDriver(driverBean);
    }

    public int deleteDriver(ArrayList<String> driverID) {
        return driverDAO.deleteDriver(driverID);
    }

    public boolean modifyDriver(DriverBean driverBean) {
        return driverDAO.modifyDriver(driverBean);
    }

    public boolean allotDriver(String reservationID, String driverID) {
        return reservationDAO.allotDriver(reservationID, driverID);
    }

    // --- Route ---
    public String addRoute(RouteBean routeBean) {
        return routeDAO.createRoute(routeBean);
    }

    public int deleteRoute(ArrayList<String> routeID) {
        return routeDAO.deleteRoute(routeID);
    }

    public boolean modifyRoute(RouteBean routeBean) {
        return routeDAO.modifyRoute(routeBean);
    }

    public RouteBean viewRoute(String routeID) {
        return routeDAO.findByID(routeID);
    }

    // --- Booking ---
    public ArrayList<ReservationBean> viewBookingDetails(Date journeyDate, String source, String destination) {
        return reservationDAO.viewBookingDetails(journeyDate, source, destination);
    }
}