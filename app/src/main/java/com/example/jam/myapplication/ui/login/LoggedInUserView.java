package com.example.jam.myapplication.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayId;
    private String displayName;
    //... other data fields that may be accessible to the UI
    private String displayEmailNum;
    private Integer displayType;

    LoggedInUserView(String displayId,
                     String displayName,
                     String displayEmailNum,
                     Integer displayType) {
        this.displayId = displayId;
        this.displayName = displayName;
        this.displayEmailNum = displayEmailNum;
        this.displayType = displayType;
    }

    String getDisplayId(){return  displayId;}
    String getDisplayName() {
        return displayName;
    }
    String getDisplayEmailNum(){return displayEmailNum;}
    Integer getDisplayType(){return displayType;}
}
