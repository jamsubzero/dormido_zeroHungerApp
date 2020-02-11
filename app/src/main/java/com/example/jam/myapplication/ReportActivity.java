package com.example.jam.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.example.jam.myapplication.Pojos.Need;
import com.example.jam.myapplication.Pojos.NeedReport;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ArrayList<NeedReport> needReportList = getIntent().getParcelableArrayListExtra(NeedFragment.NEED_REPORT);

        LineChart chart = findViewById(R.id.barchart);

        needReportList.forEach(nr-> {
            //Log.e("sirjom", nr.getItemName() + nr.getMonth() + nr.getQuan() + nr.getYear());
             }
        );

        List<NeedReport> allCrops = needReportList.stream()
                .filter(distinctByKey(p -> p.getItemName()))
                .collect(Collectors.toList());


        HashMap<String, HashMap<Integer, Integer>> allRec = createMonthQuanMap(allCrops) ;

            for(NeedReport report: needReportList){

                HashMap<Integer, Integer> pair = allRec.get(report.getItemName());

                int curSum = pair.get(report.getMonth());
                pair.replace(report.getMonth(), curSum + report.getQuan());


            }

        for(NeedReport report: allCrops) {

            HashMap<Integer, Integer> pair = allRec.get(report.getItemName());
            Log.e("crop:", report.getItemName());

            for (int c = 0; c <= 11; c++) {
                Log.e("month "+c+":", pair.get(c) + "");

            }

        }

        ArrayList dataSets = new ArrayList();
        int colRand = 0;

        for(NeedReport report: allCrops) {
            colRand++;
            ArrayList cropData = new ArrayList();

            HashMap<Integer, Integer> pair = allRec.get(report.getItemName());

            for (int c = 0; c <= 11; c++) {
                cropData.add(new BarEntry(pair.get(c), c));
            }

            LineDataSet cropDataSet = new LineDataSet(cropData, report.getItemName());
            if(colRand % 2 == 0) {
                cropDataSet.setColor(Color.BLUE);
            }else {
                cropDataSet.setColor(Color.RED);
            }
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

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private HashMap<String, HashMap<Integer, Integer>> createMonthQuanMap(List<NeedReport> uniqueCrops){
        HashMap<String, HashMap<Integer, Integer>> all = new HashMap<>();
        for(NeedReport crop: uniqueCrops) {
            HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();

            /* Key=month, Val=quantity */
            hmap.put(0, 0);
            hmap.put(1, 0);
            hmap.put(2, 0);
            hmap.put(3, 0);
            hmap.put(4, 0);
            hmap.put(5, 0);
            hmap.put(6, 0);
            hmap.put(7, 0);
            hmap.put(8, 0);
            hmap.put(9, 0);
            hmap.put(10, 0);
            hmap.put(11, 0);

            all.put(crop.getItemName(), hmap);
        }

        return all;
    }



}
