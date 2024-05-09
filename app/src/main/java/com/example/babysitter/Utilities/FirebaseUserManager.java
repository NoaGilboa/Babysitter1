package com.example.babysitter.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUserManager {
    private Context context;
    private FirebaseAuth mAuth;

    public FirebaseUserManager(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password, ProgressDialog progressDialog, boolean isBabysitter, boolean isParent) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        new FirebaseDataManager(context).checkUserRole(mAuth.getCurrentUser().getUid(), isBabysitter, isParent);
                    } else {
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
