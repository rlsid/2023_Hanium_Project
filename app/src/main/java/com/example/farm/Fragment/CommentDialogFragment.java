package com.example.farm.Fragment;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.Connection.HttpConnection;
import com.example.farm.HttpUrl;
import com.example.farm.LoginActivity;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.example.farm.Session;
import com.example.farm.SingleComment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class CommentDialogFragment extends DialogFragment {
    View view;

    ImageButton close_btn;
    EditText comment_et;
    ImageButton check_btn;
    RecyclerView comment_view;
    Session session;
    CommentRecyclerAdapter adapter;
    ArrayList<SingleComment> comments;

    private String review_id;

    public CommentDialogFragment(String review_id){
        this.review_id = review_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comment_dialogfragment, container);
        comment_view = view.findViewById(R.id.comment_view);
        close_btn = view.findViewById(R.id.close_btn);
        comment_et = view.findViewById(R.id.comment_et);
        check_btn = view.findViewById(R.id.check_btn);


        Log.i("reveiw_id", review_id);
        session = (Session)((MainActivity)getActivity()).getApplication();

        // 게시물에대한 댓글들을 서버로부터 가져온다.
        try {
            CommentTask task = new CommentTask();
            comments = task.execute(review_id).get();
            adapter = new CommentRecyclerAdapter(comments); // 받았는데 갱신이 안된거..
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            comment_view.setLayoutManager(manager);
            comment_view.setAdapter(adapter);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment_et.getText().length() > 0) {
                    // 댓글 저장 서버 전달값 : 자신의 세션(session_id), 댓글 내용(comment), 현재 시간
                    // review_id와 댓글을 전달하여 댓글 저장
                    InsertCommentTask task = new InsertCommentTask();
                    String result = "false";
                    try {
                        if(!session.getSessionId().equals("default")) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                            String now = format.format(new Date());
                            // 여기 DB DateTime자료형 형식이랑 맞게 DateFormatter를 이용하여서 String now에 저장
                            result = task.execute(new SingleComment(session.getSessionId(), comment_et.getText().toString(), now, review_id)).get();
                            comments.add(new SingleComment(session.getSessionId(), comment_et.getText().toString(), now, review_id));
                            adapter.notifyItemChanged(comments.size());
                            // 서버 동작 결과에 따른 동작분류
//                            if (result.equals("false")) {
//                                Toast.makeText(getContext(), "댓글 등록에 실패했습니다.", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(getContext(), "댓글 등록에 성공했습니다.", Toast.LENGTH_LONG).show();
//                            }
                            comment_et.setText("");
                        }else{
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext()).setTitle("안내").setIcon(R.drawable.logo)
                                    .setNegativeButton("로그인하러가기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getContext().getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            comment_et.setText("");
                                        }
                                    }).setMessage("댓글을 이용하려면 로그인해야합니다.");
                            dialog.show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext()).setTitle("오류")
                            .setMessage("댓글의 내용을 입력해주세요").setIcon(R.drawable.logo)
                            .setPositiveButton("확인", null);
                    dialog.show();
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

    public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CommentViewHolder>{

        ArrayList<SingleComment> comments;

        public CommentRecyclerAdapter(ArrayList<SingleComment> comments){
            this.comments = comments;
        }

        @NonNull
        @Override
        public CommentRecyclerAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_fragment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentRecyclerAdapter.CommentViewHolder holder, int position) {
            holder.onBind(comments.get(position));
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }


        public class CommentViewHolder extends RecyclerView.ViewHolder{
            TextView user_id, comment_tv, date_tv;
            Button delete_btn;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                user_id = itemView.findViewById(R.id.user_id);
                comment_tv = itemView.findViewById(R.id.comment_tv);
                date_tv = itemView.findViewById(R.id.comment_time);
                delete_btn = itemView.findViewById(R.id.delete_btn);
            }

            // onBind메소드 위젯과 id를 엮어서 코드 실행
            public void onBind(SingleComment comment){
                user_id.setText(comment.getUser_id());
                comment_tv.setText(comment.getComment());
                date_tv.setText(comment.getDate());
                if(user_id.equals(session.getSessionId()))
                    delete_btn.setVisibility(View.VISIBLE);

                // 댓글 삭제 요청 및 레이아웃 삭제
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        adapter.notifyItemRemoved();
                    }
                });
            }
        }
    }

    // 매개변수, 그냥, 반환값
    public class CommentTask extends AsyncTask<String, Void, ArrayList<SingleComment>> {
        @Override
        protected ArrayList<SingleComment> doInBackground(String... strings){
            ArrayList<SingleComment> list = new ArrayList<>();
            String review_id = strings[0];

            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "comments?review_id=" + review_id);
            conn.setHeader(1000, "GET", false, true);
            // 서버에서 String 형태로 ArrayList<SingleComment>데이터 Json형태로 반환
            String result = conn.readData();
            Log.i("comment", result);
            Gson gson = new Gson();
            // 서버로부터 받은 Json형태의 데이터를 ArrayList<SingleComment>형식으로 변환한다.
            list = gson.fromJson(result, new TypeToken<ArrayList<SingleComment>>(){}.getType());

            return list;
        }
    }

    public static class InsertCommentTask extends AsyncTask<SingleComment, Void, String>{

        @Override
        protected String doInBackground(SingleComment... singleComments) {
            String result = "false";

            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "insertComment");
            conn.setHeader(1000, "POST", true, true);

            // comment를 SingleComment클래스의 Json형태로 전달
            conn.writeData(new Gson().toJson(singleComments[0], SingleComment.class));
            result = conn.readData();

            return result;
        }
    }

    public static class RemoveCommentTesk extends AsyncTask<SingleComment, Void, String>{
        @Override
        protected String doInBackground(SingleComment... singleComments) {
            String result = "false";

            HttpUrl url = new HttpUrl();
            SingleComment comment = singleComments[0];
            HttpConnection conn = new HttpConnection(url.getUrl() + "removeComment?review_id=" + comment.getReview_id() + "&user_id=" + comment.getUser_id() + "&comment=" + comment.getComment());
            conn.setHeader(1000, "DELETE", false, true);

            result = conn.readData();

            return result;
        }
    }


}
