package com.example.farm;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.farm.Fragment.CameraFragment;
import com.example.farm.Fragment.HomeFragment;
import com.example.farm.Fragment.MyInfoFragment;
import com.example.farm.Fragment.CommunityFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment fragment_home, fragment_camera, fragment_community, fragment_myInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        fragment_home = new HomeFragment();
        fragment_camera = new CameraFragment();
        fragment_community = new CommunityFragment();
        fragment_myInfo = new MyInfoFragment();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 초기 Fragment설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();
                        return true;
                    case R.id.camera:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_camera).commitAllowingStateLoss();
                        return true;
                    case R.id.community:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_community).commitAllowingStateLoss();
                        return true;
                    case R.id.my:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_myInfo).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });
    }

}