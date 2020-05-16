package com.example.jam.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jam.myapplication.Pojos.NeedReport;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        final String REPORT_TYPE = getIntent().getStringExtra("type");

        LineChart chart = findViewById(R.id.barchart);

        if(REPORT_TYPE.equals(NeedFragment.NEED_REPORT)){
            chart.setDescription("Actual Demand Data in kg");
        }else if(REPORT_TYPE.equals(SupplyFragment.SUPPLY_REPORT)){
            chart.setDescription("Actual Supply Data in kg");
        }else if(REPORT_TYPE.equals(ForecastFragment.FORECAST_REPORT)){
            chart.setDescription(getIntent().getStringExtra("details")); // this will come from the fragment
        }

        ArrayList<NeedReport> needReportList = new ArrayList<>();


        needReportList = getIntent().getParcelableArrayListExtra(REPORT_TYPE);


        List<NeedReport> allCrops = needReportList.stream()
                .filter(distinctByKey(p -> p.getItemName()))
                .collect(Collectors.toList());


        HashMap<String, HashMap<Integer, Double>> allRec = createMonthQuanMap(allCrops) ;

            for(NeedReport report: needReportList){

                HashMap<Integer, Double> pair = allRec.get(report.getItemName());

                Double curSum = pair.get(report.getMonth());
                pair.replace(report.getMonth(), curSum + report.getQuan());


            }

        for(NeedReport report: allCrops) {

            HashMap<Integer, Double> pair = allRec.get(report.getItemName());
            Log.e("crop:", report.getItemName());

            for (int c = 0; c <= 11; c++) {
                Log.e("month "+c+":", pair.get(c) + "");

            }

        }

        ArrayList dataSets = new ArrayList();


        for(NeedReport report: allCrops) {

            ArrayList cropData = new ArrayList();

            HashMap<Integer, Double> pair = allRec.get(report.getItemName());

            for (int c = 0; c <= 11; c++) {
                cropData.add(new BarEntry(pair.get(c).floatValue(), c));
            }

            LineDataSet cropDataSet = new LineDataSet(cropData, report.getItemName());

            cropDataSet.setColor(generateRandomColor());

            dataSets.add(cropDataSet);
        }




        ArrayList months = new ArrayList();

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

        LineData data = new LineData(months, dataSets);
        chart.animateY(1000);
        chart.setData(data);

    }


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

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private HashMap<String, HashMap<Integer, Double>> createMonthQuanMap(List<NeedReport> uniqueCrops){
        HashMap<String, HashMap<Integer, Double>> all = new HashMap<>();
        for(NeedReport crop: uniqueCrops) {
            HashMap<Integer, Double> hmap = new HashMap<>();

            /* Key=month, Val=quantity */
            hmap.put(0, 0.0d);
            hmap.put(1, 0.0d);
            hmap.put(2, 0.0d);
            hmap.put(3, 0.0d);
            hmap.put(4, 0.0d);
            hmap.put(5, 0.0d);
            hmap.put(6, 0.0d);
            hmap.put(7, 0.0d);
            hmap.put(8, 0.0d);
            hmap.put(9, 0.0d);
            hmap.put(10, 0.0d);
            hmap.put(11, 0.0d);

            all.put(crop.getItemName(), hmap);
        }

        return all;
    }

    private int generateRandomColor(){
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }



}
