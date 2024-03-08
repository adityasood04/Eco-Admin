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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityLogin extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        startActivity(new Intent(this,ActivitySignup.class));
        finish();
    }

    private void loginUser() {
        auth.signInWithEmailAndPassword(binding.etEmailLogin.getText().toString(),binding.etPasswordLogin.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            getUserDetails(task.getResult());
                        }
                        else{
                            Toast.makeText(ActivityLogin.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityLogin.this, "Login Failed."+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
    private void getUserDetails(AuthResult result) {
        db.collection("Admins").document(result.getUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
//                            NGO ngo = task.getResult().toObject(NGO.class);
//                            saveLocalUser(ngo);
//                            launchHomeScreen();
                        }else {
                            Toast.makeText(ActivityLogin.this,"Error fetching user",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}