package com.example.recyclingapp.models;

public class DisposalPoint {
    private String pointId;
    private String name;
    private String openingHours;
    private String address;
    private Location location;

    public DisposalPoint() {}

    public String getPointId() { return pointId; }
    public void setPointId(String pointId) { this.pointId = pointId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
}
