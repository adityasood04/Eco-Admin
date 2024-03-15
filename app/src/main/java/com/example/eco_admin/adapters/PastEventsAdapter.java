package com.example.eco_admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eco_admin.R;
import com.example.eco_admin.models.PreviousEvent;
import com.example.eco_admin.models.RegisteredUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PastEventsAdapter extends RecyclerView.Adapter<PastEventsAdapter.ViewHolder> {

    Context context;
    ArrayList<PreviousEvent> previousEvents;
    TextView tvName;
    TextView tvLocation;
    TextView tvDate;
    TextView tvNoOfParticipants;


    public PastEventsAdapter(Context context, ArrayList<PreviousEvent> previousEvents){
        this.context = context;
        this.previousEvents = previousEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.previous_event_item,parent,false);
        tvName = view.findViewById(R.id.tvPEName);
        tvLocation = view.findViewById(R.id.tvPELocation);
        tvDate = view.findViewById(R.id.tvPEDate);
        tvNoOfParticipants = view.findViewById(R.id.tvPECount);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvName.setText(previousEvents.get(position).getName());
        tvLocation.setText(previousEvents.get(position).getLocation());
        tvDate.setText(previousEvents.get(position).getDate());
        tvNoOfParticipants.setText(String.valueOf(previousEvents.get(position).getNoOfParticipants()));
    }

    @Override
    public int getItemCount() {
        return previousEvents.size();
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}

