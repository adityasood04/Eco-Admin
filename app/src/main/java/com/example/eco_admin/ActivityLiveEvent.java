package com.example.eco_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eco_admin.adapters.RegisteredUserAdapter;
import com.example.eco_admin.databinding.ActivityLiveEventBinding;
import com.example.eco_admin.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityLiveEvent extends AppCompatActivity {

    private ActivityLiveEventBinding binding;
    private String liveEventId = null;

    private Event liveEvent = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        liveEventId = getIntent().getStringExtra("LIVE_EVENT_ID");
        binding.rcvParticipants.setLayoutManager(new LinearLayoutManager(this));
        Log.i("live", "onCreate: "+liveEventId);
        prefs = getSharedPreferences("EVENTS_PREFS", Context.MODE_PRIVATE);
        editor = prefs.edit();
        if(liveEventId != null) {
            fetchParticipants();
            binding.btnWrapUp.setOnClickListener(view -> {
                Log.i("adi", "updateUI: wrap up called" );
                confirmWrapup();
            });
        }

    }

    private void fetchParticipants() {
        showPb();
        FirebaseFirestore.getInstance().collection("CLEANUP_EVENTS")
                .document(liveEventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hidePb();
                        if(task.getResult().exists()){
                            liveEvent = task.getResult().toObject(Event.class);
                            assert liveEvent != null;
                            if(!liveEvent.getRegisteredUsers().isEmpty()){
                                updateUI(liveEvent);
                            }
                        } else {
                            Toast.makeText(ActivityLiveEvent.this, "Some error encountered", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hidePb();
                        Toast.makeText(ActivityLiveEvent.this, "Some error encountered" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateUI(Event liveEvent) {
        Log.i("live", "updateUI: called and name is " + liveEvent.getName());
        binding.tvNoParticipants.setVisibility(View.GONE);
        binding.llParticipants.setVisibility(View.VISIBLE);
        RegisteredUserAdapter adapter = new RegisteredUserAdapter(this, liveEvent.getRegisteredUsers());
        binding.rcvParticipants.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ActivityLiveEvent.this, MainActivity.class));
        finish();
    }

    private void confirmWrapup(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm wrap up")
                .setMessage("Are you sure you want to wrap up the event?")
                .setPositiveButton("Wrap up", (dialogInterface, i) -> wrapup())
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(true)
                .show();
    }

    private void wrapup() {
        editor.putBoolean("IS_EVENT_LIVE", false);
        editor.remove("LIVE_EVENT_ID");
        editor.apply();
        startActivity(new Intent(ActivityLiveEvent.this,MainActivity.class));
        finish();
    }

    private void showPb(){
        binding.pbLiveEvent.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        binding.pbLiveEvent.setVisibility(View.GONE);
    }
}