package com.example.jam.myapplication;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import com.example.jam.myapplication.CustomAdapters.CustomMealsAdapter;
import com.example.jam.myapplication.Pojos.NeedEntry;
import com.example.jam.myapplication.Pojos.NeedReport;
import com.example.jam.myapplication.ui.markerInfo.MarkerInfoResult;
import com.example.jam.myapplication.ui.markerInfo.MarkerInfoView;
import com.example.jam.myapplication.ui.markerInfo.MarkerViewModel;
import com.example.jam.myapplication.ui.markerInfo.MarkerViewModelFactory;
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

import static com.example.jam.myapplication.NeedFragment.monthStringToInt;


public class SupplyFragment extends Fragment {

    String searchUrl = "http://zerop.ml/agri/query.php";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String SUPPLY_REPORT = "supply_report";

    ListView listView;
    Button btn;
    ArrayList<NeedEntry> mealList = new ArrayList<>();
    ArrayList<NeedReport> reportList = new ArrayList<>();
    CustomMealsAdapter dataAdapter = null;
    private MarkerViewModel markerViewModel;

    public SupplyFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters == String param1, String param2
    public static SupplyFragment newInstance() {
        SupplyFragment fragment = new SupplyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ImageView imageView = (ImageView) getView().findViewById(R.id.foo);
        listView = (ListView) getView().findViewById(R.id.mealList);
        btn = getView().findViewById(R.id.submit_btn);
        btn.setText("View Supply Report");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ReportActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                myIntent.putParcelableArrayListExtra(SUPPLY_REPORT, reportList);
                myIntent.putExtra("type", SUPPLY_REPORT);
                startActivityForResult(myIntent, 1);

            }
        });

        new AsyncLogin().execute("1", "-1", "-1", "-1", "-1");// 0 for need, -1 for skip argument
       // sReportType, sSearchYear, sSearchMonth, sFoodType, sItem);

        listView.setOnItemClickListener((adapterView, view1, pos, id) -> {
            NeedEntry selectedEntry = (NeedEntry) adapterView.getItemAtPosition(pos);
            Log.i("selectedRecID", selectedEntry.getRecID()+"");
            String mid = selectedEntry.getRecID()+"";

            markerViewModel = ViewModelProviders.of(SupplyFragment.this, new MarkerViewModelFactory()).get(MarkerViewModel.class);

            final MainActivity m = new MainActivity();
            Context context = getContext();

            String url = context.getResources().getString(R.string.needhavedb_api);

            markerViewModel.getMarkerData(Integer.parseInt(mid), url, context);

            while (!markerViewModel.getMarkerInfoResult().hasObservers()){
                markerViewModel.getMarkerInfoResult().observe(getActivity(), new Observer<MarkerInfoResult>() {
                    @Override
                    public void onChanged(@Nullable MarkerInfoResult markerInfoResult) {
                        MarkerInfoView model =  markerInfoResult.getSuccess();
                        m.showMapDialog(context, model);
                    }
                });
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_need, container, false);

    }


    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(SupplyFragment.this.getContext());
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

            //this method will be running on UI thread

            //Toast.makeText(MainActivity.this, "Successfully Fetched!", Toast.LENGTH_LONG).show();
            Log.i("JSON", result);
            pdLoading.dismiss();
            LatLng latLng = null;
            ArrayList<NeedEntry> mealList = new ArrayList<NeedEntry>();
            try {
                JSONArray jsonArray  = new JSONArray(result);
                for(int index = 0; index < jsonArray.length() ; index++){
                    JSONObject jsonObject  = jsonArray.getJSONObject( index );
                    Log.i("JSON 1st ITEM", jsonObject.toString());
                    Integer recID = jsonObject.getInt("recID");
                    String item_name = jsonObject.getString("item_name");
                    String quan = jsonObject.getString("quan");
                    String type = jsonObject.getString("type");
                    String unit = jsonObject.getString("unit");
                    int need_have = jsonObject.getInt("need_have");
                    String sNeedHaveWaste = "";
                    if(need_have == 0){sNeedHaveWaste = "Need";}
                    else if(need_have == 1){sNeedHaveWaste = "Supply";}
                    else if(need_have == 2){sNeedHaveWaste = "Waste";}
                    Double lati = jsonObject.getDouble("latitude");
                    Double longi = jsonObject.getDouble("longitude");
                    String city = jsonObject.getString("city");
                    String province = jsonObject.getString("province");
                    String year = jsonObject.getString("year");
                    String month = jsonObject.getString("month");


                    latLng = new LatLng(lati, longi);

                    NeedEntry meal = new NeedEntry(recID,type+"("+quan+" "+unit+")",
                            city +", "+province+", for: "+month+", "+year,false);

                    NeedReport needReport = new NeedReport(type, monthStringToInt(month),
                            Integer.parseInt(year), Double.parseDouble(quan));
                    reportList.add(needReport);

                    mealList.add(meal);



                }
                dataAdapter = new CustomMealsAdapter(SupplyFragment.this.getContext(),R.layout.need_info, mealList);
                listView.setAdapter(dataAdapter);


            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

    }




}
