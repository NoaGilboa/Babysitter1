package com.example.babysitter.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.R;
import com.example.babysitter.Utilities.FirebaseDataManager;
import com.example.babysitter.Utilities.FirebaseUserManager;
import com.example.babysitter.databinding.ActivityLoginBinding;

public class activity_login extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    FirebaseUserManager userManager;
    FirebaseDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new FirebaseUserManager();
        dataManager = new FirebaseDataManager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please Wait...");

        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            if (!email.isEmpty() && !password.isEmpty()) {
                progressDialog.show();
                userManager.loginUser(email, password, this, new FirebaseUserManager.OnLoginListener() {
                    @Override
                    public void onLoginSuccess(String uid) {
                        checkUserRole(uid);
                    }

                    @Override
                    public void onLoginFailure(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(activity_login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(activity_login.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
            }
        });

        binding.alreadyAccount.setOnClickListener(v -> startActivity(new Intent(activity_login.this, activity_register.class)));
    }

    private void checkUserRole(String uid) {
        String role = binding.rbBabysitter.isChecked() ? "Babysitter" : "Parent";
        dataManager.checkUserRole(uid, role, new FirebaseDataManager.OnRoleCheckListener() {
            @Override
            public void onRoleConfirmed() {
                progressDialog.dismiss();
                if (binding.rbBabysitter.isChecked()) {
                    startActivity(new Intent(activity_login.this, activity_home_babysitter.class));
                } else {
                    startActivity(new Intent(activity_login.this, activity_home_parent.class));
                }
                finish();
            }

            @Override
            public void onRoleDenied() {
                progressDialog.dismiss();
                Toast.makeText(activity_login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(activity_login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
