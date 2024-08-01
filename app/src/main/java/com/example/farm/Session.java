package com.example.farm;

import android.app.Application;

// Session을 저장하기 위한 클래스로 Application을 상속받아 어플리케이션의 전역변수로 저장할 수 있음
// 어떠한 클래스던지 동일하게 적용됨
public class Session extends Application {
    private String id;

    // 세션이 존재하지 않는 경우 default로 설정됨
    public Session(){
        this.id = "default";
    }

    // session설정
    public void setSessionId(String id){
        this.id = id;
    }

    // session을 받아옴
    public String getSessionId(){
        return id;
    }
}
