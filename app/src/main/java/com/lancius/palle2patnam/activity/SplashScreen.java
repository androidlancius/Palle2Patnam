package com.lancius.palle2patnam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.SessionManager;


/**
 * Created by madhu on 4/18/2017.
 */

public class SplashScreen extends AppCompatActivity {

    SessionManager session;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionManager(getApplicationContext());

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (session.isLogged()) {

                    intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);

                } else {

                    if (session.isShown()) {

                        intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);

                    } else {

                        // Introduction Screens
                        intent = new Intent(SplashScreen.this, RegisterActivity.class);
                        startActivity(intent);

                    }

                }

                finish();
            }
        }, 2000);

    }
}

