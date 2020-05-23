package com.example.jam.myapplication.ui.markerInfo;

public class MarkerInfoView {
    private String displayForType;
    private String displayType;
    private String displayQuantity;
    private String displayUnit;
    private String displayUser;
    private String displayNumber;
    private String displayEmail;

    public MarkerInfoView(
            String displayForType,
            String displayType,
            String displayQuantity,
            String displayUnit,
            String displayUser,
            String displayNumber,
            String displayEmail
    ){
        this.displayForType = displayForType;
        this.displayType = displayType;
        this.displayQuantity = displayQuantity;
        this.displayUnit = displayUnit;
        this.displayUser = displayUser;
        this.displayNumber = displayNumber;
        this.displayEmail = displayEmail;
    }

    public String getDisplayForType(){return displayForType;}
    public String getDisplayType(){return displayType;}
    public String getDisplayQuantity(){return displayQuantity;}
    public String getDisplayUnit(){return displayUnit;}
    public String getDisplayUser(){return displayUser;}
    public String getDisplayNumber(){return displayNumber;}
    public String getDisplayEmail(){return displayEmail;}
}
