package com.example.farm.Fragment;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.farm.Connection.SearchTask;
import com.example.farm.Dialog.CustomDialog;
import com.example.farm.Fruit;
import com.example.farm.FruitInformationActivity;
import com.example.farm.Connection.HttpConnection;
import com.example.farm.HttpUrl;
import com.example.farm.MainActivity;
import com.example.farm.PeriodFruit;
import com.example.farm.R;
import com.example.farm.RecommendFruit;
import com.example.farm.Session;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private View view;
    private ViewPager2 viewPager;
    private TextView recommend_tv;
    private RecyclerView recommend;
    private LinearLayout recommend_ll;
    private SearchView search;
    private Button hotFruit1, hotFruit2, hotFruit3, more_btn;
    private TextView addi_tv;
    private RelativeLayout recommend_rl;
    private Fragment currentFragment;

    // fragment 새로고침 함수
    private void refreshFragment(){
        FragmentTransaction ft = requireFragmentManager().beginTransaction();
        HomeFragment fragment = new HomeFragment();
        ft.replace(R.id.main_layout, fragment);
        ft.commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_content, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        recommend_tv = view.findViewById(R.id.recommend_tv);
        recommend = view.findViewById(R.id.fruit_list);
        recommend_ll = view.findViewById(R.id.recommend_fl);
        search = view.findViewById(R.id.searchFruit);
        hotFruit1 = view.findViewById(R.id.hotFruit1);
        hotFruit2 = view.findViewById(R.id.hotFruit2);
        hotFruit3 = view.findViewById(R.id.hotFruit3);
        more_btn = view.findViewById(R.id.more_btn);
        addi_tv = view.findViewById(R.id.addi_tv);
        recommend_rl = view.findViewById(R.id.setting_rl);
        currentFragment = this.getParentFragment();

        search.setSubmitButtonEnabled(true);

        Session session = (Session)((MainActivity)getActivity()).getApplication();
        // 로그인 안된 경우
        if(session.getSessionId().equals("default")) {
            recommend_ll.setVisibility(View.INVISIBLE);
            recommend_tv.setVisibility(View.INVISIBLE);
            addi_tv.setVisibility(View.INVISIBLE);
            recommend_rl.setVisibility(View.INVISIBLE);
        }else{ // 로그인 된 경우 영양소 설정이 됐는지 안됐는지에 따라 구별
            SharedPreferences preferences = getActivity().getSharedPreferences(session.getSessionId(), Context.MODE_PRIVATE);
            Map<String, String> list = (Map<String, String>) preferences.getAll();
            Iterator<String> it = list.values().iterator();
            boolean flag = false;
            while(it.hasNext())
                if(it.next().equals("true")) {
                    flag = true;
                    break;
                }
            if(flag == true) { // 영양소가 있는 경우
                recommend_tv.setText(session.getSessionId() + "님을 위한 추천 과일");
                recommend_ll.setVisibility(View.VISIBLE);
                recommend_tv.setVisibility(View.VISIBLE);
                addi_tv.setVisibility(View.VISIBLE);
                recommend_rl.setVisibility(View.INVISIBLE);
                recommend.setVisibility(View.VISIBLE);
                more_btn.setVisibility(View.VISIBLE);
            }else{
                recommend_rl.setVisibility(View.VISIBLE);
                recommend.setVisibility(View.INVISIBLE);
                addi_tv.setVisibility(View.INVISIBLE);
                more_btn.setVisibility(View.INVISIBLE);
            }
        }

        recommend_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new  CustomDialog(getContext(), session.getSessionId());
                // WindowManager의 Layoutparameter변수를 생성하여 copyFrom을 통해 CustomDialog의 Window속성을 가져온다.
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                // layout Parameter의 height와 width를 설정하고 dialog의 Window를 불러와 속성을 재 설정한다.
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                Window window = dialog.getWindow();
                window.setAttributes(lp);
                dialog.show();
                getFragmentManager().executePendingTransactions();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.i("dismissed : ", "true1");
                        refreshFragment();
                    }
                });
            }
        });

        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new  CustomDialog(getContext(), session.getSessionId());
                // WindowManager의 Layoutparameter변수를 생성하여 copyFrom을 통해 CustomDialog의 Window속성을 가져온다.
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                // layout Parameter의 height와 width를 설정하고 dialog의 Window를 불러와 속성을 재 설정한다.
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                Window window = dialog.getWindow();
                window.setAttributes(lp);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.i("dismissed : ", "true2");
                        refreshFragment();
                    }
                });
            }
        });

        RecommendTask task = new RecommendTask();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("M");

        // 검색바 이벤트 추가
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            // 검색버튼 눌렀을때 작동하는 메소드
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchTask task = new SearchTask();
                try {
                    Fruit fruit = task.execute(query).get();
                    if(fruit != null) {
                        // intent로 정보 제공 layout으로 넘어감
                        Intent intent = new Intent(view.getContext().getApplicationContext(), FruitInformationActivity.class);
                        intent.putExtra("info", fruit);
                        startActivity(intent);
                        search.setQuery("", false);
                        search.clearFocus();
                    }else{
                        Toast.makeText(view.getContext().getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return false;
            }

            // 검색내용이 변할때 작동되는 메소드
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        String month = format.format(calendar.getTime());
        try {
            // 제철과일 정보 받아오기
            ArrayList<PeriodFruit> fruits = task.execute(month).get();
            // 제철과일 정보를 띄우기 위한 Adapter생성 및 설정
            ViewPagerAdapter adapter = new ViewPagerAdapter(fruits, R.layout.fruit_img);
            viewPager.setAdapter(adapter);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = getContext().getSharedPreferences(session.getSessionId(), Context.MODE_PRIVATE);
        Map<String, String> nu = (Map<String, String>) preferences.getAll();
        ArrayList<String> temp = new ArrayList<>();
        Iterator<String> iterator = nu.keySet().iterator();
        while(iterator.hasNext()){
            String nutrition = iterator.next();
            if(preferences.getString(nutrition, "").equals("true")) // SharedPreferences에 저장된 영양소 정보가 true인 경우
                temp.add(nutrition);

        }
        String[] nutritions = temp.toArray(new String[0]);
        ArrayList<RecommendFruit> rFruits = new ArrayList<>();
        if(temp.size() > 0){
            RecommendUserTask recommendTask = new RecommendUserTask();
            try {
                rFruits = recommendTask.execute(nutritions).get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        RecommendRecyclerView adapter = new RecommendRecyclerView(rFruits);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recommend.setLayoutManager(manager);
        recommend.setAdapter(adapter);

        // Hot과일의 목록을 가져온다.
        HotFruitTask hotTask = new HotFruitTask();
        try {
            String name = hotTask.execute().get();
            String[] names = name.split(" ");
            Log.i("size : ", names.length + "");
            int size = names.length;

            switch(size){
                case 1:
                    hotFruit1.setText(names[0]);
                    hotFruit1.setOnClickListener(new HotClickListener(names[0]));
                    hotFruit2.setText("추천과일이 없어요 ㅠㅠ");
                    hotFruit2.setTextColor(Color.GRAY);
                    hotFruit3.setText("추천과일이 없어요 ㅠㅠ");
                    hotFruit3.setTextColor(Color.GRAY);
                    break;
                case 2:
                    hotFruit1.setText(names[0]);
                    hotFruit1.setOnClickListener(new HotClickListener(names[0]));
                    hotFruit2.setText(names[1]);
                    hotFruit2.setOnClickListener(new HotClickListener(names[1]));
                    hotFruit3.setText("추천과일이 없어요 ㅠㅠ");
                    hotFruit3.setTextColor(Color.GRAY);
                    break;
                case 3:
                    hotFruit1.setText(names[0]);
                    hotFruit1.setOnClickListener(new HotClickListener(names[0]));
                    hotFruit2.setText(names[1]);
                    hotFruit2.setOnClickListener(new HotClickListener(names[1]));
                    hotFruit3.setText(names[2]);
                    hotFruit3.setOnClickListener(new HotClickListener(names[2]));
                    break;
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    public class HotClickListener implements View.OnClickListener{
        private String fruit_name;
        public HotClickListener(String fruit_name){
            this.fruit_name = fruit_name;
        }
        @Override
        public void onClick(View v) {
            SearchTask task = new SearchTask();
            try {
                Fruit fruit = task.execute(fruit_name).get();
                Intent intent = new Intent(getContext().getApplicationContext(), FruitInformationActivity.class);
                intent.putExtra("info", fruit);
                startActivity(intent);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    // 제철과일 추천을 위한 ViewPager Adapter클래스 선언
    // RecyclerView.Adapter를 사용
    public class ViewPagerAdapter extends RecyclerView.Adapter<FruitViewHolder>{
        ArrayList<PeriodFruit> fruits;

        int id;

        public ViewPagerAdapter(ArrayList<PeriodFruit> fruits, int id){
            this.fruits = fruits;
            this.id = id;
        }

        @NonNull
        @Override
        public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewHolder를 생성한다.
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.fruit_img, parent, false);
            return new FruitViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) { // ViewHolder와 Bind한다.
            holder.onBind(fruits.get(position));
        }

        @Override
        public int getItemCount() { // 총 Item갯수를 리턴
            if(fruits != null)
                return fruits.size();
            return -1;
        }
    }

    public class FruitViewHolder extends RecyclerView.ViewHolder{

        private ImageView fruit_img;
        private TextView fruit_name, fruit_period;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            fruit_img = itemView.findViewById(R.id.fruit_img);
            fruit_name = itemView.findViewById(R.id.fruit_name);
            fruit_period = itemView.findViewById(R.id.fruit_period);

            int screenWidth = (int)(getResources().getDisplayMetrics().widthPixels * 0.8);
            fruit_img.getLayoutParams().width = screenWidth;
            fruit_img.getLayoutParams().height = screenWidth;
            fruit_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchTask task = new SearchTask();
                    Intent intent = new Intent(view.getContext().getApplicationContext(), FruitInformationActivity.class);
                    try {
                        Fruit info = task.execute(fruit_name.getText().toString()).get();
                        intent.putExtra("info", info);

                        startActivity(intent);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        public void onBind(PeriodFruit fruit){
            if(fruit != null){
                int imageResource = getResources().getIdentifier(fruit.getFile_name().toLowerCase(), "drawable", requireContext().getPackageName());
                fruit_img.setImageResource(imageResource);
                fruit_name.setText(fruit.getFruit_name());
                fruit_period.setText("제철 시기 : " + fruit.getStart() + "월 ~ " + fruit.getEnd() + "월");
            }

        }
    }


    // 사용자 맞춤 과일을 추천하기 위한 RecyclerView의 Adapter선언
    public class RecommendRecyclerView extends RecyclerView.Adapter<RecommendViewHolder>{
        ArrayList<RecommendFruit> fruits;

        public RecommendRecyclerView(ArrayList<RecommendFruit>fruits){
            this.fruits = fruits;
        }
        @NonNull
        @Override
        public RecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ViewHolder를 생성하여 반환하는 메소드
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_image, parent, false);
            return new RecommendViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecommendViewHolder holder, int position) { // fruits(과일 목록)을 참조하여 요소 하나당 ViewHolder메소드를 통하여 레이아웃 생성
            holder.onBind(fruits.get(position));
        }

        @Override
        public int getItemCount() {
            return fruits.size();
        }
    }

    public class RecommendViewHolder extends RecyclerView.ViewHolder{
        ImageView fruit_img;
        TextView fruit_name;
        TextView nutrition_name;
        public RecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            fruit_img = itemView.findViewById(R.id.fruit_img);
            fruit_name = itemView.findViewById(R.id.fruit_name);
            nutrition_name = itemView.findViewById(R.id.nutrition_tv);
            fruit_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchTask task = new SearchTask();
                    Intent intent = new Intent(view.getContext().getApplicationContext(), FruitInformationActivity.class);
                    try {
                        Fruit info = task.execute(fruit_name.getText().toString()).get();
                        intent.putExtra("info", info);
                        startActivity(intent);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }

        public void onBind(RecommendFruit fruit){
            int imageResource = getResources().getIdentifier(fruit.getFruit_img().toLowerCase(), "drawable", requireContext().getPackageName());
            fruit_img.setImageResource(imageResource);
            fruit_name.setText(fruit.getFruit_name());
            nutrition_name.setText(fruit.getNutrition_name() + ": " + fruit.getNutrition_amount() + fruit.getUnit());
            fruit_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext().getApplicationContext(), FruitInformationActivity.class);
                    SearchTask task = new SearchTask();
                    try {
                        Fruit info = task.execute(fruit_name.getText().toString()).get();
                        intent.putExtra("info", info);
                        startActivity(intent);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
    }

    public static class RecommendTask extends AsyncTask<String, Void, ArrayList<PeriodFruit>> {

        @Override
        protected ArrayList<PeriodFruit> doInBackground(String... strings) {
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "period?month=" + strings[0]);
            conn.setHeader(1000, "GET", false, true);
            String fruit_list = conn.readData();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Log.i("fruit_list(background) : ", gson.toJson(fruit_list));
            ArrayList<PeriodFruit> result = gson.fromJson(fruit_list, new TypeToken<ArrayList<PeriodFruit>>() {}.getType());

            return result;
        }
    }

    public static class RecommendUserTask extends AsyncTask<String, Void, ArrayList<RecommendFruit>>{

        @Override
        protected ArrayList<RecommendFruit> doInBackground(String... nutritions) {
            HttpUrl url = new HttpUrl();
            HttpConnection conn;
            // nutritions갯수에 따라 제어
            if(nutritions.length == 1)
                conn = new HttpConnection(url.getUrl() + "recommend?nutrition=" + nutritions[0]);
            else if(nutritions.length == 2)
                conn = new HttpConnection(url.getUrl() + "recommend?nutrition=" + nutritions[0] + "&nutrition=" + nutritions[1]);
            else
                conn = new HttpConnection(url.getUrl() + "recommend?nutrition=" + nutritions[0] + "&nutrition=" + nutritions[1] + "&nutrition=" + nutritions[2]);
            conn.setHeader(1000, "GET", false, true);
            String result = conn.readData();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Log.i("recommend Fruits : ", gson.toJson(result));
            ArrayList<RecommendFruit> fruits = gson.fromJson(result, new TypeToken<ArrayList<RecommendFruit>>() {}.getType());
            return fruits;
        }
    }

    public static class HotFruitTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... voids) {
            HttpUrl url = new HttpUrl();
            HttpConnection conn = new HttpConnection(url.getUrl() + "hotFruits");
            conn.setHeader(1000, "GET", false, true);

            String result = conn.readData();

            return result;
        }
    }
}
