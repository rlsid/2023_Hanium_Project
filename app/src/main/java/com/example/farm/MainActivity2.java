package com.example.farm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.example.farm.Connection.HttpConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class MainActivity2 extends AppCompatActivity {

    ImageButton login;
    ImageButton camera;
    Session session;
    ImageButton user;
    SearchView search;
    GridLayout recommend;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login);
        camera = findViewById(R.id.Camera_Button);
        recommend = findViewById(R.id.fruit_list);
        session = new Session();
        // SearchView위젯 가져오기
        search = (SearchView) findViewById(R.id.searchFruit);
        // Session을 받아온다.
        Session se = (Session)getApplication();
        user = findViewById(R.id.userBtn);
        Log.i("session : ", se.getSessionId());

        // 현재 날짜(월) 정보를 받아와 제철과일 이름을 가져온다.
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("M");
        String month = format.format(calendar.getTime());
        Log.i("month : ", month);
        RecommendTask recommendTask = new RecommendTask();
        // 서버와 통신하여 제철과일 정보를 받아온다.
        try {
            ArrayList<String> fruit_list = recommendTask.execute(month).get();
            Iterator<String> it = fruit_list.iterator();
            if(fruit_list != null){ // 과일이 있다면
                while(it.hasNext()){
                    String[] temp = it.next().split(",");
                    TextView fruit = new TextView(this);
                    fruit.setText("* " + temp[0]);
                    fruit.setTextSize(17);
                    fruit.setTextColor(Color.BLACK);
                    recommend.addView(fruit);
                }
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(se.getSessionId().equals("default")){ // Session이 존재하지 않는경우(default) login버튼을 보이고 profile버튼을 없앤다.
            login.setVisibility(View.VISIBLE);
            user.setVisibility(View.INVISIBLE);
        }else{ // Session이 존재하는 경우 login버튼을 없애고 profile버튼을 생성한다.
            user.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
        }

        login.setOnClickListener(new View.OnClickListener() { // login버튼을 누를 시 loginActivity로 넘어감
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() { // cameraButton을 누를 시 카메라를 킴
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
            }
        });

        // 검색바 listener설정
        // SearchView위젯 Listener설정
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            // 검색버튼 눌렀을때 작동하는 메소드
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchTask task = new SearchTask();
                boolean result = false;
                try {
                    Fruit fruit = task.execute(query).get();
                    if(fruit != null) {
                        // intent로 정보 제공 layout으로 넘어감
                        Intent intent = new Intent(getApplication(), FruitInfoActivity.class);
                        intent.putExtra("info", fruit);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return false;
            }

            // 검색내용이 변할때 작동되는 메소드
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public static class SearchTask extends AsyncTask<String, Void, Fruit>{
        @Override
        protected Fruit doInBackground(String ... fruit) {
            HttpConnection conn = new HttpConnection("http://192.168.35.73:8081/search?fruit=" + fruit[0]);
            conn.setHeader(1000, "GET", false, true);
            // 과일 정보 받기 String형태를 object로 받기?
            String info = conn.readData();
            Gson gson = new Gson();
            Fruit f_info = gson.fromJson(info, Fruit.class);
            gson = new GsonBuilder().setPrettyPrinting().create();

            String temp = gson.toJson(f_info);
            Log.i("fruit : ", temp);
            return f_info;
        }
    }

    public static class RecommendTask extends AsyncTask<String, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            HttpConnection conn = new HttpConnection("http://192.168.35.73:8081/period?month=" + strings[0]);
            conn.setHeader(1000, "GET", false, true);
            String fruit_list = conn.readData();

            Log.i("fruit_list", fruit_list);

            Gson gson = new Gson();
            ArrayList<String> result = gson.fromJson(fruit_list, ArrayList.class);

            return result;
        }
    }

}