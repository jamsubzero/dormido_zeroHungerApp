package com.example.jam.myapplication.ui.markerInfo;

import android.support.annotation.Nullable;

public class MarkerInfoResult {
    @Nullable
    private MarkerInfoView success;
    @Nullable
    private Integer error;
    @Nullable
    private Integer noServer;

    MarkerInfoResult(
            @Nullable Integer error,
            @Nullable Integer noServer
    ){
        this.error = error;
        this.noServer = error;
    }

    public MarkerInfoResult(@Nullable MarkerInfoView success){this.success = success;}

    @Nullable
    public MarkerInfoView getSuccess(){return success;}

    @Nullable
    Integer getError(){return error;}

    @Nullable
    Integer getNoServer(){return noServer;}
}
