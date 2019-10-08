package com.example.jam.myapplication.CustomAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jam.myapplication.Pojos.NeedEntry;
import com.example.jam.myapplication.R;

import java.util.ArrayList;

/**
 * Created by jamsubzero on 4/20/2016.
 */

public class CustomMealsAdapter extends ArrayAdapter<NeedEntry> {

    private ArrayList<NeedEntry> mealList;
Context context;
    int selectedIndex = -1;
    public CustomMealsAdapter(Context context, int textViewResourceId,
                              ArrayList<NeedEntry> mealList) {
        super(context, textViewResourceId, mealList);
        this.mealList = new ArrayList<NeedEntry>();
        this.mealList.addAll(mealList);
        this.context = context;
    }

    private class ViewHolder {
      //  TextView code;
        TextView txt1;
        TextView txt2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.need_info, parent, false);

            holder = new ViewHolder();
          //  holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.txt1 = (TextView) convertView.findViewById(R.id.txt1);
            holder.txt2 = (TextView) convertView.findViewById(R.id.txt2);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }



        NeedEntry meal = mealList.get(position);
    //    holder.code.setText(" (" + meal.getCode() + ")");
        holder.txt1.setText(meal.getCode()+"");
        holder.txt2.setText(meal.getName());

        return convertView;

    }


    public void setSelectedIndex(int index){
        selectedIndex = index;
    }

    public ArrayList<NeedEntry> getList(){
      return  mealList;
    }

}