package com.example.farm.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.example.farm.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class FreshGageDialog extends Dialog {

    private ImageButton close_btn;
    private HorizontalBarChart fresh_chart;

    public FreshGageDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.freshgage_dialog);

        close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        fresh_chart = findViewById(R.id.fresh_chart);

        fresh_chart.setDrawValueAboveBar(false);
        XAxis xAxis = fresh_chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);

        YAxis yLeft = fresh_chart.getAxisLeft();
        yLeft.setAxisMinimum(0f);
        yLeft.setAxisMaximum(100f);
        yLeft.setEnabled(false);

        YAxis yRight = fresh_chart.getAxisRight();
        yRight.setDrawGridLines(false);
        yRight.setDrawAxisLine(true);
        yRight.setEnabled(false);

        fresh_chart.animateY(1000);
    }

    private BarDataSet getBarDataSet(ArrayList<Float> data){
        ArrayList<BarEntry> list = new ArrayList<>();

        list.add(new BarEntry(0f, data.get(0)));
        BarDataSet dataSet = new BarDataSet(list, "Grade");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(10f);
        return dataSet;
    }

    // 신선도 chart수정 메소드
    public void updateFreshChart(float[][] values){
        ArrayList<Float> list = new ArrayList<>();
        for(int i = 0; i < values[0].length; i++)
            list.add((Float)values[0][i]);

        BarData data = new BarData(getBarDataSet(list));
        fresh_chart.setData(data);
    }

    // 숙성도 chart수정 메소드
    public void updateFermentChart(){}


}
