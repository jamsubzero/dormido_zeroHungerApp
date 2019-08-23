package com.example.jam.myapplication.addneedhave;

import com.example.jam.myapplication.Pojos.Need;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

public class DataPackager {

    Need need;

    public DataPackager(Need need) {
        this.need = need;
    }

    public String packData()
    {
        JSONObject jo=new JSONObject();
        StringBuffer sb=new StringBuffer();

        try {
            jo.put("userID",need.getUserID());
            jo.put("type", need.getType());
            jo.put("item_name",need.getItem_name());
            jo.put("quan",need.getQuan());
            jo.put("unit",need.getUnit());
            jo.put("year",need.getYear());
            jo.put("month",need.getMonth());
            jo.put("lati",need.getLati());
            jo.put("longi",need.getLongi());
            jo.put("city",need.getCity());
            jo.put("province",need.getProvince());
            jo.put("need_have",need.getNeed_have());
       //
            Boolean firstvalue=true;
            Iterator it=jo.keys();

            do {
                String key=it.next().toString();
                String value=jo.get(key).toString();

                if(firstvalue)
                {
                    firstvalue=false;
                }else
                {
                    sb.append("&");
                }

                sb.append(URLEncoder.encode(key,"UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(value,"UTF-8"));

            }while (it.hasNext());

            return sb.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}