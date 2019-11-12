package com.example.jam.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jam.myapplication.addneedhave.Sender;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.List;
import java.util.Locale;

import static com.example.jam.myapplication.MapFragment.mMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private int SELECTED_NAV = R.id.nav_map; //  map by default

    private ArrayList<Marker> mapMarkers = new ArrayList<Marker>();

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //http://eresponse.tk/ZeroHunger/insertNeed.php
    String insertUrl = "http://zerop.ml/ZeroHunger/insertNeed.php";
    String searchUrl = "http://zerop.ml/ZeroHunger/query.php";
    //String searchUrl = "http://172.20.10.5/zeroHungerServer/query.php";

    MyLocation myLocation;

    FloatingActionButton fab ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(SELECTED_NAV == R.id.nav_map) {
                    showSearchDialog();
                }else if(SELECTED_NAV == R.id.nav_needs) {
                    showAddNeedsHaveDialog( 0  ); // 0 for need
                }else if(SELECTED_NAV == R.id.nav_have){
                    //TODO showAddHavesDialog()
                    showAddNeedsHaveDialog( 1  ); // 1 for have
                }else if(SELECTED_NAV == R.id.nav_reports){
                    showReportWasteDialog();
                }


            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    myLocation = new MyLocation(MainActivity.this);

        //---- goto need by default
        goto_map();

    }

    @Override
    public void onPause() {
        super.onPause();
        myLocation.cancelTimer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {

            goto_map();

        } else if (id == R.id.nav_needs) {

            goto_need();

        } else if (id == R.id.nav_have) {

            goto_have();

        } else if (id == R.id.nav_reports) {
            goto_waste();
        }


          else if (id == R.id.nav_tipid) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showAddNeedsHaveDialog(final int needOrHave){

        // Snackbar.make(view, "Replalckkkke now with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        String message = null;
        if(needOrHave == 0){ // 0 for need
            message = "What do you need?";
        }else if(needOrHave ==1){  // 1 for have
            message = "What do you have?";
        }
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setView(R.layout.add_need_dialog_layout);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_need_dialog_layout, null);
        builder1.setView(dialogView);
        Location lo;
        builder1.setPositiveButton(
                "SAVE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                            @Override
                            public void gotLocation(Location location){
                                Spinner type = dialogView.findViewById(R.id.foodtype);
                                EditText item = dialogView.findViewById(R.id.item);
                                EditText quan = dialogView.findViewById(R.id.quan);
                                EditText unit = dialogView.findViewById(R.id.unit);
                                Spinner year = dialogView.findViewById(R.id.year);
                                Spinner month = dialogView.findViewById(R.id.month);

                                String sUserID = "jam";
                                String sType = type.getSelectedItem().toString();
                                String sItem_name = item.getText().toString();
                                String sQuan = quan.getText().toString();
                                String sUnit = unit.getText().toString();
                                String sYear = year.getSelectedItem().toString();
                                String sMonth = month.getSelectedItem().toString();

                                //
                                String sLati = String.valueOf(location.getLatitude());
                                String  sLongi = String.valueOf(location.getLongitude());
                                // geocode
                                String province = null;
                                String city = null;
                                //Got the location!
                                 // 0 for need
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(
                                            //10.195131,122.8645368== Binalbagan
                                            //10.1876625,122.8484584 == chmsc
//                                            10.1876625,
//                                            122.8484584, 3); // 3 results for accuracy
                                            location.getLatitude(),
                                            location.getLongitude(), 3);// 3 results for accuracy
                                   // Toast.makeText(MainActivity.this, addresses.get(0).toString(), Toast.LENGTH_LONG).show();
                                    if(addresses.size()>0) {
                                        province = addresses.get(2).getSubAdminArea();
                                        if(province == null){//if no sub admin, ex: for Metro Manila
                                           province = addresses.get(2).getAdminArea();
                                        }
                                        if(province == null){//if no sub admin, ex: for Metro Manila
                                            province = "UNKNOWN";
                                        }
                                        city =  addresses.get(2).getLocality();
                                        if(city == null){
                                            city = "UNKNOWN";
                                        }


                                        Log.i("geocode province", province);
                                        Log.i("geocode city", city);
                                        //Log.i("geocode locality", addresses.get(2).toString());
                                    }else{
                                        //TODO UNKNOWN ADDRESS HERE
                                        city = "UNKNOWN";
                                        province = "UNKNOWN";
                                    }

                                } catch (IOException ioException) {
                                    // Catch network or other I/O problems.

                                    Log.e("GeocodeIOE", ioException.toString(), ioException);
                                } catch (IllegalArgumentException illegalArgumentException) {

                                    Log.e("GeocodeIllegalIO", illegalArgumentException.toString() + ". " +
                                            "Latitude = " + location.getLatitude() +
                                            ", Longitude = " +
                                            location.getLongitude(), illegalArgumentException);
                                }




                                // save(sUserID, sItem_name, sDesc, sLati, sLongi, sNeed_have);
                                Sender s=new Sender(MainActivity.this,insertUrl,sUserID, sType, sItem_name, sQuan, sUnit, sYear, sMonth, sLati,
                                        sLongi, city, province, needOrHave);
                                s.execute();
                            }
                        };

                        myLocation.getLocation(locationResult);
                        //
                        //=============================END FOR LOCATION




