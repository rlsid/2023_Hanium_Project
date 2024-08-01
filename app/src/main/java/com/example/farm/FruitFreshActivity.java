package com.example.farm;

import static com.example.farm.Fragment.CameraFragment.rotateImage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.farm.Connection.AISocket;
import com.example.farm.Connection.SearchTask;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FruitFreshActivity extends AppCompatActivity {
    private ImageView fruit_image;
    private TextView fruit_name, fruit_fresh, fruit_maturity, maturity_tv, maturity_tv2;
    private HorizontalBarChart fresh_graph, matuity_graph;
    private ImageButton back_btn, info_btn;
    private ShimmerFrameLayout shimmerFrameLayout1, shimmerFrameLayout2;
//    private Button ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fruitfresh_layout);

        Intent intent = getIntent();
        String info = intent.getStringExtra("freshInfo");
        String[] temp = info.split(" ");
        String f_name = temp[1];
        String fresh_grade = temp[2];
        float fresh_num = Math.round((Float.parseFloat(temp[3]) * 100));

        fruit_image = findViewById(R.id.fruit_img);
        fruit_name = findViewById(R.id.fruit_name);
        fruit_fresh = findViewById(R.id.fruit_fresh);
        fresh_graph = findViewById(R.id.fresh_graph);
        back_btn = findViewById(R.id.back_btn);
        info_btn = findViewById(R.id.info_btn);
        fruit_maturity = findViewById(R.id.fruit_maturity);
        shimmerFrameLayout1 = findViewById(R.id.simmer_layout1);
        maturity_tv = findViewById(R.id.maturity_tv);
        matuity_graph = findViewById(R.id.matuiry_graph);
        shimmerFrameLayout2 = findViewById(R.id.shimmer_layout2);
        maturity_tv2 = findViewById(R.id.maturity_tv2);

        shimmerFrameLayout1.startShimmer();
        shimmerFrameLayout2.startShimmer();

        fruit_maturity.setText("00");

        Bitmap photo = null;

        // Intent로부터 Image의 URI를 받아 Bitmap으로 변환한다.
        Uri imageURI = Uri.parse(intent.getStringExtra("imageURI"));
        try {
            photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imageURI.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("Image URI : ", imageURI.toString());

        if(photo != null) {
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageURI.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(photo, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(photo, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(photo, 270);
                    break;
            }
//            fruit_image.setImageBitmap(((BitmapDrawable)getDrawable(R.drawable.koreamelon3)).getBitmap());
            fruit_image.setImageBitmap(rotatedBitmap);

            // photo를 AI서버에 전달하여 Socket통신으로 결과값을 받는다.
            SocketTask task = new SocketTask();
            try{
                List<String> result = task.execute(rotatedBitmap).get();
                //---------------------------------------------------------------------------- case 1 : 객체가 1개 인식되는 경우
                if(result.size() == 1) {
                    setMaturityChart("17");
                    fruit_name.setText(result.get(0));

                    switch(fresh_grade){
                        case "normal":
                            fruit_fresh.setText("상태 : 보통");
                        case "rotten":
                            fruit_fresh.setText("상태 : 썩음");
                        case "fresh":
                            fruit_fresh.setText("상태 : 신선함");
                    }

                    back_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    // 신선도 그래프 설정
                    fresh_graph.setDrawBarShadow(true);
                    fresh_graph.setScaleEnabled(false);
                    fresh_graph.setClickable(false);

                    ArrayList<Float> li = new ArrayList<>();
                    li.add(fresh_num);
                    BarDataSet dataSet = getBarDataSet(li);
                    dataSet.setColor(Color.rgb(147, 247, 250));
                    BarData data = new BarData(dataSet);
                    fresh_graph.setData(data);
                    fresh_graph.setDrawValueAboveBar(false);

                    XAxis xAxis = fresh_graph.getXAxis();
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setEnabled(false);

                    YAxis yLeft = fresh_graph.getAxisLeft();
                    yLeft.setAxisMinimum(0f);
                    yLeft.setAxisMaximum(100f);
                    yLeft.setEnabled(false);

                    YAxis yRight = fresh_graph.getAxisRight();
                    yRight.setDrawGridLines(false);
                    yRight.setDrawAxisLine(true);
                    yRight.setEnabled(false);
                    yRight.setDrawLabels(false);

                    fresh_graph.setTouchEnabled(false);
                    fresh_graph.animateY(1000);

                    info_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), FruitInformationActivity.class);
                            SearchTask task = new SearchTask();
                            Fruit fruit;
                            try {
                                fruit = task.execute(f_name).get();
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            intent.putExtra("info", fruit);
                            startActivity(intent);
                        }
                    });
                //---------------------------------------------------------------------------- case 2 : 객체가 1개 이상 인식되는 경우
                }else if(result.size() > 1){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(FruitFreshActivity.this);
                    dialog.setIcon(R.drawable.logo).setMessage("하나의 과일만 촬영해주세요.").setPositiveButton("다시 촬영하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
                //---------------------------------------------------------------------------- case 3 : 객체가 인식되지 않는 경우
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(FruitFreshActivity.this);
                    dialog.setIcon(R.drawable.logo).setMessage("과일이 인식되지 않았습니다.").setPositiveButton("다시 촬영하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
                }
//            task.execute(((BitmapDrawable)getDrawable(R.drawable.koreamelon3)).getBitmap());
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    private BarDataSet getBarDataSet(ArrayList<Float> data){
        ArrayList<BarEntry> list = new ArrayList<>();

        list.add(new BarEntry(0f, data.get(0)));
        BarDataSet dataSet = new BarDataSet(list, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setLabel("신선도");
        dataSet.setValueTextSize(10f);
        dataSet.setDrawIcons(false);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)value + "%";
            }
        });
        return dataSet;
    }

    private class SocketTask extends AsyncTask<Bitmap, Void, List<String>>{
        @Override
        protected List<String> doInBackground(Bitmap... bitmaps) {
            AISocket socket = new AISocket();
            List<String> result = socket.communication(bitmaps[0]);
            Log.i("Activity Result : ", new Gson().toJson(result));
            return result;
        }

        // 스레드의 반환값을 이용하여 수행
        @Override
        protected void onPostExecute(List<String> s) {
//            // 성숙도 textView 제어
//            shimmerFrameLayout1.stopShimmer();
//            maturity_tv.setVisibility(View.INVISIBLE);
//            fruit_maturity.setVisibility(View.VISIBLE);
//            shimmerFrameLayout1.setVisibility(View.INVISIBLE);
//            fruit_maturity.setText("성숙도 : " + s + "%");
//
//            // 성숙도 Barchart제어
//            shimmerFrameLayout2.stopShimmer();
//            maturity_tv2.setVisibility(View.INVISIBLE);
//            shimmerFrameLayout2.setVisibility(View.INVISIBLE);
//            matuity_graph.setVisibility(View.VISIBLE);
//
//            matuity_graph.setDrawBarShadow(true);
//            matuity_graph.setScaleEnabled(false);
//            matuity_graph.setClickable(false);
//            matuity_graph.setTouchEnabled(false);
//
//            ArrayList<Float> li = new ArrayList<>();
////            li.add(Float.parseFloat(s));
//            BarDataSet dataSet = getBarDataSet(li);
//            dataSet.setColors(Color.rgb(248, 236, 135));
//            BarData data = new BarData(dataSet);
//            matuity_graph.setData(data);
//            matuity_graph.setDrawValueAboveBar(false);
//
//            XAxis xAxis = matuity_graph.getXAxis();
//            xAxis.setDrawGridLines(false);
//            xAxis.setDrawAxisLine(false);
//            xAxis.setEnabled(false);
//
//            YAxis yLeft = matuity_graph.getAxisLeft();
//            yLeft.setAxisMinimum(0f);
//            yLeft.setAxisMaximum(100f);
//            yLeft.setEnabled(false);
//
//            YAxis yRight = matuity_graph.getAxisRight();
//            yRight.setDrawGridLines(false);
//            yRight.setDrawAxisLine(true);
//            yRight.setEnabled(false);
//
//            matuity_graph.animateY(1000);
        }
    }

    public void setMaturityChart(String num){
        // 성숙도 textView 제어
        shimmerFrameLayout1.stopShimmer();
        maturity_tv.setVisibility(View.INVISIBLE);
        fruit_maturity.setVisibility(View.VISIBLE);
        shimmerFrameLayout1.setVisibility(View.INVISIBLE);
        fruit_maturity.setText("성숙도 : " + num + "%");

        // 성숙도 Barchart제어
        shimmerFrameLayout2.stopShimmer();
        maturity_tv2.setVisibility(View.INVISIBLE);
        shimmerFrameLayout2.setVisibility(View.INVISIBLE);
        matuity_graph.setVisibility(View.VISIBLE);

        matuity_graph.setDrawBarShadow(true);
        matuity_graph.setScaleEnabled(false);
        matuity_graph.setClickable(false);
        matuity_graph.setTouchEnabled(false);

        ArrayList<Float> li = new ArrayList<>();
        li.add(Float.parseFloat(num));
        BarDataSet dataSet = getBarDataSet(li);
        dataSet.setColors(Color.rgb(248, 236, 135));
        BarData data = new BarData(dataSet);
        matuity_graph.setData(data);
        matuity_graph.setDrawValueAboveBar(false);

        XAxis xAxis = matuity_graph.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);

        YAxis yLeft = matuity_graph.getAxisLeft();
        yLeft.setAxisMinimum(0f);
        yLeft.setAxisMaximum(100f);
        yLeft.setEnabled(false);

        YAxis yRight = matuity_graph.getAxisRight();
        yRight.setDrawGridLines(false);
        yRight.setDrawAxisLine(true);
        yRight.setEnabled(false);

        matuity_graph.animateY(1000);
    }

}
