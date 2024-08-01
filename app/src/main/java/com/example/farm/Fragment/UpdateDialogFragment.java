package com.example.farm.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.fragment.app.DialogFragment;

import com.example.farm.Connection.HttpConnection;
import com.example.farm.HttpUrl;
import com.example.farm.R;
import com.example.farm.Review;
import com.example.farm.ReviewPath;
import com.example.farm.UpdateReview;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UpdateDialogFragment extends DialogFragment {

    View view;
    ImageButton change_btn, close_btn;
    ImageView fruit_img;
    Button update_btn;
    EditText flavor, content;
    Bitmap set_image;
    String fruit_name;
    Spinner choose_fruit;
    Review review;
    boolean is_changed;
    Bitmap image;
    String s_fileName;


    public UpdateDialogFragment(Bitmap image, Review review) {
        this.image = image;
        this.review = review;
    }

    public UpdateDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.update_review, container);
        change_btn = view.findViewById(R.id.change_btn);
        close_btn = view.findViewById(R.id.close_btn);
        fruit_img = view.findViewById(R.id.image_view);
        update_btn = view.findViewById(R.id.update_btn2);
        flavor = view.findViewById(R.id.flavor);
        content = view.findViewById(R.id.content);
        choose_fruit = view.findViewById(R.id.choose_btn);

        is_changed = false;

        fruit_img.setImageBitmap(image);
        flavor.setText(review.getFlavor());
        content.setText(review.getContent());

        RegistDialogFragment.ChooseTask chooseTask = new RegistDialogFragment.ChooseTask();
        try{
            ArrayList<String> list = chooseTask.execute().get();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, list);
            choose_fruit.setAdapter(adapter);
            choose_fruit.setSelection(list.indexOf(review.getFruit_name()));
        }catch(Exception e){
            e.printStackTrace();
        }

        String timeName = review.getReview_time().replace(" ", "-").replace(":", "-");
        Log.i("timeName", timeName);
        s_fileName = review.getFruit_name() + "_" + review.getUser_id() + "_" + timeName + ".jpg";

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAllTask allTask = new UpdateAllTask();
                UpdateBodyTask bodyTask = new UpdateBodyTask();
                String result = "false";
                String fruit_name = choose_fruit.getSelectedItem().toString();
                String review_time = review.getReview_time();
                String user_id = review.getUser_id();
                String up_content = content.getText().toString();
                String up_flavor = flavor.getText().toString();
                // 이미지가 수정되지 않은 경우
                if (is_changed == false) {
                    try {
                        result = bodyTask.execute(null, new Review(fruit_name, review_time, user_id, up_content, up_flavor, "", ""), s_fileName).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else { // 이미지가 수정된 경우
                    try {
                        result = allTask.execute(set_image, new Review(fruit_name, review_time, user_id, up_content, up_flavor, "", ""), s_fileName).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(result.equals("true")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext()).setTitle("알림").setMessage("수정을 완료하였습니다.").setIcon(R.drawable.logo)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                    getActivity().finish();
                                    startActivity(new Intent(getActivity(), UpdateReview.class));
                                }
                            });
                    dialog.show();

                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext()).setTitle("알림").setMessage("수정에 실패하였습니다.").setIcon(R.drawable.logo)
                            .setPositiveButton("확인", null);
                    dialog.show();
                }
            }
        });

        return view;
    }

    // 이미지를 갤러리에서 다시 선택한 경우
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리로부터 사진을 선택하면 실행되는 메소드
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData(); // 선택된 사진으로부터 uri를 반환받는다.
                    Log.i("imageURI : ", imageUri.toString());
                    if (imageUri != null) { // 받은 URI가 NULL이 아니라면
                        // image URI에 대한 InputStream을 연다
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        // InputStream으로 받은 이미지를 Bitmap으로 변환한다.
                        Bitmap img = BitmapFactory.decodeStream(inputStream);

                        inputStream.close();
                        fruit_img.setImageBitmap(img);

                        // 서버로 이미지 파일 데이터를 전송하기 위해 저장
                        set_image = img;
                        is_changed = true;
                    } else {
                        Log.e("Image URI Null", "need uri");
                    }

                } catch (Exception e) {
                    Log.e("갤러리 불러오기 오류", "");
                    e.printStackTrace();
                }
            }
        }
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


    // 인자는 3개 1. 이미지 데이터(수정된 경우만), 2. Review정보, 3. 수정 전 이미지파일 이름(수정된 경우만)
    // 이미지가 수정된 경우의 Task class
    public class UpdateAllTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... args) {
            String result = "false";
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "updateimage");
            String boundary = "****";
            conn.setHeader(1000, "PUT", true, true);
            conn.setProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream dos = conn.getDataOutputStream();

            if (args[0] instanceof Bitmap && args[0] != null) { // 이미지 전송
                Bitmap image = (Bitmap) args[0];
                Review update_review = (Review) args[1];
                String fileName = (String) args[2];

                String timeName = review.getReview_time().replace(" ", "-").replace(":", "-");
                String giveFileName = choose_fruit.getSelectedItem().toString() + "_" + review.getUser_id() + "_" + timeName + ".jpg";
                String encodedImageName = Base64.encodeToString(giveFileName.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);

                try{
                    dos.writeBytes("--" + boundary + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + encodedImageName + "\"\r\n");
                    dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                    ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
                    byte[] imageByteArray = imageStream.toByteArray();
                    dos.write(imageByteArray);
                    dos.writeBytes("\r\n");
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{ // 수정 정보 전달
                    dos.writeBytes("--" + boundary + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"review\"\r\n\r\n");
                    Gson gson = new Gson();
                    String content = gson.toJson(update_review);
                    Log.i("content : ", content);
                    byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                    Log.i("Content : ", content);
                    dos.write(contentBytes);
                    dos.writeBytes("\r\n");
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{ // 이전 파일 이름 전달
                    dos.writeBytes("--" + boundary + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"fileName\"\r\n\r\n");
                    dos.write(fileName.getBytes(StandardCharsets.UTF_8));
                    dos.writeBytes("\r\n");
                    dos.writeBytes("--" + boundary + "--\r\n");
                    dos.flush();
                    dos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

                result = conn.readData();

            }
            return result;
        }

    }
    public class UpdateBodyTask extends AsyncTask<Object, Void, String> {
        // 이미지는 수정하지 않고 본문만 수정한 경우

        @Override
        protected String doInBackground(Object... args) {
            Review new_review = (Review)args[1];
            String fileName = (String)args[2];
            ReviewPath reviewPath = new ReviewPath(new_review, fileName);
            String result = "false";
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "updatebody");
            conn.setHeader(1000, "PUT", true, true);
            conn.setProperty("Content-type", "application/json");
            conn.setProperty("Accept", "application/json");

            // 게시글 정보를 Review객체에 담아 Json형식으로 서버에 전달
            Gson gson = new Gson();
            String data = gson.toJson(reviewPath);
            Log.i("content2 : ", data);
            byte[] postData = data.getBytes();

            Log.i("fileName ", fileName);
            conn.setProperty("Content-Length", String.valueOf(postData.length));
            conn.writeData(postData);

            result = conn.readData();

            return result;
        }
    }
}
