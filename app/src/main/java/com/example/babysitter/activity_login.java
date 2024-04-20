package com.example.babysitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activity_login extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        progressDialog = new ProgressDialog(activity_login.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please Wait");


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etEmail.getText().toString().isEmpty() && !binding.etPassword.getText().toString().isEmpty()) {
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                        // Attempt to fetch user data from both "Babysitter" and "Parent" nodes
                                        usersRef.child("Babysitter").child(uid).get().addOnSuccessListener(dataSnapshot -> {
                                            if (dataSnapshot.exists()) {
                                                if (binding.rbBabysitter.isChecked()) {
                                                    // This means the user is a Babysitter
                                                    startActivity(new Intent(activity_login.this, activity_home_babysitter.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(activity_login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                }

                                            } else {
                                                // If not found in "Babysitter", try "Parent"
                                                usersRef.child("Parent").child(uid).get().addOnSuccessListener(dataSnapshot1 -> {
                                                    if (dataSnapshot1.exists()) {
                                                        if (binding.rbParent.isChecked()) {
                                                            // This means the user is a Parent
                                                            startActivity(new Intent(activity_login.this, activity_home_parent.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(activity_login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        // User not found in either "Babysitter" or "Parent"
                                                        Log.e("LoginActivity", "User not found in either Babysitter or Parent nodes.");
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(e -> {
                                            // Handle any errors
                                            Log.e("LoginActivity", "Error fetching user data: ", e);
                                        });
                                    } else {
                                        Toast.makeText(activity_login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(activity_login.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login.this, activity_register.class);
                startActivity(intent);
            }
        });
    }
}