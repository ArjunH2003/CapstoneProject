package com.ust.ata.bean;

public class VehicleBean {
    private String vehicleID, name, type, registrationNumber;
    private int seatingCapacity;
    private double farePerKM;
    private String routeID;
    
    
    public VehicleBean() {}
    public VehicleBean(String n, String t, String r, int s, double f) {
        name=n; type=t; registrationNumber=r; seatingCapacity=s; farePerKM=f;
    }
    public String getVehicleID() { return vehicleID; } public void setVehicleID(String v) { vehicleID = v; }
    public String getName() { return name; } public void setName(String n) { name = n; }
    public String getType() { return type; } public void setType(String t) { type = t; }
    public String getRegistrationNumber() { return registrationNumber; } public void setRegistrationNumber(String r) { registrationNumber = r; }
    public int getSeatingCapacity() { return seatingCapacity; } public void setSeatingCapacity(int s) { seatingCapacity = s; }
    public double getFarePerKM() { return farePerKM; } public void setFarePerKM(double f) { farePerKM = f; }
    public String getRouteID() { return routeID; }
    public void setRouteID(String routeID) { this.routeID = routeID; }


}