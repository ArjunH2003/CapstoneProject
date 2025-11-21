package com.ust.ata.service;
import java.util.ArrayList;

import com.ust.ata.bean.DriverBean;
import com.ust.ata.bean.ReservationBean;
import com.ust.ata.bean.RouteBean;
import com.ust.ata.bean.VehicleBean;

import java.util.Date;

public interface Administrator {
    
    // Vehicle Management
    String addVehicle(VehicleBean vehicleBean);
    int deleteVehicle(ArrayList<String> vehicleID);
    boolean modifyVehicle(VehicleBean vehicleBean);
    
    // Fetches a single vehicle by ID
    VehicleBean viewVehicle(String vehicleID); 
    
    // [FIX] Fetches ALL vehicles (Required for AdminPanel Table)
    ArrayList<VehicleBean> viewAllVehicles(); 

    // Driver Management
    String addDriver(DriverBean driverBean);
    int deleteDriver(ArrayList<String> driverID);
    boolean modifyDriver(DriverBean driverBean);
    boolean allotDriver(String reservationID, String driverID);

    // Route Management
    String addRoute(RouteBean routeBean);
    int deleteRoute(ArrayList<String> routeID);
    boolean modifyRoute(RouteBean routeBean);
    RouteBean viewRoute(String routeID);
    
    // Booking Management
    ArrayList<ReservationBean> viewBookingDetails(Date journeyDate, String source, String destination);
}