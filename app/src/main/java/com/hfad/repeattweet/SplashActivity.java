package com.hfad.repeattweet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    public static String SHARED_PREF_NAME;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor sharedPrefEditor;


    public int delayTime = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = this.getSharedPreferences(this.SHARED_PREF_NAME, 0);
        sharedPrefEditor = sharedPref.edit();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sharedPref.contains("newUser")){
                    Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                    startActivity(intent);
                    sharedPrefEditor.putString("newUser", "1");
                    sharedPrefEditor.commit();
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, this.delayTime * 1000);

    }
}
