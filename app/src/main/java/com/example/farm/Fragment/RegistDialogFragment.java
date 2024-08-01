package com.example.farm.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.farm.Connection.HttpConnection;
import com.example.farm.HttpUrl;
import com.example.farm.R;
import com.example.farm.Review;
import com.example.farm.Session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class RegistDialogFragment extends DialogFragment {

    View view;
    ImageButton close_btn, image_button;
    ImageView image;
    Button regist_btn;
    EditText flavor, content;
    Bitmap set_image;
    String fruit_name;
    Spinner choose_fruit;
    public RegistDialogFragment(String fruit_name){
        this.fruit_name = fruit_name;
    }

    public RegistDialogFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.regist_review, container);
        close_btn = view.findViewById(R.id.close_btn);
        regist_btn = view.findViewById(R.id.regist_btn);
        flavor = view.findViewById(R.id.flavor);
        content = view.findViewById(R.id.content);
        image_button = view.findViewById(R.id.image_button);
        image = view.findViewById(R.id.image_view);
        choose_fruit = view.findViewById(R.id.choose_btn);

        String session_id = ((Session)getActivity().getApplication()).getSessionId();

        // Choose_fruit(Spinner)에 과일 이름들의 데이터를 저장하기 위한 adapter생성
        ChooseTask chooseTask = new ChooseTask();
        try {
            ArrayList<String> names = chooseTask.execute().get();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item , names);
            choose_fruit.setAdapter(adapter);
        }catch(Exception e){
            e.printStackTrace();
        }

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
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String flavor_content = flavor.getText().toString();
                String body_content = content.getText().toString();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                String current = format.format(new Date());
                fruit_name = choose_fruit.getSelectedItem().toString();
                Review review = new Review(fruit_name, current, session_id, body_content, flavor_content, "", "");


                // 게시글 정보 텍스트 입력 데이터 서버로 전송
                try {
                    if(review != null) {
                        String message = content.getText().toString();
                        if (message.length() > 0) { // 리뷰 작성 폼에 내용을 입력한 경우만 서버에 리뷰 데이터 전송
                            RegistTask task = new RegistTask();
                            String result = task.execute(set_image, review).get(); // review객체를 서버에 전달 및 결과 반환
                            Log.i("Result Request : ", result);
                            if (result != null) { // 서버로부터 응답값을 받은 경우
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                                if (result.equals("true")) { // 게시 성공
                                    dialog.setTitle("리뷰 작성").setMessage("리뷰 작성에 성공하였습니다.").setIcon(R.drawable.logo)
                                            .setPositiveButton("확인", null).show();
                                    dismiss();
                                } else { // 게시 요청했는데 서버에서 값을 못받아서 false를 반환한 경우
                                    dialog.setTitle("리뷰 작성").setMessage("리뷰 작성에 실패하였습니다.(네트워크 요청 오류)").setIcon(R.drawable.logo)
                                            .setPositiveButton("확인", null).show();
                                }
                            } else { // 서버로부터 응답값을 받지 못한 경우
                                Toast.makeText(getContext(), "리뷰 작성에 실패하였습니다.(네트워크 응답 오류)", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "내용을 입력해주세요!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    private void sendResult(String newData){
        if(getTargetFragment() == null)
            return;
        Intent intent = new Intent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){ // 갤러리로부터 사진을 선택하면 실행되는 메소드
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                try{
                    Uri imageUri = data.getData(); // 선택된 사진으로부터 uri를 반환받는다.
                    Log.i("imageURI : ", imageUri.toString());
                    if(imageUri != null){ // 받은 URI가 NULL이 아니라면
                        // image URI에 대한 InputStream을 연다
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        // InputStream으로 받은 이미지를 Bitmap으로 변환한다.
                        Bitmap img = BitmapFactory.decodeStream(inputStream);

                        inputStream.close();
                        image.setImageBitmap(img);
                        image_button.setVisibility(View.INVISIBLE);
                        image.setVisibility(View.VISIBLE);
                        set_image = img;
                    }else{
                        Log.e("Image URI Null", "need uri");
                    }
                }catch(Exception e){
                    Log.e("갤러리 불러오기 오류", "");
                    e.printStackTrace();
                }
            }
        }
    }

    // 게시글 작성 데이터 서버로 전송
    public class RegistTask extends AsyncTask<Object, Void, String>{
        @Override
        protected String doInBackground(Object... args){
            HttpUrl url = new HttpUrl();
            String boundary = "****";
            HttpConnection conn = new HttpConnection(url.getUrl() + "regist");
            conn.setHeader(1000, "POST", true, true);
            conn.setProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream dos = conn.getDataOutputStream();
            if(args[0] instanceof Bitmap && args[0] != null) { // 이미지 전송
                Bitmap image = (Bitmap) args[0];
                Review review = (Review) args[1];
                String imageName = review.getFruit_name() + "_" + review.getUser_id() + "_" + review.getReview_time() + ".jpg";
                // 이미지 파일 이름을 UTF-8로 인코딩 후 Base64로 인코딩
                String encodedImageName = Base64.encodeToString(imageName.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

                try {
                    dos.writeBytes("--" + boundary + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + encodedImageName + "\"\r\n");
                    dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                    ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
                    byte[] imageByteArray = imageStream.toByteArray();
                    dos.write(imageByteArray);
                    dos.writeBytes("\r\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(args[1] instanceof Review){
                Review review = (Review)args[1];
                try {
                    dos.writeBytes("--" + boundary + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"review\"\r\n\r\n");
                    Gson gson = new Gson();
                    String content = gson.toJson(review);
                    byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                    Log.i("Content : ", content);
                    dos.write(contentBytes);
                    dos.writeBytes("\r\n");
                    dos.writeBytes("--" + boundary + "--\r\n");
                    dos.flush();
                    dos.close();
                }catch(IOException e){
                    e.printStackTrace();
                }

            }

            String result = conn.readData();
            return result;
        }
    }

    public static class ChooseTask extends AsyncTask<Void, Void, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "fruitnames");
            conn.setHeader(1000, "GET", false, true);

            String result = conn.readData();
            Gson gson = new Gson();
            ArrayList<String> list = gson.fromJson(result, new TypeToken<ArrayList<String>>() {}.getType());
            return list;
        }
    }

}
