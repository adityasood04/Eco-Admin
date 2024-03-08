package com.example.eco_admin.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eco_admin.ActivityCreateEvent;
import com.example.eco_admin.R;
import com.example.eco_admin.databinding.FragmentHomeBinding;
import com.example.eco_admin.models.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;
    private Context context;
    FirebaseFirestore db;
    boolean isEventLive = false;
    String liveEventId = null;
    private Event liveEvent = null;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public FragmentHome() {
        // Required empty public constructor
    }
    public FragmentHome(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null){
            binding = FragmentHomeBinding.inflate(inflater);
//            Methods.setStatusBarColor(requireActivity().getColor(R.color.colorStatusBar), (AppCompatActivity) requireActivity());
            loadData();
            setListeners();
        }
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnOpenCreateEvent.setOnClickListener(view -> {
            if(!isEventLive){
                Intent intent = new Intent(context, ActivityCreateEvent.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                Toast.makeText(context, "A event already in progress. Finish that before creating new event.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUI() {
        hidePb();
        if(liveEvent != null) {
            binding.tvNoEvents.setVisibility(View.GONE);
            binding.llLiveEvent.setVisibility(View.VISIBLE);
            binding.tvLEName.setText(liveEvent.getName().toString());
            binding.tvLELocation.setText(liveEvent.getLocation().toString());
            binding.tvLETime.setText(liveEvent.getTime().toString());
            binding.tvLEDate.setText(liveEvent.getDate().toString());
            binding.llLiveEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Event is live", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadEventDetails(String liveEventId) {
        showPb();
        db.collection("CLEANUP_EVENTS").document(liveEventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            liveEvent = task.getResult().toObject(Event.class);
                            loadUI();
                        } else {
                            Toast.makeText(context, "Some error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hidePb();
                        Toast.makeText(context, "Some error occurred. " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loadData() {
//        ngo = new Gson().fromJson(context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE).getString("NGO", ""), NGO.class);
        db = FirebaseFirestore.getInstance();
        prefs = context.getSharedPreferences("EVENTS_PREFS", Context.MODE_PRIVATE);
        editor = prefs.edit();
        isEventLive = prefs.getBoolean("IS_EVENT_LIVE",false);
        liveEventId = prefs.getString("LIVE_EVENT_ID", null);
        if(liveEventId != null) {
            loadEventDetails(liveEventId);
        }

    }


    private void showPb(){
        binding.pbHome.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        binding.pbHome.setVisibility(View.GONE);
    }
}