package com.example.jam.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link NeedFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link NeedFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class NeedFragment extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    String searchUrl = "http://zerop.ml/agri/query.php";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    public static final String NEED_REPORT = "need_report";
    public static final boolean IS_NEED = true;

//    private OnFragmentInteractionListener mListener;

    ListView listView;
    Button reportBtn, filterBtn;
    ArrayList<NeedEntry> mealList = new ArrayList<NeedEntry>();
    ArrayList<NeedReport> reportList = new ArrayList<NeedReport>();

    CustomMealsAdapter dataAdapter = null;

    public NeedFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment NeedFragment.
//     */
    // TODO: Rename and change types and number of parameters == String param1, String param2
    public static NeedFragment newInstance() {
        NeedFragment fragment = new NeedFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
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
        reportBtn = getView().findViewById(R.id.submit_btn);
        reportBtn.setText("View Demand Report");
        filterBtn = getView().findViewById(R.id.filterBtn);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                final AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

                builder1.setMessage("Filter Demand");
                builder1.setCancelable(true);
                builder1.setView(R.layout.filter_demand_layout);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.filter_demand_layout, null);
                builder1.setView(dialogView);
                builder1.setPositiveButton(
                        "Filter",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                           final Spinner filCrop =  dialogView.findViewById(R.id.filCrop);
                           final Spinner filMonth =  dialogView.findViewById(R.id.filMonth);
                           final Spinner filYear =  dialogView.findViewById(R.id.filYear);
                           String sCrop = filCrop.getSelectedItem().toString();
                           String sMonth = filMonth.getSelectedItem().toString();
                           String sYear = filYear.getSelectedItem().toString();

                           if(filCrop.getSelectedItemPosition() == 0){
                               sCrop = "-1";
                           }
                           if(filMonth.getSelectedItemPosition() == 0){
                               sMonth = "-1";
                           }
                           if(filYear.getSelectedItemPosition()==0){
                               sYear = "-1";
                           }


                                new AsyncLogin().execute("0", sYear, sMonth, sCrop, "-1");// 0 for need, -1 for skip argument
                                dialog.dismiss();
                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                //
            }
        });


        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ReportActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                myIntent.putParcelableArrayListExtra(NEED_REPORT, reportList);
                myIntent.putExtra("type", IS_NEED);
                startActivityForResult(myIntent, 1);

            }
        });
        new AsyncLogin().execute("0", "-1", "-1", "-1", "-1");// 0 for need, -1 for skip argument
       // new AsyncLogin().execute(sReportType, sSearchYear, sSearchMonth, sFoodType, sItem);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_need, container, false);

    }


//    private void loadMeals(){
//        ArrayList<NeedEntry> mealList = new ArrayList<NeedEntry>();
//
//
//        {NeedEntry meal = new NeedEntry(1, "jam", false);
//            mealList.add(meal);}
//        {NeedEntry meal = new NeedEntry(2, "jam", false);
//            mealList.add(meal);}
//        {NeedEntry meal = new NeedEntry(3, "jam", false);
//            mealList.add(meal);}
//        {NeedEntry meal = new NeedEntry(4, "jam", false);
//            mealList.add(meal);}
//
//
//        dataAdapter = new CustomMealsAdapter(NeedFragment.this.getContext(),R.layout.need_info, mealList);
//        listView.setAdapter(dataAdapter);
//    }



    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(NeedFragment.this.getContext());
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
            mealList = new ArrayList<NeedEntry>();
            reportList = new ArrayList<>();
            try {
                if(!result.equals("-1")){

                JSONArray jsonArray  = new JSONArray(result);
                for(int index = 0; index < jsonArray.length() ; index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    Log.i("JSON 1st ITEM", jsonObject.toString());
                    String item_name = jsonObject.getString("item_name");
                    String quan = jsonObject.getString("quan");
                    String type = jsonObject.getString("type");
                    String unit = jsonObject.getString("unit");
                    int need_have = jsonObject.getInt("need_have");
                    String sNeedHaveWaste = "";
                    if (need_have == 0) {
                        sNeedHaveWaste = "Need";
                    } else if (need_have == 1) {
                        sNeedHaveWaste = "Supply";
                    } else if (need_have == 2) {
                        sNeedHaveWaste = "Waste";
                    }
                    Double lati = jsonObject.getDouble("latitude");
                    Double longi = jsonObject.getDouble("longitude");
                    String city = jsonObject.getString("city");
                    String province = jsonObject.getString("province");
                    String year = jsonObject.getString("year");
                    String month = jsonObject.getString("month");


                    latLng = new LatLng(lati, longi);

                    NeedEntry meal = new NeedEntry(type + "(" + quan + " " + unit + ")",
                            city + ", " + province + ", for: " + month + ", " + year, false);

                    NeedReport needReport = new NeedReport(type, monthStringToInt(month),
                            Integer.parseInt(year), Integer.parseInt(quan));
                    reportList.add(needReport);

                    mealList.add(meal);
                }

                }else{
                    Toast.makeText(getActivity(), "No record found", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
            dataAdapter = new CustomMealsAdapter(NeedFragment.this.getContext(),R.layout.need_info, mealList);
            listView.setAdapter(dataAdapter);


        }

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
