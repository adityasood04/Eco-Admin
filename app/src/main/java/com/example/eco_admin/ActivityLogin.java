package com.example.eco_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eco_admin.databinding.ActivityLoginBinding;
import com.example.eco_admin.models.NGO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class ActivityLogin extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        prefs = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        editor = prefs.edit();

        binding.btnLogIn.setOnClickListener(view->{
            if(binding.etEmailLogin.getText() == null || binding.etEmailLogin.getText().toString().equals("")){
                binding.etEmailLogin.setError("Required");
            }
            if(binding.etPasswordLogin.getText() == null){
                binding.etPasswordLogin.setError("Required");
            }
            if(binding.etPasswordLogin.getText().length() < 8){
                binding.etPasswordLogin.setError("Minimum 8 characters");
            }
            if(binding.etEmailLogin.getText() != null && binding.etPasswordLogin.getText() != null){
                loginUser();
            }
        });

        binding.tvSignUpLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSignupScreen();
            }
        });
    }

    private void launchSignupScreen() {
        startActivity(new Intent(this,ActivityRegister.class));
        finish();
    }

    private void loginUser() {
        showPb();
        auth.signInWithEmailAndPassword(binding.etEmailLogin.getText().toString(),binding.etPasswordLogin.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            getUserDetails(task.getResult());
                        }
                        else{
                            hidePb();
                            Toast.makeText(ActivityLogin.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hidePb();
                        Toast.makeText(ActivityLogin.this, "Login Failed."+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
    private void getUserDetails(AuthResult result) {
        db.collection("Admins").document(result.getUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hidePb();
                        if(task.isSuccessful()){
                            NGO ngo = task.getResult().toObject(NGO.class);
                            saveLocalUser(ngo);
                            launchHomeScreen();
                        }else {
                            Toast.makeText(ActivityLogin.this,"Error fetching user",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hidePb();
                        Toast.makeText(ActivityLogin.this,"Error fetching user",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void launchHomeScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void saveLocalUser(NGO ngo) {
        editor.putString("NGO", new Gson().toJson(ngo));
        editor.apply();
    }
    private void showPb(){
        binding.pbLogin.setVisibility(View.VISIBLE);
    }
    private void hidePb(){
        binding.pbLogin.setVisibility(View.GONE);
    }
}