package com.example.farm.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.farm.Connection.HttpConnection;
import com.example.farm.HttpUrl;
import com.example.farm.R;
import com.example.farm.Review;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

public class RegistDialog extends Dialog {
    ImageButton close_btn, image_button;
    ImageView image;
    Button regist_btn;
    EditText flavor, content;

    // context를 받아 Dialog객체 생성
    public RegistDialog(@NonNull Context context, String session_id) {
        super(context);
        setContentView(R.layout.regist_review);
        close_btn = findViewById(R.id.close_btn);
        regist_btn = findViewById(R.id.regist_btn);
        flavor = findViewById(R.id.flavor);
        content = findViewById(R.id.content);
        image_button = findViewById(R.id.image_button);
        image = findViewById(R.id.image_view);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 갤러리에서 사진 여러개를 선택하기 위한 코드
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                getOwnerActivity().startActivityForResult(intent, 1);
            }
        });

        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flavor_content = flavor.getText().toString();
                String body_content = content.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
                Review review = new Review("참외", format.toString(), session_id, body_content, flavor_content, "", "");
                String[] args = {"참외", format.toString(), session_id, body_content, flavor_content};

                try {
                    if(review != null){
                        CommunityTask task = new CommunityTask();
                        String result = task.execute(review).get(); // review객체를 서버에 전달 및 결과 반환
                        Log.i("Result Request : ", result);
                        if(result != null) { // 서버로부터 응답값을 받은 경우
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            if (result.equals("true")) { // 게시 성공
                                dialog.setTitle("리뷰 작성").setMessage("리뷰 작성에 성공하였습니다.").setIcon(R.drawable.logo)
                                        .setPositiveButton("확인", null).show();
                                dismiss();
                            } else { // 게시 요청했는데 서버에서 값을 못받아서 false를 반환한 경우
                                dialog.setTitle("리뷰 작성").setMessage("리뷰 작성에 실패하였습니다.(네트워크 요청 오류)").setIcon(R.drawable.logo)
                                        .setPositiveButton("확인", null).show();
                            }
                        }else{ // 서버로부터 응답값을 받지 못한 경우
                            Toast.makeText(getContext(), "리뷰 작성에 실패하였습니다.(네트워크 응답 오류)", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Log.e("register Error ", "review Object is NULL");
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }


    public class CommunityTask extends AsyncTask<Review, Void, String>{

        @Override
        protected String doInBackground(Review... args) { // HttpUrlConnection에서 객체 전송이 안됨..인자로 초깃값 받아야 함
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "regist");
            conn.setHeader(1000, "POST", true, true);
            Gson gson = new Gson();
            String temp = gson.toJson(args[0]);
            conn.writeData(temp); // json형태로 값을 전달
            String result = conn.readData();

            return result;
        }
    }
}


