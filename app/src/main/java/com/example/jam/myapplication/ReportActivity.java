package com.example.jam.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        LineChart chart = findViewById(R.id.barchart);

        ArrayList riceData = new ArrayList();
        riceData.add(new BarEntry(50f, 0));
        riceData.add(new BarEntry(52f, 1));
        riceData.add(new BarEntry(53f, 2));
        riceData.add(new BarEntry(54f, 3));
        riceData.add(new BarEntry(55f, 4));
        riceData.add(new BarEntry(40f, 5));
        riceData.add(new BarEntry(55f, 6));
        riceData.add(new BarEntry(56f, 7));
        riceData.add(new BarEntry(57f, 8));
        riceData.add(new BarEntry(58f, 9));
        riceData.add(new BarEntry(65f, 10));
        riceData.add(new BarEntry(70f, 11));

        //=============

        ArrayList fishData = new ArrayList();
        fishData.add(new BarEntry(20f, 0));
        fishData.add(new BarEntry(25f, 1));
        fishData.add(new BarEntry(26f, 2));
        fishData.add(new BarEntry(30f, 3));
        fishData.add(new BarEntry(32f, 4));
        fishData.add(new BarEntry(35, 5));
        fishData.add(new BarEntry(37f, 6));
        fishData.add(new BarEntry(37, 7));
        fishData.add(new BarEntry(38, 8));
        fishData.add(new BarEntry(26f, 9));
        fishData.add(new BarEntry(49f, 10));
        fishData.add(new BarEntry(40f, 11));


        //===

        ArrayList fruitsData = new ArrayList();
        fruitsData.add(new BarEntry(60f, 0));
        fruitsData.add(new BarEntry(62f, 1));
        fruitsData.add(new BarEntry(63f, 2));
        fruitsData.add(new BarEntry(64f, 3));
        fruitsData.add(new BarEntry(65f, 4));
        fruitsData.add(new BarEntry(66f, 5));
        fruitsData.add(new BarEntry(67f, 6));
        fruitsData.add(new BarEntry(69f, 7));
        fruitsData.add(new BarEntry(72f, 8));
        fruitsData.add(new BarEntry(65f, 9));
        fruitsData.add(new BarEntry(67f, 10));
        fruitsData.add(new BarEntry(77f, 11));

        //===


        ArrayList months = new ArrayList();
//
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");

        LineDataSet riceDataSet = new LineDataSet(riceData, "Rice");
        riceDataSet.setColor(Color.BLUE);

        LineDataSet fishDataSet = new LineDataSet(fishData, "Fish");
        fishDataSet.setColor(Color.GREEN);

        LineDataSet fruitsDataSet = new LineDataSet(fruitsData, "Fish");
        fruitsDataSet.setColor(Color.RED);


        ArrayList dataSets = new ArrayList();
        dataSets.add(fishDataSet);
        dataSets.add(riceDataSet);
        dataSets.add(fruitsDataSet);

        LineData data = new LineData(months, dataSets);

        chart.animateY(1000);

        chart.setData(data);

    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finishActivity(1);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finishActivity(1);
        return true;
    }

}
