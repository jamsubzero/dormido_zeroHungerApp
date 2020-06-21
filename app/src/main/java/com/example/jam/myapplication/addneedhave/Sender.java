package com.example.jam.myapplication.addneedhave;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jam.myapplication.Pojos.Need;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

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
}