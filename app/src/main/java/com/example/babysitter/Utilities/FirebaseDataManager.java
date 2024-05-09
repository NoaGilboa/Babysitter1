package com.example.babysitter.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.babysitter.Views.activity_home_babysitter;
import com.example.babysitter.Views.activity_home_parent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDataManager {
    private Context context;
    private DatabaseReference usersRef;

    public FirebaseDataManager(Context context) {
        this.context = context;
        this.usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void checkUserRole(String uid, boolean isBabysitter, boolean isParent) {
        usersRef.child(isBabysitter ? "Babysitter" : "Parent").child(uid).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                if (isBabysitter) {
                    context.startActivity(new Intent(context, activity_home_babysitter.class));
                } else if (isParent) {
                    context.startActivity(new Intent(context, activity_home_parent.class));
                }
            } else {
                Log.e("LoginActivity", "User not found in specified role.");
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("LoginActivity", "Error fetching user data: ", e);
        });
    }
}
