package com.example.farm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;

public class FruitInfoActivity extends AppCompatActivity {
    TextView fruit_name;
    Button detail;
    TextView calories, carbohydrate, protein, fat, sugar;
    ImageView fruit_img;
    ImageButton back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fruit_information);

        Intent intent = getIntent();
        Fruit info = (Fruit)intent.getSerializableExtra("info");

        detail = findViewById(R.id.detail);
        fruit_name = findViewById(R.id.fruit_name);
        calories = findViewById(R.id.calories);
        carbohydrate = findViewById(R.id.carbohydrate);
        protein = findViewById(R.id.protein);
        fat = findViewById(R.id.fat);
        sugar = findViewById(R.id.sugar);
        fruit_img = findViewById(R.id.fruit_img);
        back_btn = findViewById(R.id.backBtn);

        fruit_name.setText(info.getFruit_name());
        calories.setText("칼로리 : " + info.getCalories() + " Kcal");
        carbohydrate.setText("탄수화물 : " + info.getCarbohydrate() + " g");
        protein.setText("단백질 : " + info.getProtein() + " g");
        fat.setText("지방 : " + info.getFat() + " g");
        sugar.setText("당 : " + info.getSugar() + " g");

        int img_resource = getResources().getIdentifier(info.getFile_name().toLowerCase(), "drawable", getPackageName());
        fruit_img.setImageResource(img_resource);
        if(info != null) {
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(getApplicationContext(), FruitDetailInfoActivity.class);
                    detailIntent.putExtra("info", info);
                    startActivity(detailIntent);
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "제공할 정보가 없습니다.", Toast.LENGTH_LONG).show();
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
