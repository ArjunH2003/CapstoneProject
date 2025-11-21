package com.ust.ata.service;

import com.ust.ata.bean.ReservationBean;
import com.ust.ata.bean.RouteBean;
import com.ust.ata.bean.VehicleBean;

import java.util.ArrayList;

public interface Customer {

    /**
     * View vehicles filtered by type (e.g., "AC", "NON AC").
     * Reference: DD Page 12
     */
    ArrayList<VehicleBean> viewVehiclesByType(String vehicleType);

    /**
     * View vehicles filtered by seating capacity.
     * Reference: DD Page 12
     */
    ArrayList<VehicleBean> viewVehicleBySeats(int noOfSeats);

    /**
     * View all available routes.
     * Reference: DD Page 12
     */
    ArrayList<RouteBean> viewAllRoutes();

    /**
     * Book a vehicle. Returns the Reservation ID or "FAIL".
     * Reference: DD Page 12
     */
    String bookVehicle(ReservationBean reservationBean);

    /**
     * Cancel an existing booking.
     * Reference: DD Page 12
     */
    boolean cancelBooking(String userID, String reservationID);

    /**
     * View details of a specific booking.
     * Reference: DD Page 12
     */
    ReservationBean viewBookingDetails(String reservationID);

    /**
     * Print (fetch) booking details for ticket generation.
     * Reference: DD Page 12
     */
    ReservationBean printBookingDetails(String reservationID);
}