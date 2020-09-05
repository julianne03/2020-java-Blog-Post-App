package com.example.azangazang;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.show();
        ab.setTitle("리뷰 쓰기");
        ab.setDisplayHomeAsUpEnabled(true);
    }
}