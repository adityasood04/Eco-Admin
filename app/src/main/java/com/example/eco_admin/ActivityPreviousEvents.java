package com.example.eco_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eco_admin.adapters.PastEventsAdapter;
import com.example.eco_admin.databinding.ActivityPreviousEventsBinding;
import com.example.eco_admin.models.NGO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityPreviousEvents extends AppCompatActivity {


    private  String ngoUID;
    private ActivityPreviousEventsBinding binding;
    private NGO ngo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviousEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ngoUID = getIntent().getStringExtra("NGO_ID");
        if(ngoUID != null) {
            loadPastEvents(ngoUID);
        }
        binding.ivBackBtn.setOnClickListener(view -> finish());
        
    }

    private void loadPastEvents(String ngoUID) {
        binding.pbPastEvent.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("NGO-Data")
                .document(ngoUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        binding.pbPastEvent.setVisibility(View.GONE);

                        if(task.isSuccessful()){
                            ngo = task.getResult().toObject(NGO.class);
                            if(ngo.getPreviousEvents().size() != 0)
                                setUI(ngo);
                            else {
                                binding.tvNoEvents.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(ActivityPreviousEvents.this, "Error loading events. Try again!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                
    }

    private void setUI(NGO ngo) {
        binding.llPreviousEvents.setVisibility(View.VISIBLE);
        binding.rcvPastEvents.setLayoutManager(new LinearLayoutManager(this));
        PastEventsAdapter adapter = new PastEventsAdapter(this,ngo.getPreviousEvents());
        binding.rcvPastEvents.setAdapter(adapter);
    }
}