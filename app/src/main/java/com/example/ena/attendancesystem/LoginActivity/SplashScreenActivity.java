package com.example.ena.attendancesystem.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.ena.attendancesystem.PhotoActivity;
import com.example.ena.attendancesystem.R;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);

        imageView = (ImageView)findViewById(R.id.imageView);

        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.splash);
        imageView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                final String MyPref = "MyPref";
                final String USERNAME = "USERNAME";
                final String PASSWORD = "PASSWORD";
                SharedPreferences sharedpreferences;
                sharedpreferences = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
                final String username = sharedpreferences.getString(USERNAME, null);
                final String password = sharedpreferences.getString(PASSWORD, null);


                if(username == null)
                {
                    Intent intent = new Intent(SplashScreenActivity.this,LoginPageActivity.class);
                    startActivity(intent);
                    finish();
                }

                else if(password == null)
                {
                    Intent intent = new Intent(SplashScreenActivity.this,LoginPageActivity.class);
                    startActivity(intent);
                    finish();
                }

                else
                {
                    Intent intent = new Intent(SplashScreenActivity.this,PhotoActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
