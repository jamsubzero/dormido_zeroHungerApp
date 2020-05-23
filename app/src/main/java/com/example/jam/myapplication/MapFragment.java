package com.example.jam.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jam.myapplication.ui.markerInfo.MarkerInfoResult;
import com.example.jam.myapplication.ui.markerInfo.MarkerInfoView;
import com.example.jam.myapplication.ui.markerInfo.MarkerViewModel;
import com.example.jam.myapplication.ui.markerInfo.MarkerViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MapFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MapFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener
{
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public static GoogleMap mMap;
    private Marker myMarker;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private MarkerViewModel markerViewModel;


//    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    //    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MapFragment.
//     */
    // TODO: Rename and change types and number of parameters ==String param1, String param2
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
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
    public void onLocationChanged(Location location) {
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
//        mMap.animateCamera(cameraUpdate);
//  locationManager.removeUpdates(this);
 //       LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        Toast.makeText(this.getContext(), location.getLatitude()+"=="+location.getLongitude(), Toast.LENGTH_LONG).show();
 //      mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.setMaxZoomPreference(400);
    }



//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.layout_maps, container, false);
//
//
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_maps, container, false);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {



        mMap = googleMap;
        if(checkPermission()){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }else{
            askPermission();
        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
     //   mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(true);
        ProgressDialog pd =  new ProgressDialog(this.getContext());
        pd.setTitle("Send");
        pd.setMessage("Sending...Please wait");
        pd.show();

        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(final Location location){
                MapFragment.this.getActivity().
                runOnUiThread(new Runnable(){
                    public void run() {

                        mMap.isMyLocationEnabled();

                        String url = getResources().getString(R.string.needhavedb_api);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                    int index = 0;
                                    int status = 0;
                                    String message = "";
                                    BitmapDescriptor map_icon = null;

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("response", String.valueOf(response));
                                try{

                                    status = response.getInt("status");
                                    message = response.getString("message");

                                    if (status == 200){

                                        JSONArray dataJson = response.getJSONArray("data");

                                        while(index < dataJson.length()){

                                            JSONObject dataObj = dataJson.getJSONObject(index); // loop all

                                            Double lat = Double.parseDouble(dataObj.getString("lat"));
                                            Double lng = Double.parseDouble(dataObj.getString("long"));

                                            Integer id = dataObj.getInt("id");
                                            Integer needHave = dataObj.getInt("need_have");
                                            String type = dataObj.getString("type");
                                            int quan = dataObj.getInt("quan");
                                            String unit = dataObj.getString("unit");

                                            String snip =  "Click for more info...\n"+id;
                                            String title = ": " + type + " (" + quan + " " + unit + ")";
                                            String preTitle = "";

                                            if (needHave == 1){
                                                map_icon = BitmapDescriptorFactory.fromResource(R.mipmap.farmers);
                                                preTitle = "For Sale";
                                            }else{
                                                map_icon = BitmapDescriptorFactory.fromResource(R.mipmap.cart);
                                                preTitle = "Looking For";
                                            }

                                            myMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat,lng))
                                                    .title(preTitle + title)
                                                    .snippet(snip)
                                                    .icon(map_icon)
                                            );

                                            index++;
                                        }
                                    } else {
                                        Toast.makeText( getContext(), "Server Error", Toast.LENGTH_LONG).show();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error", String.valueOf(error));
                                Toast.makeText( getContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
                            }
                        });

                        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

                        //TODO: Clustering and custom window info
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                       // mMap.addMarker(new MarkerOptions().position(latLng).title("Current Looation"));


//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.175661F,122.944741F)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.farmers)));
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.179251F,122.943539F)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.farmers)));
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.172493F,122.946200F)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.farmers)));
//
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.194858, 122.861871)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.cart)));
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.194563, 122.861055)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.cart)));
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.194690, 122.862149)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.cart)));
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.195070, 122.861495)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.cart)));
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(10.195007, 122.861710)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.cart)));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));          // UI code goes here
                    }
                });

            }
        };



        MyLocation myLocation = new MyLocation(this.getActivity());
        myLocation.getLocation(locationResult);
        pd.dismiss();
//        // Add a marker in Sydney and move the camera
//        checkLocationPermission();
//        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//        String provider =  locationManager.getBestProvider(new Criteria(), false);
//       LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//        Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        LatLng latLng = new LatLng(10.1699464, 122.7979289); // CHMSC
//        if(loc!=null){
//
//          latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
//        }
//   //10.1699464,122.7979289
//
//        Toast.makeText(this.getContext(), latLng.toString(), Toast.LENGTH_LONG).show();
//        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Looation"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean checkPermission() {
        Log.d("MAP", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    // Asks for permission
    private void askPermission() {
        Log.d("MAP", "askPermission()");
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                MY_PERMISSIONS_REQUEST_LOCATION
        );
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this.getContext())
                        .setTitle("Title")
                        .setMessage("Please enable location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapFragment.this.getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MapFragment.this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
                       String provider =  locationManager.getBestProvider(new Criteria(), false);
                       locationManager.requestLocationUpdates(provider, 400, 1, this);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        markerViewModel = ViewModelProviders.of(this, new MarkerViewModelFactory()).get(MarkerViewModel.class);

        MainActivity m = new MainActivity();
        Context context = getContext();

        String url = context.getResources().getString(R.string.needhavedb_api);
        String id = marker.getSnippet().substring(23);
        markerViewModel.getMarkerData(Integer.parseInt(id), url, context);

        markerViewModel.getMarkerInfoResult().observe(this, new Observer<MarkerInfoResult>() {
            @Override
            public void onChanged(@Nullable MarkerInfoResult markerInfoResult) {
                MarkerInfoView model =  markerInfoResult.getSuccess();
                m.showMapDialog(context, model);

            }
        });

    }
}// END of

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

 //   @Override
 //   public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//       }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
