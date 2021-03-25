package com.example.simpleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

public class SpashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5920;

    //variables

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        lottieAnimationView = findViewById(R.id.progressBar);


        lottieAnimationView.animate().translationX(-1600).setDuration(300).setStartDelay(5920);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SpashScreen.this, Login.class);
                startActivity(intent);
                finish();

            }
        },SPLASH_SCREEN);
    }
}