package com.example.eco_admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eco_admin.R;
import com.example.eco_admin.models.RegisteredUser;
import com.example.eco_admin.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegisteredUserAdapter extends RecyclerView.Adapter<RegisteredUserAdapter.ViewHolder> {

    Context context;
    ArrayList<RegisteredUser> participants;
    TextView tvName;
    TextView tvContact;

    LinearLayout llParticipant;


    public RegisteredUserAdapter(Context context, ArrayList<RegisteredUser> user){
        this.context = context;
        this.participants = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.participants_item,parent,false);
        tvName = view.findViewById(R.id.tvParticipantName);
        tvContact = view.findViewById(R.id.tvParticipantContact);
        llParticipant = view.findViewById(R.id.llParticipant);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tvName.setText(participants.get(position).getName());
        tvContact.setText(participants.get(position).getContact());

        llParticipant.setOnClickListener(view -> {
            Log.i("adi", "onBindViewHolder: ll clicked");
            Log.i("adi", "onBindViewHolder: partipant" + participants.get(position).getName());
            Log.i("adi", "onBindViewHolder: partipant" + participants.get(position).getId());
            showUserDetailsDialog(participants.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public interface Listener {
        void updateEcopoints();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    private void showUserDetailsDialog(RegisteredUser user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);
        dialogBuilder.setView(dialogView);
        TextView tvName = dialogView.findViewById(R.id.tvSelectedName);
        EditText etEcopoints = dialogView.findViewById(R.id.etSelectedEcopoints);
        ProgressBar pbDialog = dialogView.findViewById(R.id.pbDialog);
        AppCompatButton btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        tvName.setText(user.getName());
        etEcopoints.setText(String.valueOf(user.getEcoPoints()));

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Set onClickListener for the update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisteredUser updatedUser = new RegisteredUser();
                updatedUser.setContact(user.getContact());
                updatedUser.setId(user.getId());
                updatedUser.setEcoPoints(Integer.parseInt(etEcopoints.getText().toString()));
                updatedUser.setName(user.getName());
                updatedUser.setEmail(user.getEmail());
                pbDialog.setVisibility(View.VISIBLE);

                Log.i("adi", "onClick: update " + user.getId());
                FirebaseFirestore.getInstance().collection("USERS").document(user.getId())
                        .set(updatedUser)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        pbDialog.setVisibility(View.GONE);
                                        if (task.isSuccessful())
                                            Toast.makeText(context, "Points assigned successfully", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(context, "Some error while assigning points. Try again!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                pbDialog.setVisibility(View.GONE);
                                                Toast.makeText(context, "Some error encountered" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


            }
        });
    }
}

