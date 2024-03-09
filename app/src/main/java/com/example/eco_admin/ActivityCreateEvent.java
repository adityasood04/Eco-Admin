package com.example.eco_admin;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eco_admin.databinding.ActivityCreateEventBinding;
import com.example.eco_admin.models.Event;
import com.example.eco_admin.models.NGO;
import com.example.eco_admin.models.NGOEvents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityCreateEvent extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    int mHour = calendar.get(Calendar.HOUR_OF_DAY);
    int mMinute = calendar.get(Calendar.MINUTE);
    DatePickerDialog datePickerDialog;
    private ActivityCreateEventBinding binding;

    String mSelectedDate;
    String mSelectedTime;
    FirebaseFirestore db;
    NGO ngo;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private NGOEvents eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        ngo = new Gson().fromJson(getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE).getString("NGO", ""), NGO.class);
        prefs = getSharedPreferences("EVENTS_PREFS", Context.MODE_PRIVATE);
        editor = prefs.edit();
        //TODO : If ngo is null logout
        if(getIntent().getStringExtra("EVENTS_LIST") != null){
            eventsList = new Gson().fromJson(getIntent().getStringExtra("EVENTS_LIST"),NGOEvents.class);
        } else {
            eventsList = new NGOEvents();
        }
        setupDropDown();
        setupDatePicker();
        setupTimePicker();

        binding.btnCreateEvent.setOnClickListener(view -> {

            if (binding.etNameOfEvent.getText().toString().isEmpty() || binding.etNameOfEvent.getText().toString().equals("")) {
                binding.etNameOfEvent.setError("Required");
            }
            if (binding.etDescription.getText().toString().isEmpty() || binding.etDescription.getText().toString().equals("")) {
                binding.etDescription.setError("Required");
            }
            if (binding.etLocation.getText().toString().isEmpty() || binding.etLocation.getText().toString().equals("")) {
                binding.etLocation.setError("Required");
            }
            if (mSelectedTime == null) {
                binding.tvSelectedTime.setTextColor(getColor(R.color.colorDanger));
                binding.tvSelectedTime.setText("Please select a time.");
            }
            if (mSelectedDate == null) {
                binding.tvSelectedDate.setTextColor(getColor(R.color.colorDanger));
                binding.tvSelectedDate.setText("Please select a valid date for the event.");
            }
            if (!binding.etNameOfEvent.getText().toString().isEmpty() &&
                    !binding.etNameOfEvent.getText().toString().isEmpty() &&
                    !binding.etDescription.getText().toString().isEmpty() &&
                    !binding.etLocation.getText().toString().isEmpty() &&
                    mSelectedTime != null &&
                    mSelectedDate != null
            ) {
//                fetchPreviousEvents();
                createEvent();
            }
        });


    }


    private void createEvent() {
        showPb();
        Event event = new Event();
        event.setName(binding.etNameOfEvent.getText().toString());
        event.setDescription(binding.etDescription.getText().toString());
        event.setLocation(binding.etLocation.getText().toString());
        event.setTime(mSelectedTime);
        event.setDate(mSelectedDate);
        event.setNgo(ngo);
        event.setRegisteredUsers(new ArrayList<>());
        event.setType(binding.spinner.getSelectedItem().toString());
        eventsList.addEvent(event);
        event.setId(db.collection("CLEANUP_EVENTS").document().getId());
        Log.i("adi", "createEvent: date "+event.getDate());

        db.collection("CLEANUP_EVENTS").document(event.getId()).set(event)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hidePb();
                        if(task.isSuccessful()) {
                            editor.putBoolean("IS_EVENT_LIVE", true);
                            editor.putString("LIVE_EVENT_ID", event.getId());
                            editor.apply();
                            startActivity(new Intent(ActivityCreateEvent.this,MainActivity.class));
                            finish();
                            Toast.makeText(ActivityCreateEvent.this, "Event created successfully!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityCreateEvent.this, "Some error encountered. Try again!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hidePb();
                        Log.i("Create event", "onFailure: create event" + e.getMessage());
                        Toast.makeText(ActivityCreateEvent.this, "Some error encountered. Try again!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupDatePicker() {
        binding.btnSelectDate.setOnClickListener(view -> {
            datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    Log.i("date", "onDateSet: day " + day+" year "+ year +" month "+month);
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, day);
                    month = month+1;
                        binding.tvSelectedTime.setTextColor(getColor(R.color.textPrimary));
                        mSelectedDate = day+"/"+month+"/"+year;
                        binding.tvSelectedDate.setText(mSelectedDate);

                }
            }, year, month, dayOfMonth);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Set minimum date to today
            datePickerDialog.show();
        });
    }

    private void setupTimePicker() {
        binding.btnSelectTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String AM_PM;
                            if (hourOfDay < 12) {
                                AM_PM = "AM";
                            } else {
                                AM_PM = "PM";
                            }
                            mSelectedTime = String.format("%02d:%02d ", hourOfDay, minute);
                            binding.tvSelectedTime.setTextColor(getColor(R.color.textPrimary));
                            binding.tvSelectedTime.setText(mSelectedTime + AM_PM);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

    }

//    private void fetchPreviousEvents() {
//        db.collection("EVENTS").document(ngo.getName()).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            if(task.getResult() != null){
//                                eventsList = task.getResult().toObject(NGOEvents.class);
//                            } else {
//                                eventsList = new NGOEvents();
//                            }
//                            createEvent(eventsList);
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
//    }


    private void setupDropDown() {
        String[] languages = getResources().getStringArray(R.array.type_of_event);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, languages);
        binding.spinner.setAdapter(arrayAdapter);
        binding.spinner.setSelection(0);
    }

    private void showPb(){
        binding.pbCreateEvent.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        binding.pbCreateEvent.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ActivityCreateEvent.this, MainActivity.class));
        finish();
    }
}