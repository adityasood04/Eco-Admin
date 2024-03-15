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
import com.example.eco_admin.models.NGO;
import com.example.eco_admin.models.PreviousEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ActivityLiveEvent extends AppCompatActivity {

    private ActivityLiveEventBinding binding;
    private String liveEventId = null;

    private Event liveEvent = null;
    private PreviousEvent previousEvent = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private  NGO mNGO;

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
        binding.tvEventName.setText(liveEvent.getName().toString());
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
        saveEventToDB();
    }

    private void saveEventToDB() {
        String ngoData= getSharedPreferences("APP_PREFS",Context.MODE_PRIVATE).getString("NGO","");
        mNGO = new Gson().fromJson(ngoData, NGO.class);
        Log.i("live", "saveEventToDB: " + mNGO.getUid());
        if(previousEvent == null) previousEvent = new PreviousEvent();
        previousEvent.setName(liveEvent.getName());
        previousEvent.setDescription(liveEvent.getDescription());
        previousEvent.setId(liveEvent.getId());
        previousEvent.setLocation(liveEvent.getLocation());
        previousEvent.setDate(liveEvent.getDate());
        previousEvent.setTime(liveEvent.getTime());
        previousEvent.setType(liveEvent.getType());
        previousEvent.setNoOfParticipants(liveEvent.getRegisteredUsers().size());


        FirebaseFirestore.getInstance().collection("NGO-Data")
                .document(mNGO.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i("live", "saveEventToDB:1 " + task.getResult() );

                        if(task.isSuccessful()){
                            mNGO = task.getResult().toObject(NGO.class);
                            Log.i("live", "saveEventToDB:1.5 mngo is " + mNGO.getName());

                            if(mNGO != null) {
                                Log.i("live", "saveEventToDB:2 task success and mngo not null " + mNGO.getName());

                                if(mNGO.getPreviousEvents() == null || mNGO.getPreviousEvents().isEmpty()){
                                    mNGO.setPreviousEvents(new ArrayList<>());
                                    Log.i("live", "saveEventToDB:3 events empty");


                                }
                                mNGO.getPreviousEvents().add(previousEvent);

                                FirebaseFirestore.getInstance().collection("NGO-Data")
                                        .document(mNGO.getUid())
                                        .set(mNGO)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                editor.putBoolean("IS_EVENT_LIVE", false);
                                                editor.remove("LIVE_EVENT_ID");
                                                editor.apply();
                                                startActivity(new Intent(ActivityLiveEvent.this,MainActivity.class));
                                                finish();

                                                Toast.makeText(ActivityLiveEvent.this, "Event wrapped up!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ActivityLiveEvent.this, "Some error encountered"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                            else {
                                Log.i("live", "saveEventToDB:1.5 ngo null" + task.getResult());

                            }
                        } else {
                            Toast.makeText(ActivityLiveEvent.this, "Some error encountered", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityLiveEvent.this, "Some error encountered" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void showPb(){
        binding.pbLiveEvent.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        binding.pbLiveEvent.setVisibility(View.GONE);
    }
}