package com.example.farm.Connection;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Server와 HTTP통신을 위한 클래스
// 이거는 AsyncTask상속받아서 doInBackground메소드 오버라이드 해서 HTTP통신 해야 함
// 무조건 백그라운드로 실행해야 오류 안남
// 이 클래스 사용법
// 1. 생성자로 Server url설정
// 2. setHeader를 통해 Header설정
// 3. readData or writeData메소드를 통해 서버와 데이터 주고받음
public class HttpConnection {

    private String link;
    private HttpURLConnection conn;
//    private OutputStream outputStream;
//    private BufferedReader reader;

    // url을 생성 시 입력받아 connection을 생성
    public HttpConnection(String link){
        this.link = link;
        try{
            URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setHeader(int time, String method, boolean output, boolean input) {
        try {
            if (conn == null) {
                throw new IllegalStateException("Connection is not initialized. Make sure to provide a valid URL in the constructor.");
            }
            conn.setConnectTimeout(time);
            conn.setRequestProperty("Content-Type", "UTF-8");
            conn.setRequestMethod(method);
            conn.setDoInput(input);
            conn.setDoOutput(output);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    // Header를 설정한 Connection을 통해 Server로부터 데이터를 읽어온다.
    public String readData(){
        String result = "";
        try{
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }

    // Header를 설정한 Connection을 통해 Server로 데이터를 보낸다.
    public boolean writeData(String data){
        try{
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean writeData(byte[] image){
        try{
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(image);
            outputStream.flush();
        }catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    public DataOutputStream getDataOutputStream(){
        DataOutputStream dataOutputStream = null;
        try{
            OutputStream outputStream = conn.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.flush();
            outputStream.flush();
        }catch(IOException e){
            e.printStackTrace();
        }

        return dataOutputStream;
    }

    public void closeAll(){
        if(conn != null)
            conn.disconnect();
    }

    public void connect() {
        try {
            conn.connect();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setProperty(String key, String value){
        conn.setRequestProperty(key, value);
    }


}
