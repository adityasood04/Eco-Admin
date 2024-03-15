package com.example.eco_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eco_admin.databinding.ActivityRegisterBinding;
import com.example.eco_admin.models.Credentials;
import com.example.eco_admin.models.NGO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class ActivityRegister extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        editor = prefs.edit();
        binding.btnRegister.setOnClickListener(view -> {
            //TODO: Add checks for entered values
//            startActivity(new Intent(this,MainActivity.class));

            signupNGO();
        });
    }

    private void signupNGO() {
        Log.i("adi", "sign in initiated ");

        Credentials credentials = new Credentials();
        credentials.setEmail(binding.etEmail.getText().toString());
        credentials.setPassword(binding.etPassword.getText().toString());
        showPb();
        auth.createUserWithEmailAndPassword(credentials.getEmail(), credentials.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            saveToDb(task);
                        } else {
                            hidePb();
                            Toast.makeText(ActivityRegister.this, "Registration failed. Try again!!", Toast.LENGTH_SHORT).show();
                            Log.i("adi", "onComplete signup: error ");
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hidePb();
                        Log.i("adi", "onComplete signup: error " + e.getMessage());
                        Toast.makeText(ActivityRegister.this, "Some error encountered. Try again!!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void saveToDb(Task<AuthResult> task) {
        NGO ngo = new NGO();
        //TODO Checks for empty or null
        ngo.setName(binding.etOrganisationName.getText().toString());
        ngo.setAddress(binding.etOfficialAddress.getText().toString());
        ngo.setRegistrationNo(binding.etRegistrationNo.getText().toString());
        ngo.setContactNo(Long.parseLong(binding.etContactNo.getText().toString()));
        ngo.setWebsite(binding.etWebsite.getText().toString());
        ngo.setEmail(binding.etEmail.getText().toString());
        ngo.setUid(task.getResult().getUser().getUid());

        db.collection("NGO-Data").document(task.getResult().getUser().getUid())
                .set(ngo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hidePb();
                        if(task.isSuccessful()){
                            saveLocalUser(ngo);
                            startActivity(new Intent(ActivityRegister.this,MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ActivityRegister.this, "Error while saving details. Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityRegister.this, "Error while saving details. Try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveLocalUser(NGO ngo) {
        editor.putString("NGO", new Gson().toJson(ngo));
        editor.apply();
    }


    private void showPb(){
        binding.pbRegister.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        binding.pbRegister.setVisibility(View.GONE);
    }
}