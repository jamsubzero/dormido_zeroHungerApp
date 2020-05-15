package com.example.jam.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jam.myapplication.CustomAdapters.CustomMealsAdapter;
import com.example.jam.myapplication.Pojos.NeedEntry;
import com.example.jam.myapplication.Pojos.NeedReport;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ForecastFragment extends Fragment {



    String searchUrl = "http://zerop.ml/agri/query.php";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String NEED_REPORT = "need_report";
    public static final boolean IS_NEED = true;


    ListView listView;

    ArrayList<NeedEntry> mealList = new ArrayList<NeedEntry>();
    ArrayList<NeedReport> reportList = new ArrayList<NeedReport>();

    CustomMealsAdapter dataAdapter = null;


    public ForecastFragment() {}


    public static ForecastFragment newInstance() {
        ForecastFragment fragment = new ForecastFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) getView().findViewById(R.id.mealList);


        String year = "2020";
        String crop = "Rice";


        new AsyncLogin().execute("0", year, "-1", crop, "-1"); // 0 for need, -1 for skip argument

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_forecast, container, false);

    }


    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(ForecastFragment.this.getContext());
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tFetching data...please wait.");
            pdLoading.setCancelable(false);
            pdLoading.show();


        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(searchUrl+  "?"+
                        "needhave="+params[0]+
                        "&year="+params[1]+
                        "&month="+params[2]+
                        "&type="+params[3]+
                        "&item="+params[4]+
                        "");
                // url = new URL(searchUrl);
                // http://localhost/zeroHungerServer/query.php?type=Grain&item=Rice

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //  Append parameters to URL

//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("type", "Grain")
//                        .appendQueryParameter("item", "Rice");
//                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                // writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            Log.i("JSON", result);
            pdLoading.dismiss();
            LatLng latLng = null;

            reportList = new ArrayList<>();
            try {
                if(!result.equals("-1")){

                    JSONArray jsonArray  = new JSONArray(result);
                    for(int index = 0; index < jsonArray.length() ; index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        Log.i("JSON 1st ITEM", jsonObject.toString());

                        String quan = jsonObject.getString("quan");
                        String type = jsonObject.getString("type");
                        String unit = jsonObject.getString("unit");

                        String city = jsonObject.getString("city");
                        String province = jsonObject.getString("province");
                        String year = jsonObject.getString("year");
                        String month = jsonObject.getString("month");


                        NeedEntry meal = new NeedEntry(type + "(" + quan + " " + unit + ")",
                                city + ", " + province + ", for: " + month + ", " + year, false);

                        NeedReport needReport = new NeedReport(type, monthStringToInt(month),
                                Integer.parseInt(year), Integer.parseInt(quan));
                        reportList.add(needReport);

                    }

                }else{
                    Toast.makeText(getActivity(), "No record found", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

            //===========================================

            //group data into months

            ArrayList<NeedReport> needReportList = reportList;


            List<NeedReport> allCrops = needReportList.stream()
                    .filter(distinctByKey(p -> p.getItemName()))
                    .collect(Collectors.toList());


            HashMap<String, HashMap<Integer, Integer>> allRec = createMonthQuanMap(allCrops) ;

            for(NeedReport report: needReportList){

                HashMap<Integer, Integer> pair = allRec.get(report.getItemName());

                int curSum = pair.get(report.getMonth());
                pair.replace(report.getMonth(), curSum + report.getQuan());


            }

            HashMap<String, HashMap<Integer, Double>> forecastedAll = createForecastedMonthQuanMap(allCrops) ;

            for(NeedReport report: allCrops) {

                final float CONST_FACTOR = 0.3f;


                HashMap<Integer, Integer> actualDataPair = allRec.get(report.getItemName());
                HashMap<Integer, Double> forecastedDataPair = forecastedAll.get(report.getItemName());

                Log.e("crop:", report.getItemName());

                for (int c = 0; c <= 11; c++) {
                    Log.e("month "+c+":", actualDataPair.get(c) + "");

                }

                Double actualAvg = getAverageFromActual(new ArrayList<>(actualDataPair.values()));
                Log.e("actualAvg:{}" , actualAvg.toString());


                forecastedDataPair.put(0, actualAvg);
                for (int counter = 0 ; counter < 12; counter++){

                    Double curAct = new Double(actualDataPair.get(counter));
                    Double curFor = forecastedDataPair.get(counter);

                    Double diff = curAct - curFor;

                    Double nextForMonth = (diff * CONST_FACTOR) + curFor;

                    forecastedDataPair.put(counter+1, nextForMonth);

                }

                Log.e("ForeCast", "ForCast:");
                for (int c = 0; c <= 11; c++) {
                    Log.e("ForCast month "+c+":", forecastedDataPair.get(c) + "");

                }

            }


            //======================================
            //generate forcast



        }

    }

    private Double getAverageFromActual(List<Integer> valueList){
        return valueList.stream().mapToInt(val -> val).average().orElse(0.0);

    }


    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private HashMap<String, HashMap<Integer, Integer>> createMonthQuanMap(List<NeedReport> uniqueCrops){
        HashMap<String, HashMap<Integer, Integer>> all = new HashMap<>();
        for(NeedReport crop: uniqueCrops) {
            HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();

            /* Key=month, Val=quantity */
            hmap.put(0, 0);
            hmap.put(1, 0);
            hmap.put(2, 0);
            hmap.put(3, 0);
            hmap.put(4, 0);
            hmap.put(5, 0);
            hmap.put(6, 0);
            hmap.put(7, 0);
            hmap.put(8, 0);
            hmap.put(9, 0);
            hmap.put(10, 0);
            hmap.put(11, 0);

            all.put(crop.getItemName(), hmap);
        }

        return all;
    }


    private HashMap<String, HashMap<Integer, Double>> createForecastedMonthQuanMap(List<NeedReport> uniqueCrops){
        HashMap<String, HashMap<Integer, Double>> all = new HashMap<>();
        for(NeedReport crop: uniqueCrops) {
            HashMap<Integer, Double> hmap = new HashMap<>();

            /* Key=month, Val=quantity */
            hmap.put(0, 0.0d);
            hmap.put(1, 0.0d);
            hmap.put(2, 0.0d);
            hmap.put(3, 0.0d);
            hmap.put(4, 0.0d);
            hmap.put(5, 0.0d);
            hmap.put(6, 0.0d);
            hmap.put(7, 0.0d);
            hmap.put(8, 0.0d);
            hmap.put(9, 0.0d);
            hmap.put(10, 0.0d);
            hmap.put(11, 0.0d);

            all.put(crop.getItemName(), hmap);
        }

        return all;
    }




    public static int monthStringToInt(String month){
        if (month.equalsIgnoreCase("January")){
            return 0;
        }else if (month.equalsIgnoreCase("February")){
            return 1;
        }else if (month.equalsIgnoreCase("March")){
            return 2;
        }else if (month.equalsIgnoreCase("April")){
            return 3;
        }else if (month.equalsIgnoreCase("May")){
            return 4;
        }else if (month.equalsIgnoreCase("June")){
            return 5;
        }else if (month.equalsIgnoreCase("July")){
            return 6;
        }else if (month.equalsIgnoreCase("August")){
            return 7;
        }else if (month.equalsIgnoreCase("September")){
            return 8;
        }else if (month.equalsIgnoreCase("October")){
            return 9;
        }else if (month.equalsIgnoreCase("November")){
            return 10;
        }else if (month.equalsIgnoreCase("December")){
            return 11;
        }

        return -1;
    }







}
