package com.example.jam.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
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
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    Button forecastBtn;

    Button compareBtn;


    String type = "0";
    String crop = "Rice";
    String year = "2020";

    Spinner filTypeSpinner;
    Spinner filCropSpinner;
    Spinner filYearSpinner;

    TextView forecastDetails;

    public static final String FORECAST_REPORT = "forecast_report";


    private static DecimalFormat df = new DecimalFormat("#.##");

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
        listView = getView().findViewById(R.id.mealList);
        forecastBtn = getView().findViewById(R.id.forecastBtn);
        compareBtn = getView().findViewById(R.id.compareBtn);

        filTypeSpinner = getView().findViewById(R.id.filForecastType);
        filCropSpinner = getView().findViewById(R.id.filForecastCrop);
        filYearSpinner = getView().findViewById(R.id.filForecastYear);
        forecastDetails = getView().findViewById(R.id.foreCastDetails);


        forecastBtn.setOnClickListener(view1 -> {
            if(filCropSpinner.getSelectedItemPosition() !=0 && filYearSpinner.getSelectedItemPosition() != 0 ){
             crop = filCropSpinner.getSelectedItem().toString();
             year = filYearSpinner.getSelectedItem().toString();
             type = filTypeSpinner.getSelectedItemPosition()+"";


             new AsyncLogin().execute(type, year, "-1", crop, "-1"); // 0 for need, -1 for skip argument


            }else{
                Toast.makeText(ForecastFragment.this.getContext(), "Please select crop and year first", Toast.LENGTH_SHORT).show();
            }
        });


        compareBtn.setOnClickListener(view1 -> {


            if (!mealList.isEmpty()){

                String nextYear = String.valueOf((Integer.parseInt(year) + 1));
                new AsyncActualDataProcessor().execute(type, nextYear , "-1", crop, "-1"); // 0 for need, -1 for skip argument

            }else {
                Toast.makeText(ForecastFragment.this.getContext(), "No forecast generated. Please generate forecast first", Toast.LENGTH_SHORT).show();
            }


        });




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
            mealList = new ArrayList<>();
            ArrayList<NeedReport>  tempReportList = new ArrayList<>(); // used to generate the forecast

            reportList = new ArrayList<>(); // used for the graph
            try {
                if(!result.equals("-1")){

                    JSONArray jsonArray  = new JSONArray(result);
                    for(int index = 0; index < jsonArray.length() ; index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        Log.i("JSON 1st ITEM", jsonObject.toString());

                        Integer recID = jsonObject.getInt("recID");
                        String quan = jsonObject.getString("quan");
                        String type = jsonObject.getString("type");
                        String unit = jsonObject.getString("unit");

                        String city = jsonObject.getString("city");
                        String province = jsonObject.getString("province");
                        String year = jsonObject.getString("year");
                        String month = jsonObject.getString("month");


                        NeedEntry meal = new NeedEntry(recID, type + "(" + quan + " " + unit + ")",
                                city + ", " + province + ", for: " + month + ", " + year, false);

                        NeedReport needReport = new NeedReport(type, monthStringToInt(month),
                                Integer.parseInt(year), Double.parseDouble(quan));
                        tempReportList.add(needReport);

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

            //===========================================



            //group data into months


            List<String> allCrops = Arrays.asList(crop);

            HashMap<String, HashMap<Integer, Double>> allRec = createMonthQuanMap(allCrops) ;

            for(NeedReport report: tempReportList){

                HashMap<Integer, Double> pair = allRec.get(report.getItemName());

                double curSum = pair.get(report.getMonth());
                pair.replace(report.getMonth(), curSum + report.getQuan());

            }

            if(isActualDataComplete(allRec)) {


                HashMap<String, HashMap<Integer, Double>> forecastedAll = createForecastedMonthQuanMap(allCrops);

                for (String crop : allCrops) {

                    final float CONST_FACTOR = 0.3f;


                    HashMap<Integer, Double> actualDataPair = allRec.get(crop);
                    HashMap<Integer, Double> forecastedDataPair = forecastedAll.get(crop);

                    Log.e("crop:", crop);

                    for (int c = 0; c <= 11; c++) {
                        Log.e("month " + c + ":", actualDataPair.get(c) + "");

                    }

                    Double actualAvg = getAverageFromActual(new ArrayList<>(actualDataPair.values()));
                    Log.e("actualAvg:{}", actualAvg.toString());


                    forecastedDataPair.put(0, actualAvg);
                    for (int counter = 0; counter < 12; counter++) {

                        Double curAct = new Double(actualDataPair.get(counter));
                        Double curFor = forecastedDataPair.get(counter);

                        Double diff = curAct - curFor;

                        Double nextForMonth = (diff * CONST_FACTOR) + curFor;

                        forecastedDataPair.put(counter + 1, nextForMonth);

                    }

                    Log.e("ForeCast", "ForCast:");
                    final String itemName = "Forecast data";
                    for (int month = 0; month <= 11; month++) {
                        Log.e("ForCast month " + month + ":", forecastedDataPair.get(month) + "");
                        Integer noId = -1;

                        NeedEntry meal = new NeedEntry(noId, monthIntToString(month),
                                df.format(forecastedDataPair.get(month)) + " kg", false);
                        mealList.add(meal);

                        NeedReport needReport = new NeedReport(itemName + "", month,
                                Integer.parseInt(year), forecastedDataPair.get(month));
                        reportList.add(needReport);

                    }

                }

            }

            if(mealList.size() > 0) {
                forecastDetails.setText(generateReportDetails(Integer.parseInt(year), Integer.parseInt(type), crop));
            } else {
                forecastDetails.setText(null);
                Toast.makeText(ForecastFragment.this.getContext(), "Please make sure the actual data for year " + year +" is complete", Toast.LENGTH_LONG).show();
            }

            dataAdapter = new CustomMealsAdapter(ForecastFragment.this.getContext(),R.layout.need_info, mealList);
            listView.setAdapter(dataAdapter);


            //======================================
            //generate forcast



        }

    }

    private class AsyncActualDataProcessor extends AsyncTask<String, String, String>
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

            ArrayList<NeedReport> tempReportList = new ArrayList<>(); // used to generate the forecast

            try {
                if (!result.equals("-1")) {

                    JSONArray jsonArray = new JSONArray(result);
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        Log.i("JSON 1st ITEM", jsonObject.toString());

                        Integer recID = jsonObject.getInt("recID");
                        String quan = jsonObject.getString("quan");
                        String type = jsonObject.getString("type");
                        String unit = jsonObject.getString("unit");

                        String city = jsonObject.getString("city");
                        String province = jsonObject.getString("province");
                        String year = jsonObject.getString("year");
                        String month = jsonObject.getString("month");


                        NeedEntry meal = new NeedEntry(recID,type + "(" + quan + " " + unit + ")",
                                city + ", " + province + ", for: " + month + ", " + year, false);

                        NeedReport needReport = new NeedReport(type, monthStringToInt(month),
                                Integer.parseInt(year), Double.parseDouble(quan));
                        tempReportList.add(needReport);

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

            //===========================================


            //group data into months


            List<String> allCrops = Arrays.asList(crop);


            HashMap<String, HashMap<Integer, Double>> allRec = createMonthQuanMap(allCrops);

            for (NeedReport report : tempReportList) {

                HashMap<Integer, Double> pair = allRec.get(report.getItemName());

                double curSum = pair.get(report.getMonth());
                pair.replace(report.getMonth(), curSum + report.getQuan());

            }


            ArrayList<HashMap<Integer, Double>> actualList = new ArrayList<>(allRec.values()); // assumes there is only one crop
            if (actualList.size() != 0) {


                HashMap<Integer, Double> actualDataPair = actualList.get(0);

                final String itemName = "Actual data";

                for (int month = 0; month <= 11; month++) {
                    Log.e("Actaul DATA", itemName + " " + actualDataPair.get(month));

                    NeedReport needReport = new NeedReport(itemName + "", month,
                            Integer.parseInt(year), actualDataPair.get(month));
                    reportList.add(needReport);

                }

            }

            if(!reportList.isEmpty()) {
                Intent myIntent = new Intent(getActivity(), ReportActivity.class);
                myIntent.putParcelableArrayListExtra(FORECAST_REPORT, reportList);
                myIntent.putExtra("type", FORECAST_REPORT);
                myIntent.putExtra("details",  "Forecast and Actual Data for " + (type.equals("0") ? "Demand" : "Supply") + " of " + crop + " for year " + year);
                startActivityForResult(myIntent, 1);
            }else{
                Toast.makeText(ForecastFragment.this.getContext(), "No forecast generated. Please generate forecast first", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private Double getAverageFromActual(List<Double> valueList){
        return valueList.stream().mapToDouble(val -> val).average().orElse(0.0);

    }


    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private HashMap<String, HashMap<Integer, Double>> createMonthQuanMap(List<String> uniqueCrops){
        HashMap<String, HashMap<Integer, Double>> all = new HashMap<>();
        for(String crop: uniqueCrops) {
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

            all.put(crop, hmap);
        }

        return all;
    }


    private HashMap<String, HashMap<Integer, Double>> createForecastedMonthQuanMap(List<String> uniqueCrops){
        HashMap<String, HashMap<Integer, Double>> all = new HashMap<>();
        for(String crop: uniqueCrops) {
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

            all.put(crop, hmap);
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

    public static String monthIntToString(int month){
      return new DateFormatSymbols().getMonths()[month];

    }

    private String generateReportDetails(int year, int type, String crop){
        return (type == 0 ? "Demand" : "Supply") + " forecast of "+crop+" for year " + (year+1);
    }

    private boolean isActualDataComplete(HashMap<String, HashMap<Integer, Double>> actualData){
        ArrayList<HashMap<Integer, Double>> actualList = new ArrayList<>(actualData.values()); // assumes there is only one crop
        if(actualList.size() == 0){
            return false;
        }

        HashMap<Integer, Double> actualHasMap = actualList.get(0);

        for (int c = 0; c <= 11; c++) {
           Double val = actualHasMap.get(c);
           if(val <= 0){
               return false;
           }
        }

        return true;

    }







}
