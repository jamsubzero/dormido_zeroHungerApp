package com.example.jam.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jam.myapplication.CustomAdapters.CustomMealsAdapter;
import com.example.jam.myapplication.Pojos.NeedEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

import static com.example.jam.myapplication.MapFragment.mMap;

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

    String searchUrl = "http://eresponse.tk/ZeroHunger/query.php";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

//    private OnFragmentInteractionListener mListener;

    ListView listView;
    Button btn;

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
        btn = getView().findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), ReportActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
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

                    {NeedEntry meal = new NeedEntry(item_name+"("+quan+" "+unit+") - "+type,
                            city +", "+province+", for: "+month+", "+year,false);
                        mealList.add(meal);}




                }
                dataAdapter = new CustomMealsAdapter(NeedFragment.this.getContext(),R.layout.need_info, mealList);
                listView.setAdapter(dataAdapter);


            } catch (JSONException e) {
                e.printStackTrace();

            }

//            if(result.equalsIgnoreCase("true"))
//            {
//                /* Here launching another activity when login successful. If you persist login state
//                use sharedPreferences of Android. and logout button to clear sharedPreferences.
//                 */
//
//                Intent intent = new Intent(MainActivity.this,SuccessActivity.class);
//                startActivity(intent);
//                MainActivity.this.finish();
//
//            }else if (result.equalsIgnoreCase("false")){
//
//                // If username and password does not match display a error message
//                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
//
//            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
//
//                Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
//
//            }
//
        }

    }





//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
