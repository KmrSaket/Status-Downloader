package com.example.statusify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        sb.append(File.separator);
        sb.append(getResources().getString(R.string.foldername));
        sb.append(File.separator);

        checkAndCreateFolder(sb.toString());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                // This method will be executed once the timer is over
                String appType = "WhatsApp";
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("appType", appType);
                startActivity(intent);
                finish();
            }
//        }, 0);
//    }
    private void checkAndCreateFolder(String s) {
        if (!new File(s).exists() ) {
            new File(s).mkdir();
        }
        if (!new File(s + "WhatsApp").exists()){
            new File(s + "WhatsApp").mkdir();
        }
        if (!new File(s + "WhatsApp Bussiness").exists()){
            new File(s + "WhatsApp Bussiness").mkdir();
        }
    }
}