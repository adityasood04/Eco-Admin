package com.example.eco_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.eco_admin.databinding.ActivityMainBinding;
import com.example.eco_admin.fragments.FragmentHome;
import com.example.eco_admin.fragments.FragmentProfile;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final FragmentHome homeFragment = new FragmentHome(this);
    private final FragmentProfile profileFragment = new FragmentProfile(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        changeFragment(0);


        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    changeFragment(0);
                }
                else if(item.getItemId() == R.id.profile){
                    changeFragment(1);
                }
                return true;
            }
        });


    }

    private void changeFragment(int i){
        Fragment fragment = homeFragment;
        switch (i){
            case 1:
                fragment = profileFragment;
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.commit();
    }

}