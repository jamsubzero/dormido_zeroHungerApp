package com.example.jam.myapplication.ui.markerInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jam.myapplication.MySingleton;
import com.example.jam.myapplication.data.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarkerViewModel extends ViewModel {

    private MutableLiveData<MarkerInfoResult> markerInfoResult = new MutableLiveData<>();

    private MarkerDataSource markerDataSource;

    private MarkerRepository markerRepository;

    public MarkerViewModel(MarkerRepository markerRepository) {
        this.markerRepository = markerRepository;
    }

    public LiveData<MarkerInfoResult> getMarkerInfoResult(){
        return markerInfoResult;
    }

    public void getMarkerData(int id, String url, Context context){

        JSONObject params = new JSONObject();

        try{
            params.put("id", id);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                int status = 0;
                String message = "";

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        status = response.getInt("status");
                        message = response.getString("message");

                        if (status == 200) {

                            JSONArray dataJson = response.getJSONArray("data");
                            JSONObject dataObj = dataJson.getJSONObject(0);

                            Integer needHave = dataObj.getInt("need_have");
                            String type = dataObj.getString("type");
                            int quan = dataObj.getInt("quan");
                            String unit = dataObj.getString("unit");
                            String user = dataObj.getString("name");
                            String mobile = dataObj.getString("mobile");
                            String email = dataObj.getString("email");

//                            MarkerData markerData = new MarkerData(
//                                    "For Sale",
//                                    "Hotdog",
//                                    "30",
//                                    "kg",
//                                    "uno",
//                                    "09054790111",
//                                    ""
//                            );

                            String forType = "";

                            if(needHave == 1 ){
                                forType = "For Sale";
                            }else{
                                forType = "Looking for";
                            }

                            Result<MarkerData> result = markerRepository.getStatus(
                                    forType,
                                    type,
                                    String.valueOf(quan),
                                    unit,
                                    user,
                                    mobile,
                                    email
                            );

                            if (result instanceof Result.Success) {
                                MarkerData data = ((Result.Success<MarkerData>) result).getData();
                                markerInfoResult.setValue(new MarkerInfoResult(new MarkerInfoView(

                                        data.getForType(),
                                        data.getType(),
                                        data.getQuantity(),
                                        data.getUnit(),
                                        data.getUser(),
                                        data.getNumber(),
                                        data.getEmail()

                                )));
                            }
                        }else {
                            Toast.makeText( context , "Server Error", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText( context, "Cannot connect to server", Toast.LENGTH_LONG).show();
                }
            }
            );

            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
