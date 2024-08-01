package com.example.farm.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.farm.R;

public class CustomDialog extends Dialog {

    ImageButton close_btn;
    Button set_btn;
    CheckBox[] list;
    String sessionId;

    public CustomDialog(@NonNull Context context, String sessionId) {
        super(context);
        setContentView(R.layout.layout_recommendfruit);
        this.sessionId = sessionId;
        set_btn = findViewById(R.id.set_btn);

        // Nutrition체크여부 확인을 위한 영양소 별 CheckBox객체 배열형태로 생성
        list = new CheckBox[]{
                findViewById(R.id.vitaminA), findViewById(R.id.betacarotin), findViewById(R.id.vitaminB1), findViewById(R.id.vitaminB2),
                findViewById(R.id.vitaminB3), findViewById(R.id.vitaminB6), findViewById(R.id.folicAcid), findViewById(R.id.viotin),
                findViewById(R.id.vitaminC), findViewById(R.id.vitaminE), findViewById(R.id.vitaminK), findViewById(R.id.callium),
                findViewById(R.id.caltuim), findViewById(R.id.magnesium), findViewById(R.id.iin), findViewById(R.id.fabric)
        };

        for(CheckBox box : list){
            SharedPreferences preferences = getContext().getSharedPreferences(sessionId, Context.MODE_PRIVATE);
            // true로 설정되어있는 경우
            if(preferences.getString(box.getText().toString(), "").equals("true")){
                box.setChecked(true);
            }else{
                box.setChecked(false);
            }
        }

        close_btn = findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = 0;
                for(CheckBox ch : list){
                    if(ch.isChecked() == true)
                        cnt++;
                }
                if(cnt > 3)
                    Toast.makeText(getContext(), "3개까지만 선택할 수 있습니다", Toast.LENGTH_LONG).show();
                else {
                    // 파일이름, 모드
                    SharedPreferences preferences = getContext().getSharedPreferences(sessionId, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    for (CheckBox box : list) {
                        if (box.isChecked()) {
                            editor.putString(box.getText().toString(), "true");
                        } else
                            editor.putString(box.getText().toString(), "false");
                    }
                    editor.commit();
                    dismiss();
                }
            }
        });

    }
}
