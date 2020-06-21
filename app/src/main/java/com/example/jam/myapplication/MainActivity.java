package com.example.jam.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;

import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jam.myapplication.addneedhave.Sender;
import com.example.jam.myapplication.ui.login.LoginActivity;
import com.example.jam.myapplication.ui.login.Logout;
import com.example.jam.myapplication.ui.markerInfo.MarkerInfoResult;
import com.example.jam.myapplication.ui.markerInfo.MarkerInfoView;
import com.example.jam.myapplication.ui.markerInfo.MarkerViewModel;
import com.example.jam.myapplication.ui.markerInfo.MarkerViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.example.jam.myapplication.MapFragment.mMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnInfoWindowClickListener {


    private static int SPLASH_TIMEOUT = 2000;

    private static final int REQUEST_PHONE_CALL = 1;
    private int SELECTED_NAV = R.id.nav_map; //  map by default

    private MarkerViewModel markerViewModel;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //http://eresponse.tk/ZeroHunger/insertNeed.php
    String insertUrl = "http://zerop.ml/agri/insertNeed.php";
    String searchUrl = "http://zerop.ml/agri/query.php";
    //String searchUrl = "http://172.20.10.5/zeroHungerServer/query.php";

    MyLocation myLocation;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        markerViewModel = ViewModelProviders.of(this, new MarkerViewModelFactory()).get(MarkerViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (SELECTED_NAV == R.id.nav_map) {
                    showSearchDialog();
                } else if (SELECTED_NAV == R.id.nav_needs) {
                    if (sharedPreferences.getString("id", "").isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please login first", Toast.LENGTH_LONG).show();
                        goto_login();
                    } else {
                        showAddNeedsHaveDialog(0); // 0 for need
                    }
                } else if (SELECTED_NAV == R.id.nav_have) {
                    //TODO showAddHavesDialog()
                    if (sharedPreferences.getString("id", "").isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please login first", Toast.LENGTH_LONG).show();
                        goto_login();
                    } else {
                        showAddNeedsHaveDialog(1); // 1 for have
                    }
                } else if (SELECTED_NAV == R.id.nav_reports) {
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
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();

        if (sharedPreferences.getString("id", "").isEmpty()) {
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(false);
        } else {
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(true);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (myLocation != null) {
            myLocation.cancelTimer();
        }
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
        } else if (id == R.id.nav_forcast) {
            goto_forecast();
        } else if (id == R.id.nav_reports) {
            goto_waste();
        } else if (id == R.id.nav_login) {
            goto_login();
        } else if (id == R.id.nav_aboutus) {
            goto_aboutUs();
        } else if (id == R.id.nav_logout) {
            goto_logout();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showAddNeedsHaveDialog(final int needOrHave) {

        // Snackbar.make(view, "Replalckkkke now with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        String message = null;
        if (needOrHave == 0) { // 0 for need
            message = "Add New Demand";
        } else if (needOrHave == 1) {  // 1 for have
            message = "Add New Supply";
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

                        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                            @Override
                            public void gotLocation(Location location) {
                                Spinner type = dialogView.findViewById(R.id.foodtype);
                                EditText item = dialogView.findViewById(R.id.item); //decription
                                EditText quan = dialogView.findViewById(R.id.quan);
                                Spinner unit = dialogView.findViewById(R.id.unit);
                                Spinner year = dialogView.findViewById(R.id.year);
                                Spinner month = dialogView.findViewById(R.id.month);

                                if (type.getSelectedItemPosition() != 0 &&
                                        !item.getText().toString().isEmpty() &&
                                        !quan.getText().toString().isEmpty() &&
                                        year.getSelectedItemPosition() != 0 &&
                                        month.getSelectedItemPosition() != 0
                                ){

                                    String sUserID = sharedPreferences.getString("id", "");
                                String sType = type.getSelectedItem().toString();
                                String sItem_name = item.getText().toString();
                                String sQuan = quan.getText().toString();
                                String sUnit = unit.getSelectedItem().toString();
                                String sYear = year.getSelectedItem().toString();
                                String sMonth = month.getSelectedItem().toString();

                                //
                                String sLati = String.valueOf(location.getLatitude());
                                String sLongi = String.valueOf(location.getLongitude());
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
                                    if (addresses.size() > 0) {
                                        province = addresses.get(2).getSubAdminArea();
                                        if (province == null) {//if no sub admin, ex: for Metro Manila
                                            province = addresses.get(2).getAdminArea();
                                        }
                                        if (province == null) {//if no sub admin, ex: for Metro Manila
                                            province = "UNKNOWN";
                                        }
                                        city = addresses.get(2).getLocality();
                                        if (city == null) {
                                            city = "UNKNOWN";
                                        }


                                        Log.i("geocode province", province);
                                        Log.i("geocode city", city);
                                        //Log.i("geocode locality", addresses.get(2).toString());
                                    } else {
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
                                Sender s = new Sender(MainActivity.this, insertUrl, sUserID, sType, sItem_name, sQuan, sUnit, sYear, sMonth, sLati,
                                        sLongi, city, province, needOrHave);
                                s.execute();
                                    dialog.dismiss();
                            } else{
                                    MainActivity.this.runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(MainActivity.this, "Save failed, there are unfilled fields", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                           }

                            }
                        };

                        myLocation.getLocation(locationResult);
                        //
                        //=============================END FOR LOCATION


//                        EditText name = dialogView.findViewById(R.id.desc);
//                        Toast.makeText(MainActivity.this, name.getText(), Toast.LENGTH_LONG).show();

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
    private void showReportWasteDialog() {
        final int WASTE_FLAG = 2; // 2 for waste

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

                        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                            @Override
                            public void gotLocation(Location location) {
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
                                String sLongi = String.valueOf(location.getLongitude());
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
                                    if (addresses.size() > 0) {
                                        province = addresses.get(2).getSubAdminArea();
                                        if (province == null) {//if no sub admin, ex: for Metro Manila
                                            province = addresses.get(2).getAdminArea();
                                        }
                                        if (province == null) {//if no sub admin, ex: for Metro Manila
                                            province = "UNKNOWN";
                                        }
                                        city = addresses.get(2).getLocality();
                                        if (city == null) {
                                            city = "UNKNOWN";
                                        }


                                        Log.i("geocode province", province);
                                        Log.i("geocode city", city);
                                        //Log.i("geocode locality", addresses.get(2).toString());
                                    } else {
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
                                Sender s = new Sender(MainActivity.this, insertUrl, sUserID, sType, sItem_name, sQuan, sUnit, "N/A", "N/A", sLati,
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
    private void showSearchDialog() {

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
                        if (reportType.getSelectedItemPosition() != 0) {
                            sReportType = (reportType.getSelectedItemPosition() - 1) + ""; // subtract 1 because the flags also starts at 0
                        }

                        String sSearchYear = "-1";
                        if (searchYear.getSelectedItemPosition() != 0) {
                            sSearchYear = searchYear.getSelectedItem().toString();
                        }
                        String sSearchMonth = "-1";
                        if (searchMonth.getSelectedItemPosition() != 0) {
                            sSearchMonth = searchMonth.getSelectedItem().toString();
                        }

                        String sFoodType = "-1";
                        if (foodType.getSelectedItemPosition() != 0) {
                            sFoodType = foodType.getSelectedItem().toString();
                        }


                        String sItem = item.getText().toString() + "";
                        if (sItem.isEmpty()) {
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
        fab.show();
        fab.setImageResource(R.drawable.baseline_search_24);
        //fab.setVisibility(View.INVISIBLE);
    }

    private void goto_need() {
        SELECTED_NAV = R.id.nav_needs;
        NeedFragment needFragment = new NeedFragment();
        // ItemFragment needFragment = new ItemFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, needFragment).commit();
        fab.show();
        fab.setImageResource(R.drawable.baseline_add_24);
        //  fab.setVisibility(View.VISIBLE);
    }

    private void goto_have() {
        SELECTED_NAV = R.id.nav_have;
        SupplyFragment haveFragment = new SupplyFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, haveFragment).commit();
        // fab.setVisibility(View.VISIBLE);
        fab.show();
        fab.setImageResource(R.drawable.baseline_add_24);

    }

    private void goto_waste() {
        SELECTED_NAV = R.id.nav_reports;
        //WasteFragment wasteFragment = new WasteFragment();
        WastageFragment wasteFragment = new WastageFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, wasteFragment).commit();
        fab.setImageResource(R.drawable.baseline_add_24);
        // fab.setVisibility(View.VISIBLE);
    }

    private void goto_forecast() {
        SELECTED_NAV = R.id.nav_reports;
        //WasteFragment wasteFragment = new WasteFragment();
        ForecastFragment forecastFragment = new ForecastFragment();
        //----
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, forecastFragment).commit();
        fab.hide();
    }

    private void goto_login() {
//        SELECTED_NAV = R.id.nav_login;
//        LoginFragment loginFragment = new LoginFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frame, loginFragment).commit();
//        fab.hide();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        this.startActivity(intent);
    }

    private void goto_logout() {
        Intent intent = new Intent(MainActivity.this, Logout.class);
        startActivity(intent);
    }

    private void goto_aboutUs(){
        Intent intent = new Intent(MainActivity.this, AboutUs.class);
        startActivity(intent);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Context context = MainActivity.this.getApplicationContext();

        //showMapDialog(context);

        String url = context.getResources().getString(R.string.needhavedb_api);
        String id = marker.getSnippet().substring(23);

        markerViewModel.getMarkerData(Integer.parseInt(id), url, context);

        while (!markerViewModel.getMarkerInfoResult().hasObservers()) {
            markerViewModel.getMarkerInfoResult().observe(this, new Observer<MarkerInfoResult>() {
                @Override
                public void onChanged(@Nullable MarkerInfoResult markerInfoResult) {
                    MarkerInfoView model = markerInfoResult.getSuccess();
                    //m.updateDialogData(context, model);
                    showMapDialog(context, model);
                }
            });
        }
    }
    //====================

    private class AsyncLogin extends AsyncTask<String, String, String> {
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
                url = new URL(searchUrl + "?" +
                        "needhave=" + params[0] +
                        "&year=" + params[1] +
                        "&month=" + params[2] +
                        "&type=" + params[3] +
                        "&item=" + params[4] +
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
                conn = (HttpURLConnection) url.openConnection();
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
                    return (result.toString());

                } else {

                    return ("unsuccessful");
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

            mMap.clear();

            pdLoading.dismiss();
            LatLng latLng = null;
            BitmapDescriptor map_icon = null;
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int index = 0; index < jsonArray.length(); index++) {
//                    JSONObject jsonObject  = jsonArray.getJSONObject( index );
//                    Log.i("JSON 1st ITEM", jsonObject.toString());
//                    String item_name = jsonObject.getString("item_name");
//                    String quan = jsonObject.getString("quan");
//                    String unit = jsonObject.getString("unit");
//                    int need_have = jsonObject.getInt("need_have");
//                    String sNeedHaveWaste = "";
//                    if(need_have == 0){sNeedHaveWaste = "Need";}
//                    else if(need_have == 1){sNeedHaveWaste = "Supply";}
//                    else if(need_have == 2){sNeedHaveWaste = "Waste";}
//                    Double lati = jsonObject.getDouble("latitude");
//                    Double longi = jsonObject.getDouble("longitude");
//                    latLng = new LatLng(lati, longi);
//
//                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(item_name).
//                                                                    snippet(quan+" "+unit));
//                    mapMarkers.add(marker);

                    JSONObject dataObj = jsonArray.getJSONObject(index); // loop all

                    Double lat = Double.parseDouble(dataObj.getString("latitude"));
                    Double lng = Double.parseDouble(dataObj.getString("longitude"));

                    Integer id = dataObj.getInt("recID");
                    Integer needHave = dataObj.getInt("need_have");
                    String type = dataObj.getString("type");
                    int quan = dataObj.getInt("quan");
                    String unit = dataObj.getString("unit");

                    String snip = "Click for more info...\n" + id;
                    String title = ": " + type + " (" + quan + " " + unit + ")";
                    String preTitle = "";

                    if (needHave == 1) {
                        map_icon = BitmapDescriptorFactory.fromResource(R.mipmap.farmers);
                        preTitle = "For Sale";
                    } else {
                        map_icon = BitmapDescriptorFactory.fromResource(R.mipmap.cart);
                        preTitle = "Looking For";
                    }

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(preTitle + title)
                            .snippet(snip)
                            .icon(map_icon)
                    );

                }
                if (latLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6));
                }
//                Toast.makeText(MainActivity.this, "Your search returned "+result.length()+" results",
//                        Toast.LENGTH_LONG).show();
                Snackbar.make(fab,
                        "Your search returned " + jsonArray.length() + " results", Snackbar.LENGTH_LONG).show();


            } catch (JSONException e) {
                e.printStackTrace();

                Snackbar.make(fab,
                        "Sorry, we don't have data for that yet.", Snackbar.LENGTH_LONG).show();

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

    private void clearMapMarkers() {

    }

    public void showMapDialog(Context context, MarkerInfoView model) {

        AlertDialog.Builder mapInfoBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.custom_info_window_adapter, null);

        TextView forTypeView = dialogView.findViewById(R.id.for_type);
        forTypeView.setText(model.getDisplayForType());

        TextView typeView = dialogView.findViewById(R.id.type);
        typeView.setText(model.getDisplayType());

        TextView quanView = dialogView.findViewById(R.id.quan);
        quanView.setText(model.getDisplayQuantity());

        TextView unitView = dialogView.findViewById(R.id.unit);
        unitView.setText(model.getDisplayUnit());

        TextView userView = dialogView.findViewById(R.id.user);
        userView.setText(model.getDisplayUser());

        TextView numberView = dialogView.findViewById(R.id.number);
        numberView.setText(model.getDisplayNumber());

        TextView emailView = dialogView.findViewById(R.id.email);
        emailView.setText(model.getDisplayEmail());

        ImageView callButton = dialogView.findViewById(R.id.call_btn);
        ImageView sendSmsBtn = dialogView.findViewById(R.id.sms_btn);
        ImageView sendEmailBtn = dialogView.findViewById(R.id.email_btn);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Calling ", model.getDisplayNumber());

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String call = "tel:" + model.getDisplayNumber();
                callIntent.setData(Uri.parse(call));

                if (ActivityCompat.checkSelfPermission( context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    return;
                }

                context.startActivity(callIntent);
            }
        });

        sendSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Send message ", model.getDisplayNumber());
                Intent openSms = new Intent(Intent.ACTION_SENDTO);
                String sms = "sms:" + model.getDisplayNumber();
                openSms.setData(Uri.parse(sms));

                try {
                    context.startActivity(Intent.getIntent(sms));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        if(model.getDisplayEmail().isEmpty()){
            sendEmailBtn.setVisibility(View.GONE);
        }else {
            sendEmailBtn.setVisibility(View.VISIBLE);
            sendEmailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{model.getDisplayEmail()});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, model.getDisplayType());
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
            });
        }

        mapInfoBuilder.setView(dialogView);

        mapInfoBuilder.setNegativeButton("Close",
                (dialog, id) -> dialog.dismiss());

        AlertDialog mapAlert = mapInfoBuilder.create();

        mapAlert.show();
    }

//    public void updateDialogData(Context context, MarkerInfoView model){
//        AlertDialog.Builder mapInfoBuilder = new AlertDialog.Builder(context);
//    }


    public void checkPermission(String permission, int requestCode)
    {

        // Checking if permission is not granted 
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[] { permission },
                            requestCode);
        }
        else {
            Toast
                    .makeText(MainActivity.this,
                            "Permission already granted",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
} //== END OF CLASS
