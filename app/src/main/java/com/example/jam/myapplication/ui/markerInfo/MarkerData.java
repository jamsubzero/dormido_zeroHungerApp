package com.example.jam.myapplication.ui.markerInfo;

public class MarkerData {

    private String forType;
    private String type;
    private String quantity;
    private String unit;
    private String user;
    private String number;
    private String email;

    public MarkerData(String forType, String type, String quantity, String unit, String user, String number, String email) {
        this.forType = forType;
        this.type = type;
        this.quantity = quantity;
        this.unit = unit;
        this.user = user;
        this.number = number;
        this.email = email;
    }

    public String getForType() {
        return forType;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getUser() {
        return user;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }
}
