package com.example.farm.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.farm.R;

public class InstructionDialog extends Dialog {

    ImageButton close_btn;


    public InstructionDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.instruction_dialog);

        close_btn = findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });


    }

}
