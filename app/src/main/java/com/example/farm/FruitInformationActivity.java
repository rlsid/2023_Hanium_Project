package com.example.farm;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farm.Dialog.NutritionDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FruitInformationActivity extends AppCompatActivity {

    private TextView f_name;
    TextView fruit_name, effective1, effective2, effective3, nutrition1, nutrition2, nutrition3, slide_tv;
    ImageButton back_btn, chart_btn, fruit_cut_img;
    private PieChart pie_chart;
    private ImageView fruit_img;
    private Button nutrition_btn;
    private ImageButton ar_btn;
    private SlidingUpPanelLayout slide;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fruit_information_layout);

        // 과일 정보 받기
        Fruit fruit = (Fruit) getIntent().getSerializableExtra("info");
        f_name = findViewById(R.id.f_name);
        pie_chart = findViewById(R.id.pie_chart);
        fruit_img = findViewById(R.id.fruit_img);
        ar_btn = findViewById(R.id.ar_Btn);
        chart_btn = findViewById(R.id.chart_btn);
        nutrition1 = findViewById(R.id.nutrition_name1);
        nutrition2 = findViewById(R.id.nutrition_name2);
        nutrition3 = findViewById(R.id.nutrition_name3);
        slide = findViewById(R.id.slide);
        fruit_cut_img = findViewById(R.id.fruit_cut_img);

        int img_resource = getResources().getIdentifier(fruit.getFile_name().toLowerCase(), "drawable", getPackageName());
        fruit_img.setImageResource(img_resource);
        f_name.setText(fruit.getFruit_name());

        ArrayList<PieEntry> list = new ArrayList<>();
        if(Float.parseFloat(fruit.getCarbohydrate()) >= 1f)
            list.add(new PieEntry(Float.parseFloat(fruit.getCarbohydrate()), "탄수화물"));
        if(Float.parseFloat(fruit.getProtein()) >= 1f)
            list.add(new PieEntry(Float.parseFloat(fruit.getProtein()), "단백질"));
        if(Float.parseFloat(fruit.getFat()) >= 1f)
            list.add(new PieEntry(Float.parseFloat(fruit.getFat()), "지방"));
        if(Float.parseFloat(fruit.getSugar()) >= 1f)
            list.add(new PieEntry(Float.parseFloat(fruit.getSugar()), "당"));

        PieDataSet pieDataSet = new PieDataSet(list, "기본 영양정보");
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)value + "mg";
            }
        });
        pieDataSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDataSet);

        pie_chart.setData(pieData);
        pie_chart.setCenterText("기본 영양정보");
        pie_chart.setEntryLabelColor(Color.BLACK);
        pie_chart.animateXY(1000, 1000);

        fruit_name = findViewById(R.id.fruit_name);
        effective1 = findViewById(R.id.effeciency1);
        effective2 = findViewById(R.id.effeciency2);
        effective3 = findViewById(R.id.effeciency3);
        back_btn = findViewById(R.id.backBtn);
        nutrition_btn = findViewById(R.id.nutrition_btn);
        slide_tv = findViewById(R.id.slide_tv);

        fruit_name.setVisibility(INVISIBLE);

        slide.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                fruit_name.setVisibility(VISIBLE);
                slide_tv.setVisibility(INVISIBLE);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                fruit_name.setVisibility(INVISIBLE);
                slide_tv.setVisibility(VISIBLE);
            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });

        fruit_name.setText(fruit.getFruit_name());
        // 효능 : type을 분류하고 그 안에서 가장 많은 3개를 분별하여 사용
        ArrayList<Nutrition> infos = fruit.getFruitInfo().getInfoList();
        ArrayList<Nutrition> vitamin = new ArrayList<Nutrition>();
        ArrayList<Nutrition> etc = new ArrayList<Nutrition>();
        Iterator<Nutrition> it = infos.iterator();

        nutrition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NutritionDialog dialog = new NutritionDialog(FruitInformationActivity.this, vitamin, etc);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                Window window = dialog.getWindow();
                window.setAttributes(lp);
                dialog.show();
            }
        });

        chart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NutritionDialog dialog = new NutritionDialog(FruitInformationActivity.this, vitamin, etc);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                Window window = dialog.getWindow();
                window.setAttributes(lp);
                dialog.show();
            }
        });


        // 함유 영양소 정보 가져오기
        // vitamin과 etc종류를 나누어 vitamin ArrayList와 etc ArrayList에 각각 추가한다.
        while(it.hasNext()){
            Nutrition temp = it.next();
            if(temp.getType().equals("vitamin"))
                vitamin.add(temp);
            else
                etc.add(temp);
        }

        // 효능 정보 가져오기
        ArrayList<Nutrition> result = getEffective(infos);

        nutrition1.setText(result.get(0).getNutrition());
        nutrition2.setText(result.get(1).getNutrition());
        nutrition3.setText(result.get(2).getNutrition());

        effective1.setText(result.get(0).getNutrition() + " 성분에 의해 " + result.get(0).getEffect());
        effective2.setText(result.get(1).getNutrition() + " 성분에 의해 " + result.get(1).getEffect());
        effective3.setText(result.get(2).getNutrition() + " 성분에 의해 " + result.get(2).getEffect());

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // AR화면을 볼 수 있는 버튼(과일 전체)
        ar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                HttpUrl url = new HttpUrl();
                String fruit_file = fruit.getFile_name();
                String serverPath = url.getUrl();
                Uri intentUri =
                        Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                                .appendQueryParameter("file", serverPath + "arwholeimage?fruit_name=" + fruit_file)
                                .appendQueryParameter("mode", "3D_preferred")
                                .build();
                sceneViewerIntent.setData(intentUri);
                sceneViewerIntent.setPackage("com.google.ar.core");
                startActivity(sceneViewerIntent);
            }
        });

        fruit_cut_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                HttpUrl url = new HttpUrl();
                String fruit_file = fruit.getFile_name();
                String serverPath = url.getUrl();
                Uri intentUri =
                        Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                                .appendQueryParameter("file", serverPath + "arimage?fruit_name=" + fruit_file)
                                .appendQueryParameter("mode", "3D_preferred")
                                .build();
                sceneViewerIntent.setData(intentUri);
                sceneViewerIntent.setPackage("com.google.ar.core");
                startActivity(sceneViewerIntent);
            }
        });
    }


    // 여러 단위(g, mg, ug)을 mg으로 변환하여 반환해주는 단위 변환기 (함유량을 정렬하기 위해 사용)
    public double unitConvertMg(Nutrition nutrition){
        double amount = -1;

        if(nutrition.getUnit().equals("g"))
            amount = nutrition.getAmount() * 1000;
        else if(nutrition.getUnit().equals("ug"))
            amount = nutrition.getAmount() / 1000;
        else
            amount = nutrition.getAmount();

        return amount;
    }

    // 과일의 성분 함유량 배열을 이용하여 정렬 후 가장 많이 포함된 영양소 3개에 대한 효능을 반환한다.
    public ArrayList<Nutrition> getEffective(ArrayList<Nutrition> nutrition){
        // ArrayList<Nutrition>자료형을 Nutrition[]배열로 변환하기 위한 작업
        Nutrition[] nutritions = new Nutrition[nutrition.size()];
        Iterator<Nutrition> it = nutrition.iterator();
        int i = 0;
        while(it.hasNext()){
            Nutrition temp = it.next();
            nutritions[i] = temp;
            i++;
        }
        String[] effective = new String[3];

        // 영양소 함유량에 따라 내림차순 정렬 Comparator을 이용
        Arrays.sort(nutritions, new Comparator<Nutrition>() {
            @Override
            public int compare(Nutrition o1, Nutrition o2) {
                return -Double.compare(unitConvertMg(o1), unitConvertMg(o2));
            }
        });

        Log.i("effect1", nutritions[0].getNutrition() + effective[0]);
        Log.i("effect2", nutritions[1].getNutrition() + effective[1]);
        Log.i("effect3", nutritions[2].getNutrition() + effective[2]);

        // 결과를 추가 후 반환
        ArrayList<Nutrition> result = new ArrayList<>();
        result.add(nutritions[0]);
        result.add(nutritions[1]);
        result.add(nutritions[2]);

        return result;
    }

}
