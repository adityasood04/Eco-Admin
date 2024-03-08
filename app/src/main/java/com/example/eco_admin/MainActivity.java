package com.example.eco_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.eco_admin.databinding.ActivityMainBinding;
import com.example.eco_admin.fragments.FragmentHome;
import com.example.eco_admin.fragments.FragmentProfile;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final FragmentHome homeFragment = new FragmentHome(this);
    private final FragmentProfile profileFragment = new FragmentProfile(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}