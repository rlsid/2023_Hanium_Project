package com.example.farm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farm.Connection.HttpConnection;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    Session session;
    Button join;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.loginBtn);
        EditText idText = findViewById(R.id.id);
        EditText pwText = findViewById(R.id.pw);
        Intent intent = new Intent(this, MainActivity.class); // mainActivity로 이동하기 위한 화면전환 intent
        join = findViewById(R.id.join);
        session = (Session)getApplication();
        loginBtn.setOnClickListener(new View.OnClickListener() { // 로그인 버튼 클릭 시 작동하는 이벤트
            @Override
            public void onClick(View v) {
                LoginTask activity = new LoginTask(); // 아래 정의한 LoginTask클래스를 이용하여 Server와 HTTP통신
                String id = idText.getText().toString(); // 입력한 id를 editText로부터 받아온다.
                // 아이디와 비밀번호 입력여부 확인 후 입력하지 않았으면 ToastMessage로 알림
                if(id.length() == 0) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String pw = pwText.getText().toString();
                if(pw.length() == 0){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String result = null;
                try {
                    // LoginTask클래스의 doInBackground메소드를 execute메소드를 이용하여 실행하여 get메소드를 통해 리턴값을 받는다.
                    // result : true or false(로그인 결과)
                    result = activity.execute(id, pw).get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Log.i("main : ", result);
                // 로그인 성공 시 Session 설정 및 Main화면으로 넘어가는 intent실행
                if(result.equals("true")){
                    session.setSessionId(id);
                    startActivity(intent);
                    Log.i("LoginSession2", session.getSessionId());
                }else{
                    Toast.makeText(LoginActivity.this, "아이디 혹은 비밀번호를 확인하세요", Toast.LENGTH_LONG).show();
                }
            }
        });
        join.setOnClickListener(new View.OnClickListener(){ // 회원가입버튼 클릭 시 회원가입 화면으로 넘어가는 intent실행
            @Override
            public void onClick(View view){
                Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(joinIntent);
            }
        });
    }

    // server와 http통신을 위한 백그라운드 환경 구현
    public static class LoginTask extends AsyncTask<String, Void, String> {
        String result;
        PasswordHash hash = new PasswordHash("SHA-256");

        @Override
        protected String doInBackground(String... arg){
            try{
                String id = arg[0];
                String pw = arg[1];
                HttpUrl url = new HttpUrl();
                HttpConnection conn = new HttpConnection(url.getUrl() + "login");
                conn.setHeader(1000, "POST", true, true);
                pw = hash.passwordHashing(pw);
                Log.i("hashPw : ", pw);
                String message = String.format("%s %s", id, pw);
                conn.writeData(message);
                Log.i("Login Info : ", id + pw);
                result = conn.readData();
                Log.i("message", result);
            }catch(Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }

}
