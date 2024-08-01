package com.example.farm.Fragment;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farm.Dialog.CustomDialog;
import com.example.farm.Connection.HttpConnection;
import com.example.farm.Dialog.InstructionDialog;
import com.example.farm.HttpUrl;
import com.example.farm.LoginActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.Session;
import com.example.farm.UpdateReview;

public class MyInfoFragment extends Fragment {

    private View view;
    private Button login_btn, guide_btn, guide_btn2, logout_btn, category_btn, delete_btn, review_btn;
    private LinearLayout login_ll, info_ll, fruit_setting_ll, mypost_ll, info_ll2;
    private ImageView user_img;
    private TextView name_tv, user_id;
    private ImageButton login_img, setting_img, mypost_img;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myinfo_fragment, container, false);

        login_btn = view.findViewById(R.id.login_btn);
        guide_btn = view.findViewById(R.id.guide_btn1);
        guide_btn2 = view.findViewById(R.id.guide_btn2);
        category_btn = view.findViewById(R.id.category_btn);
        logout_btn = view.findViewById(R.id.logout_btn);
        delete_btn = view.findViewById(R.id.delete_user);
        review_btn = view.findViewById(R.id.review_btn);
        login_ll = view.findViewById(R.id.login_ll);
        info_ll = view.findViewById(R.id.info_ll);
        user_img = view.findViewById(R.id.user_img);
        user_id = view.findViewById(R.id.user_id);
        name_tv = view.findViewById(R.id.user_tv);
        fruit_setting_ll = view.findViewById(R.id.fruit_setting_ll);
        mypost_ll = view.findViewById(R.id.mypost_ll);
        info_ll2 = view.findViewById(R.id.info_ll2);
        login_img = view.findViewById(R.id.login_img);
        setting_img = view.findViewById(R.id.setting_img);
        mypost_img = view.findViewById(R.id.mypost_img);


        Session session = (Session)((MainActivity)getActivity()).getApplication();
        String sessionId = session.getSessionId();

        // 로그인 하지 않은 경우
        if(session.getSessionId().equals("default")) {
            login_ll.setVisibility(VISIBLE);
            info_ll.setVisibility(VISIBLE);
            user_id.setVisibility(INVISIBLE);
            name_tv.setVisibility(INVISIBLE);
            user_img.setVisibility(INVISIBLE);
            fruit_setting_ll.setVisibility(INVISIBLE);
            mypost_ll.setVisibility(INVISIBLE);
            info_ll2.setVisibility(INVISIBLE);
            login_btn.setVisibility(VISIBLE);
            category_btn.setVisibility(INVISIBLE);
            logout_btn.setVisibility(INVISIBLE);
            delete_btn.setVisibility(INVISIBLE);
            review_btn.setVisibility(INVISIBLE);

            // 로그인 버튼 및 이미지버튼 listener
            login_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            // 로그인 버튼 및 이미지 버튼 listener끝

            // 이용안내
            guide_btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    instruction_show();
                }
            });

        }else { // 로그인 한 경우
            login_ll.setVisibility(INVISIBLE);
            info_ll.setVisibility(INVISIBLE);
            user_id.setVisibility(VISIBLE);
            name_tv.setVisibility(VISIBLE);
            user_img.setVisibility(VISIBLE);
            fruit_setting_ll.setVisibility(VISIBLE);
            mypost_ll.setVisibility(VISIBLE);
            info_ll2.setVisibility(VISIBLE);
            login_btn.setVisibility(INVISIBLE);
            category_btn.setVisibility(VISIBLE);
            logout_btn.setVisibility(VISIBLE);
            delete_btn.setVisibility(VISIBLE);
            review_btn.setVisibility(VISIBLE);
            user_id.setText(sessionId);

            // setting_img, category_img
            setting_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog dialog = new  CustomDialog(getContext(), sessionId);
                    // WindowManager의 Layoutparameter변수를 생성하여 copyFrom을 통해 CustomDialog의 Window속성을 가져온다.
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    // layout Parameter의 height와 width를 설정하고 dialog의 Window를 불러와 속성을 재 설정한다.
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    Window window = dialog.getWindow();
                    window.setAttributes(lp);
                    dialog.show();
                }
            });

            category_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog dialog = new  CustomDialog(getContext(), sessionId);
                    // WindowManager의 Layoutparameter변수를 생성하여 copyFrom을 통해 CustomDialog의 Window속성을 가져온다.
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    // layout Parameter의 height와 width를 설정하고 dialog의 Window를 불러와 속성을 재 설정한다.
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    Window window = dialog.getWindow();
                    window.setAttributes(lp);
                    dialog.show();
                }
            });

            // category_btn, setting_img 끝

            // mypost_img, review_btn시작
            mypost_img.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(getContext(), UpdateReview.class);
                    startActivity(intent);
                }
            });


            // 나의 게시글 보기 버튼 클릭 시
            review_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(getContext(), UpdateReview.class);
                    startActivity(intent);
                }
            });

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("회원탈퇴").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                DeleteTask task = new DeleteTask();
                                Log.i("delete Session : ", sessionId + "");
                                String result = task.execute(sessionId).get();
                                if (result.equals("true")) {
                                    Toast.makeText(getContext(), "회원탈퇴 완료!", Toast.LENGTH_LONG).show();
                                    session.setSessionId("default");
                                    SharedPreferences preferences = getContext().getSharedPreferences(sessionId, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.clear();
                                    editor.commit();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getContext(), "회원탈퇴 실패!", Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception e){
                                Toast.makeText(getContext(), "서버 통신 오류", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialog.setMessage("Farm 회원을 탈퇴하시겠습니까?");
                    dialog.setNegativeButton("취소", null).setIcon(R.drawable.logo);
                    dialog.show();
                }
            });

            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            session.setSessionId("default");
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("취소", null).show();

                }
            });
        }

        guide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instruction_show();
            }
        });

        return view;
    }

    public class DeleteTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "delete?id=" + strings[0]);
            conn.setHeader(1000, "GET", false,  true);
            String result = conn.readData();
            Log.i("delete Result : ", result);
            return result;
        }
    }

    // 주의사항 dialog띄우기
    public void instruction_show(){
        // 주의사항 띄우기
        InstructionDialog dialog = new InstructionDialog(getContext());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        dialog.show();
    }
}
