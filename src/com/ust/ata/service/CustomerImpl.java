package com.ust.ata.service;

import com.ust.ata.bean.*;
import com.ust.ata.dao.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.util.ArrayList;

public class CustomerImpl implements Customer {

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private RouteDAO routeDAO = new RouteDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    public ArrayList<VehicleBean> viewVehiclesByType(String vehicleType) {
        ArrayList<VehicleBean> allVehicles = vehicleDAO.findAll();
        ArrayList<VehicleBean> filteredList = new ArrayList<>();
        
        if (vehicleType != null) {
            for (VehicleBean vehicle : allVehicles) {
                if (vehicleType.equalsIgnoreCase(vehicle.getType())) {
                    filteredList.add(vehicle);
                }
            }
        }
        return filteredList;
    }

    @Override
    public ArrayList<VehicleBean> viewVehicleBySeats(int noOfSeats) {
        ArrayList<VehicleBean> allVehicles = vehicleDAO.findAll();
        ArrayList<VehicleBean> filteredList = new ArrayList<>();
        
        for (VehicleBean vehicle : allVehicles) {
            // Return vehicles that have at least the requested capacity
            if (vehicle.getSeatingCapacity() >= noOfSeats) {
                filteredList.add(vehicle);
            }
        }
        return filteredList;
    }

    @Override
    public ArrayList<RouteBean> viewAllRoutes() {
        return routeDAO.findAll();
    }

    @Override
    public String bookVehicle(ReservationBean reservationBean) {
        return reservationDAO.bookVehicle(reservationBean);
    }

    @Override
    public boolean cancelBooking(String userID, String reservationID) {
        return reservationDAO.cancelBooking(userID, reservationID);
    }

    @Override
    public ReservationBean viewBookingDetails(String reservationID) {
        return reservationDAO.findByID(reservationID);
    }

    @Override
    public ReservationBean printBookingDetails(String reservationID) {
        // In a real application, this might trigger a PDF generation or specific formatting.
        // Based on the bean return type in DD, it fetches the booking data.
        return reservationDAO.findByID(reservationID);
    }
}