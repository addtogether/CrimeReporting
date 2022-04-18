package com.example.crimereporting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    private TextView tv1;
    private Animation topAnim,bottomAnim;
    private LottieAnimationView lottieAnimationView;
    private static int SPLASH_TIME_OUT=4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1=findViewById(R.id.tv1);
        lottieAnimationView = findViewById(R.id.lottie);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.middle_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        tv1.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);

    }
}