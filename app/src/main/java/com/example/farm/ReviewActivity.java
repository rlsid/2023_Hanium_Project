package com.example.farm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CheckedTextViewCompat;

public class ReviewActivity extends AppCompatActivity {
    // Review객체를 잘 받는지 확인을 위한 객체
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_layout);

        text = findViewById(R.id.content);

        Intent intent = getIntent();
        Review review = (Review) intent.getSerializableExtra("review");

        text.setText(review.getUser_id() + ", " + review.getContent() + ", " + review.getFlavor());
    }
}
