package com.example.treasurehunter2;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
public class MainActivity extends AppCompatActivity {
    private static int TIME_OUT = 6000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainActivity.this, PlayActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, TIME_OUT);
    }
}
