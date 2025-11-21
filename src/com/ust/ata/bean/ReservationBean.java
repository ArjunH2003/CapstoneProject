package com.ust.ata.bean;

import java.sql.Date;

public class ReservationBean {
    private String reservationID, userID, vehicleID, routeID, driverID, bookingStatus, boardingPoint, dropPoint;
    private Date bookingDate, journeyDate;
    private double totalFare;
    // Getters Setters
    public String getReservationID() { return reservationID; } public void setReservationID(String reservationID) { this.reservationID = reservationID; }
    public String getUserID() { return userID; } public void setUserID(String userID) { this.userID = userID; }
    public String getVehicleID() { return vehicleID; } public void setVehicleID(String vehicleID) { this.vehicleID = vehicleID; }
    public String getRouteID() { return routeID; } public void setRouteID(String routeID) { this.routeID = routeID; }
    public String getDriverID() { return driverID; } public void setDriverID(String driverID) { this.driverID = driverID; }
    public String getBookingStatus() { return bookingStatus; } public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public String getBoardingPoint() { return boardingPoint; } public void setBoardingPoint(String boardingPoint) { this.boardingPoint = boardingPoint; }
    public String getDropPoint() { return dropPoint; } public void setDropPoint(String dropPoint) { this.dropPoint = dropPoint; }
    public Date getBookingDate() { return bookingDate; } public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    public Date getJourneyDate() { return journeyDate; } public void setJourneyDate(Date journeyDate) { this.journeyDate = journeyDate; }
    public double getTotalFare() { return totalFare; } public void setTotalFare(double totalFare) { this.totalFare = totalFare; }
}