//                        EditText name = dialogView.findViewById(R.id.desc);
//                        Toast.makeText(MainActivity.this, name.getText(), Toast.LENGTH_LONG).show();
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


    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showReportWasteDialog(){
         final  int WASTE_FLAG = 2; // 2 for waste

        // Snackbar.make(view, "Replalckkkke now with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

        builder1.setMessage("Report food waste");
        builder1.setCancelable(true);
        builder1.setView(R.layout.report_waste_dialog_layout);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.report_waste_dialog_layout, null);
        builder1.setView(dialogView);
        Location lo;
        builder1.setPositiveButton(
                "SAVE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                            @Override
                            public void gotLocation(Location location){
                                Spinner type = dialogView.findViewById(R.id.reportFoodType);
                                EditText item = dialogView.findViewById(R.id.reportItem);
                                EditText quan = dialogView.findViewById(R.id.reportQuan);
                                EditText unit = dialogView.findViewById(R.id.reportUnit);

                                String sUserID = "jam";
                                String sType = type.getSelectedItem().toString();
                                String sItem_name = item.getText().toString();
                                String sQuan = quan.getText().toString();
                                String sUnit = unit.getText().toString();

                                String sLati = String.valueOf(location.getLatitude());
                                String  sLongi = String.valueOf(location.getLongitude());
                                // geocode
                                String province = null;
                                String city = null;
                                //Got the location!
                                // 0 for need
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(
                                            //10.195131,122.8645368== Binalbagan
                                            //10.1876625,122.8484584 == chmsc
//                                            10.1876625,
//                                            122.8484584, 3); // 3 results for accuracy
                                            location.getLatitude(),
                                            location.getLongitude(), 3);// 3 results for accuracy
                                    // Toast.makeText(MainActivity.this, addresses.get(0).toString(), Toast.LENGTH_LONG).show();
                                    if(addresses.size()>0) {
                                        province = addresses.get(2).getSubAdminArea();
                                        if(province == null){//if no sub admin, ex: for Metro Manila
                                            province = addresses.get(2).getAdminArea();
                                        }
                                        if(province == null){//if no sub admin, ex: for Metro Manila
                                            province = "UNKNOWN";
                                        }
                                        city =  addresses.get(2).getLocality();
                                        if(city == null){
                                            city = "UNKNOWN";
                                        }


                                        Log.i("geocode province", province);
                                        Log.i("geocode city", city);
                                        //Log.i("geocode locality", addresses.get(2).toString());
                                    }else{
                                        //TODO UNKNOWN ADDRESS HERE
                                        city = "UNKNOWN";
                                        province = "UNKNOWN";
                                    }

                                } catch (IOException ioException) {
                                    // Catch network or other I/O problems.

                                    Log.e("GeocodeIOE", ioException.toString(), ioException);
                                } catch (IllegalArgumentException illegalArgumentException) {

                                    Log.e("GeocodeIllegalIO", illegalArgumentException.toString() + ". " +
                                            "Latitude = " + location.getLatitude() +
                                            ", Longitude = " +
                                            location.getLongitude(), illegalArgumentException);
                                }




                                // save(sUserID, sItem_name, sDesc, sLati, sLongi, sNeed_have);
                                Sender s=new Sender(MainActivity.this,insertUrl,sUserID, sType, sItem_name, sQuan, sUnit, "N/A", "N/A", sLati,
                                        sLongi, city, province, WASTE_FLAG);
                                s.execute();
                            }
                        };

                        myLocation.getLocation(locationResult);
                        //
                        //=============================END FOR LOCATION




