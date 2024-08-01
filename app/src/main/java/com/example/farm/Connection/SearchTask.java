package com.example.farm.Connection;

import android.os.AsyncTask;
import android.util.Log;
import com.example.farm.Fruit;
import com.example.farm.HttpUrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SearchTask extends AsyncTask<String, Void, Fruit> {

    @Override
    protected Fruit doInBackground(String ... fruit) {
        HttpUrl url = new HttpUrl();
        HttpConnection conn = new HttpConnection(url.getUrl() + "search?fruit=" + fruit[0]);
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