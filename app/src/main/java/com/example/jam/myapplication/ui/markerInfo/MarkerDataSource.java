package com.example.jam.myapplication.ui.markerInfo;

import com.example.jam.myapplication.data.Result;

import java.io.IOException;

public class MarkerDataSource {
    public Result<MarkerData> getStatus(String forType,
                                        String type,
                                        String quantity,
                                        String unit,
                                        String user,
                                        String number,
                                        String email
    ){
        try {
            MarkerData markerData = new MarkerData(
                    forType, type, quantity, unit, user, number, email);
            return new Result.Success<>(markerData);
        }catch (Exception e) {
            return new Result.Error(new IOException("Error ", e));
        }
    }


}
