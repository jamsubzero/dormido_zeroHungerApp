package com.example.jam.myapplication.ui.markerInfo;

import com.example.jam.myapplication.data.Result;

public class MarkerRepository {

    private static volatile MarkerRepository instance;

    private MarkerDataSource dataSource;

    private MarkerData markerData = null;

    private MarkerRepository(MarkerDataSource markerDataSource){
        this.dataSource = markerDataSource;
    }

    public static MarkerRepository getInstance(MarkerDataSource dataSource) {
        if (instance == null) {
            instance = new MarkerRepository(dataSource);
        }
        return instance;
    }

    public void setMarkerData(MarkerData markerData){
        this.markerData = markerData;
    }

    public Result<MarkerData> getStatus(
            String forType,
            String type,
            String quantity,
            String unit,
            String user,
            String number,
            String email,
            double price) {
        // handle login
        Result<MarkerData> result = dataSource.getStatus(
                forType, type, quantity, unit, user, number, email, price
        );
        if (result instanceof Result.Success) {
            setMarkerData(((Result.Success<MarkerData>) result).getData());
        }
        return result;
    }
}
