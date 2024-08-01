package com.example.farm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class FruitDetailInfoActivity extends AppCompatActivity {

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
    TextView fruit_name, effective1, effective2, effective3;
    GridView vitaminView, etcView;
    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fruit_efficacy);

        Intent intent = getIntent();
        Fruit fruit = (Fruit)intent.getSerializableExtra("info");
        fruit_name = findViewById(R.id.fruit_name);
        vitaminView = findViewById(R.id.vitamin);
        etcView = findViewById(R.id.etc);
        effective1 = findViewById(R.id.effeciency1);
        effective2 = findViewById(R.id.effeciency2);
        effective3 = findViewById(R.id.effeciency3);
        back_btn = findViewById(R.id.backBtn);

        fruit_name.setText(fruit.getFruit_name());
        // 효능 : type을 분류하고 그 안에서 가장 많은 3개를 분별하여 사용
        ArrayList<Nutrition> infos = fruit.getFruitInfo().getInfoList();
        ArrayList<Nutrition> vitamin = new ArrayList<Nutrition>();
        ArrayList<Nutrition> etc = new ArrayList<Nutrition>();
        Iterator<Nutrition> it = infos.iterator();

        // 함유 영양소 정보 가져오기
        // vitamin과 etc종류를 나누어 vitamin ArrayList와 etc ArrayList에 각각 추가한다.
        while(it.hasNext()){
            Nutrition temp = it.next();
            if(temp.getType().equals("vitamin"))
                vitamin.add(temp);
            else
                etc.add(temp);
        }
        // 아래 정의한 AdapterView를 이용하여 Adapter를 설정한다.
        VitaminAdapter vAdapter = new VitaminAdapter(this, vitamin);
        EtcAdapter eAdapter = new EtcAdapter(this, etc);
        vitaminView.setAdapter(vAdapter);
        etcView.setAdapter(eAdapter);

        // 효능 정보 가져오기
        ArrayList<Nutrition> result = getEffective(infos);

        effective1.setText(result.get(0).getNutrition() + " 성분에 의해 " + result.get(0).getEffect());
        effective2.setText(result.get(1).getNutrition() + " 성분에 의해 " + result.get(1).getEffect());
        effective3.setText(result.get(2).getNutrition() + " 성분에 의해 " + result.get(2).getEffect());

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // 함유 영양소에 포함된 비타민류, 그외 카테고리를 위한 GridView Adapter클래스 선언
    public class VitaminAdapter extends BaseAdapter{
        ArrayList<Nutrition> vitamin = new ArrayList<Nutrition>();
        Context context;

        public VitaminAdapter(Context context, ArrayList<Nutrition> vitamin){
            this.context = context;
            this.vitamin = vitamin;
        }
        @Override
        public int getCount() {
            return vitamin.size();
        }

        @Override
        public Object getItem(int position) {
            return vitamin.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 동적으로 TextView를 만들어 return하여 GridView에 추가한다.
            TextView text = new TextView(context);
            Nutrition nutrition = vitamin.get(position);
            text.setText(nutrition.getNutrition() + " : " + nutrition.getAmount() + " " + nutrition.getUnit());
            text.setTextSize(12f);
            text.setTextColor(Color.BLACK);
            text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) text.getLayoutParams();
            param.setMargins(10, 20, 0, 0);
            text.setLayoutParams(param);

            return text;
        }


    }


    public class EtcAdapter extends BaseAdapter {
        ArrayList<Nutrition> etc = new ArrayList<Nutrition>();
        Context context;

        public EtcAdapter(Context context, ArrayList<Nutrition> vitamin) {
            this.context = context;
            this.etc = vitamin;
        }

        @Override
        public int getCount() {
            return etc.size();
        }

        @Override
        public Object getItem(int position) {
            return etc.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 동적으로 TextView를 만들어 return하여 GridView에 추가한다.
            TextView text = new TextView(context);
            Nutrition nutrition = etc.get(position);
            text.setText(nutrition.getNutrition() + " : " + nutrition.getAmount() + " " + nutrition.getUnit());
            text.setTextSize(12f);
            text.setTextColor(Color.BLACK);
            text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) text.getLayoutParams();
            param.setMargins(10, 20, 0, 0);
            text.setLayoutParams(param);

            return text;
        }
    }

}
