package com.example.farm;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpUrl {

    private String url;

    public HttpUrl(){
        this.url = "http://54.180.113.188:8082/";
    }
//
//    public HttpUrl(){
//        this.url = "http://192.168.35.73:8082/";
//    }

    public String getUrl(){
        return url;
    }

    public String[] getEncodeingURLParam(String[] params){

        for(int i = 0; i < params.length; i++){
            try{
                params[i] = URLEncoder.encode(params[i], StandardCharsets.UTF_8.toString());
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return params;
    }

    public String getEncodeURLParam(String param){
        try{
            param = URLEncoder.encode(param, StandardCharsets.UTF_8.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return param;
    }
}
