package com.example.jam.myapplication.addneedhave;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jam.myapplication.CustomAdapters.CustomMealsAdapter;
import com.example.jam.myapplication.MainActivity;
import com.example.jam.myapplication.Pojos.Need;
import com.example.jam.myapplication.Pojos.NeedEntry;
import com.example.jam.myapplication.Pojos.NeedReport;
import com.example.jam.myapplication.R;
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

import static com.example.jam.myapplication.MainActivity.CONNECTION_TIMEOUT;
import static com.example.jam.myapplication.MainActivity.READ_TIMEOUT;

public class Sender extends AsyncTask<Void,Void,String> {

    AppCompatActivity c;
    String urlAddress;
    String type;
    String item_nameTxt;
    String quanTxt;
    String unitTxt;
    String spinYear;
    String spinMonth;
    Need need;
    ProgressDialog pd;
    int need_have;


    public static ListView needListView, supplyListView;
    public static CustomMealsAdapter dataAdapter = null;
    public static ArrayList<NeedEntry> mealList = new ArrayList<NeedEntry>();
    public static ArrayList<NeedReport> reportList = new ArrayList<NeedReport>();


    public Sender(AppCompatActivity c, String urlAddress, String userID, String type, String item_name, String quan, String unit,
                  String year, String month, String lati, String longi, String city, String province, int need_have) {
        this.c = c;
        this.urlAddress = urlAddress;
        this.type = type;
        this.item_nameTxt = item_name;
        this.quanTxt = quan;
        this.unitTxt = unit;
        this.spinYear = year;
        this.spinMonth = month;
        this.need_have = need_have;
        this.pd = pd;

        need=new Need(userID, type, item_name, quan, unit,
                year, month, lati, longi, city, province, need_have);
//        need.setName(nameTxt.getText().toString());
//        need.setPropellant(propellantTxt.getText().toString());
//        need.setDescription(descTxt.getText().toString());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        c.runOnUiThread(new Runnable() {
            public void run() {
                pd =  new ProgressDialog(c);
                pd.setTitle("Send");
                pd.setMessage("Sending...Please wait");
                pd.show();
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.send();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        pd.dismiss();

        if(s==null){
            Toast.makeText(c,"Unsuccessful,Null returned",Toast.LENGTH_SHORT).show();
        }else{
            if(s=="Bad Response"){
                Toast.makeText(c,"Unsuccessful,Bad Response returned",Toast.LENGTH_SHORT).show();

            }else
            {
                Toast.makeText(c,"Successfully Saved",Toast.LENGTH_SHORT).show();
                new RefreshRecord().execute(need_have+"", "-1", "-1", "-1", "-1");// 0 for need, -1 for skip argument
            }
        }
    }

    private String send()
    {


        HttpURLConnection con=Connector.connect(urlAddress);
        if(con==null)
        {
            return null;
        }
        try {

            OutputStream os=con.getOutputStream();

            //WRITE
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            bw.write(new DataPackager(need).packData());

            bw.flush();
            //RELEASE
            bw.close();
            os.close();

            //SUCCESS OR NOT??
            int responseCode=con.getResponseCode();
            if(responseCode==con.HTTP_OK)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response=new StringBuffer();

                String line;
                while ((line=br.readLine()) != null)
                {
                    response.append(line);
                }

                br.close();

                return response.toString();
            }else {
                return "Bad Response";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public class RefreshRecord extends AsyncTask<String, String, String>
    {
        String searchUrl = "http://zerop.ml/agri/query.php";
        HttpURLConnection conn;
        URL url = null;
        String need_have = "0";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            need_have = params[0];
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

            Log.i("JSON", result);
            LatLng latLng = null;
            Sender.mealList = new ArrayList<>();
            Sender.reportList = new ArrayList<>();
            try {
                if(!result.equals("-1")){

                    JSONArray jsonArray  = new JSONArray(result);
                    for(int index = 0; index < jsonArray.length() ; index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        Log.i("JSON 1st ITEM", jsonObject.toString());
                        Integer recID = jsonObject.getInt("recID");
                        String item_name = jsonObject.getString("item_name");
                        String quan = jsonObject.getString("quan");
                        String type = jsonObject.getString("type");
                        String unit = jsonObject.getString("unit");

                        String city = jsonObject.getString("city");
                        String province = jsonObject.getString("province");
                        String year = jsonObject.getString("year");
                        String month = jsonObject.getString("month");

                        NeedEntry meal = new NeedEntry(recID , type + "(" + quan + " " + unit + ")",
                                city + ", " + province + ", " + month + ", " + year, false);

                        NeedReport needReport = new NeedReport(type, monthStringToInt(month),
                                Integer.parseInt(year), Double.parseDouble(quan));
                        Sender.reportList.add(needReport);

                        Sender.mealList.add(meal);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

            Sender.dataAdapter = new CustomMealsAdapter(c, R.layout.need_info, Sender.mealList);
            if(need_have.equals("0")) {
                Sender.needListView.setAdapter(dataAdapter);
            }else {
                Sender.supplyListView.setAdapter(dataAdapter);
            }
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
        }else{
            return 0;
        }
    }

}