//                        EditText name = dialogView.findViewById(R.id.desc);
//                        Toast.makeText(MainActivity.this, name.getText(), Toast.LENGTH_LONG).show();
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


    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showSearchDialog(){

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

        builder1.setMessage("Search food");
        builder1.setCancelable(true);
        builder1.setView(R.layout.search_dialog_layout);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.search_dialog_layout, null);
        builder1.setView(dialogView);
        Location lo;
        builder1.setPositiveButton(
                "Search",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Spinner reportType = dialogView.findViewById(R.id.searchreporttype);
                        Spinner searchYear = dialogView.findViewById(R.id.searchyear);
                        Spinner searchMonth = dialogView.findViewById(R.id.searchmonth);
                        Spinner foodType = dialogView.findViewById(R.id.searchfoodtype);
                        EditText item = dialogView.findViewById(R.id.searchItem);

                        String sReportType = "-1";
                        if(reportType.getSelectedItemPosition()!=0){
                          sReportType = (reportType.getSelectedItemPosition()-1) + ""; // subtract 1 because the flags also starts at 0
                        }

                        String sSearchYear = "-1";
                        if(searchYear.getSelectedItemPosition()!=0){
                            sSearchYear = searchYear.getSelectedItem().toString();
                        }
                        String sSearchMonth = "-1";
                        if(searchMonth.getSelectedItemPosition()!=0){
                            sSearchMonth = searchMonth.getSelectedItem().toString();
                        }

                        String sFoodType = "-1";
                        if(foodType.getSelectedItemPosition()!=0){
                            sFoodType = foodType.getSelectedItem().toString();
                        }


                        String sItem = item.getText().toString() + "";
                        if(sItem.isEmpty()){
                            sItem = "-1";
                        }

                        clearMapMarkers();
                        new AsyncLogin().execute(sReportType, sSearchYear, sSearchMonth, sFoodType, sItem);
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

    }




    private void goto_map() {
        SELECTED_NAV = R.id.nav_map;
        MapFragment mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, mapFragment).commit();

        fab.setImageResource(R.drawable.baseline_search_24);
        //fab.setVisibility(View.INVISIBLE);
    }

    private void goto_need(){
        SELECTED_NAV = R.id.nav_needs;
        NeedFragment needFragment = new NeedFragment();
       // ItemFragment needFragment = new ItemFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, needFragment).commit();

        fab.setImageResource(R.drawable.baseline_add_24);
      //  fab.setVisibility(View.VISIBLE);
    }

    private void goto_have(){
        SELECTED_NAV = R.id.nav_have;
        SupplyFragment haveFragment = new SupplyFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, haveFragment).commit();
       // fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.baseline_add_24);

    }

    private void goto_waste(){
        SELECTED_NAV = R.id.nav_reports;
        //WasteFragment wasteFragment = new WasteFragment();
        WastageFragment wasteFragment = new WastageFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, wasteFragment).commit();
        fab.setImageResource(R.drawable.baseline_add_24);
       // fab.setVisibility(View.VISIBLE);
    }
    //====================

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSearching...please wait.");
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
            try {
                JSONArray jsonArray  = new JSONArray(result);
                for(int index = 0; index < jsonArray.length() ; index++){
                  JSONObject jsonObject  = jsonArray.getJSONObject( index );
                    Log.i("JSON 1st ITEM", jsonObject.toString());
                    String item_name = jsonObject.getString("item_name");
                    String quan = jsonObject.getString("quan");
                    String unit = jsonObject.getString("unit");
                    int need_have = jsonObject.getInt("need_have");
                    String sNeedHaveWaste = "";
                    if(need_have == 0){sNeedHaveWaste = "Need";}
                    else if(need_have == 1){sNeedHaveWaste = "Supply";}
                    else if(need_have == 2){sNeedHaveWaste = "Waste";}
                    Double lati = jsonObject.getDouble("latitude");
                    Double longi = jsonObject.getDouble("longitude");
                    latLng = new LatLng(lati, longi);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(item_name)
                            .snippet(quan+" "+unit)
                           // .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_warning_24)) // gets the icon from the resource
                    );
                    mapMarkers.add(marker);

                }
                if(latLng !=null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6));
                }
                Toast.makeText(MainActivity.this, "Your search returned "+mapMarkers.size()+" results",
                        Toast.LENGTH_LONG).show();
      Snackbar.make(fab,
              "Your search returned "+mapMarkers.size()+" results", Snackbar.LENGTH_LONG).show();


            } catch (JSONException e) {
                e.printStackTrace();

                Snackbar.make( fab,
                        "Sorry, we dont have data for that yet.", Snackbar.LENGTH_LONG).show();

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

    private void clearMapMarkers(){

        for(Marker marker: mapMarkers){
            marker.remove();
        }
    }








} //== END OF CLASS
