package com.example.eco_admin;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ActivitySplash extends AppCompatActivity {
    String ngo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*TODO:
           model for event -- done
           replace dummy data with actual edittext values -- done
           a profile section
           organiser details from pre entered data -- done
           add more details fields while creating event
        */

        ngo = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE).getString("NGO", "");
        if(ngo.equals("")) {
            startActivity(new Intent(this,ActivityLogin.class));
            finish();
        } else {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

    }
}