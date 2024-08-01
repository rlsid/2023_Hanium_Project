package com.example.farm.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.example.farm.Nutrition;
import com.example.farm.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class NutritionDialog extends Dialog {

    private BarChart vitamin_chart, etc_chart;
    private ImageButton close_btn;


    public NutritionDialog(@NonNull Context context, ArrayList<Nutrition> vitamin, ArrayList<Nutrition> etc) {
        super(context);
        setContentView(R.layout.innutrition_dialog);

        vitamin_chart = findViewById(R.id.vitamin_ch);
        etc_chart = findViewById(R.id.etc_ch);
        close_btn = findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 비타민 영양소 막대그래프 설정 및 초기화
        BarData vitaminBarData = getBarDatas(vitamin);
        vitamin_chart.setData(vitaminBarData);
        XAxis xAxis = vitamin_chart.getXAxis();
        ArrayList<String> vitamin_names = new ArrayList<>();
        Iterator<Nutrition> it = vitamin.iterator();
        while(it.hasNext()){
            Nutrition nut = it.next();
            if(nut.getAmount() > 1) {
                vitamin_names.add(nut.getNutrition());
                Log.i("vitamin", nut.getNutrition());
            }
        }
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(vitamin_names));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        vitamin_chart.setFitBars(true);
        vitamin_chart.setScaleEnabled(false);
        vitamin_chart.setClickable(false);
        vitamin_chart.setTouchEnabled(false);
        vitamin_chart.animateXY(1000, 1000);


        // 기타 영양소 막대그래프 설정 및 초기화
        BarData etcBarData = getBarDatas(etc);
        etc_chart.setData(etcBarData);
        XAxis xAxisEtc = etc_chart.getXAxis();
        ArrayList<String> etc_names = new ArrayList<>();
        Iterator<Nutrition> it2 = etc.iterator();
        while(it2.hasNext()){
            Nutrition nut = it2.next();
            if(nut.getAmount() > 1) {
                etc_names.add(nut.getNutrition());
                Log.i("etc", nut.getNutrition());
            }
        }
        // 막대 간격을 1로 설정
        xAxisEtc.setGranularity(1f);
        xAxisEtc.setValueFormatter(new IndexAxisValueFormatter(etc_names));
        xAxisEtc.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisEtc.setDrawGridLines(false);

        etc_chart.setFitBars(true);
        etc_chart.setScaleEnabled(false);
        etc_chart.setClickable(false);
        etc_chart.setTouchEnabled(false);
        etc_chart.animateXY(1000, 1000);

    }

    private BarData getBarDatas(ArrayList<Nutrition> nutritions){

        Iterator<Nutrition> it = nutritions.iterator();
        ArrayList<BarEntry> list = new ArrayList<>();
        String nut_type = "";
        if(nutritions.get(0).getType().equals("vitamin"))
            nut_type = "비타민";
        else
            nut_type="기타영양소";

        int cnt = 0;
        while(it.hasNext()){
            Nutrition temp = it.next();
            if((float)temp.getAmount() > 1.0f)
                list.add(new BarEntry((float)cnt++, (float)temp.getAmount()));
        }

        BarDataSet barDataSet = new BarDataSet(list, nut_type);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)value + "mg";
            }
        });

        BarData barData = new BarData(barDataSet);

        return barData;
    }
}
