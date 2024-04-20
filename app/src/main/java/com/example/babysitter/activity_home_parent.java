package com.example.babysitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.Users.Babysitter;
import com.example.babysitter.Users.Parent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class activity_home_parent extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BabysitterAdapter adapter;
    private List<Babysitter> babysitters;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_parent);

        recyclerView = findViewById(R.id.rvBabysitters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        babysitters = new ArrayList<>();
        adapter = new BabysitterAdapter(babysitters, this);
        recyclerView.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        loadBabysitters();

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(activity_home_parent.this, activity_login.class));
                finish();
            }
        });

        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_home_parent.this, activity_setting.class));
            }
        });

        findViewById(R.id.btnSortByExperience).setOnClickListener(v -> {
            Collections.sort(babysitters, (b1, b2) -> Double.compare(b2.getExperience(), b1.getExperience()));
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

        findViewById(R.id.btnSortByHourlyWage).setOnClickListener(v -> {
            Collections.sort(babysitters, (b1, b2) -> Double.compare(b2.getHourlyWage(), b1.getHourlyWage()));
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

        findViewById(R.id.btnSortByDistance).setOnClickListener(v -> {
            sortBabysittersByDistance();
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

    }

    private void sortBabysittersByDistance() {
        String currentUserId = auth.getCurrentUser().getUid();
        database.getReference("Users").child("Parent").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Parent parent = dataSnapshot.getValue(Parent.class);
                if (parent != null) {
                    double parentLatitude = parent.getLatitude();
                    double parentLongitude = parent.getLongitude();

                    Collections.sort(babysitters, new Comparator<Babysitter>() {
                        @Override
                        public int compare(Babysitter b1, Babysitter b2) {
                            double distanceToB1 = calculateDistance(parentLatitude, parentLongitude, b1.getLatitude(), b1.getLongitude());
                            double distanceToB2 = calculateDistance(parentLatitude, parentLongitude, b2.getLatitude(), b2.getLongitude());
                            return Double.compare(distanceToB1, distanceToB2);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchParent", "Failed to read parent", databaseError.toException());
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        try {
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c; // convert to kilometers

            distance = Math.pow(distance, 2);
            return Math.sqrt(distance);
        } catch (Exception e) {
            Log.e("calculateDistance", "Error calculating distance", e);
            return -1; // Return an invalid distance in case of error
        }
    }


    private void loadBabysitters() {
        DatabaseReference babysittersRef = database.getReference("Users").child("Babysitter");
        babysittersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                babysitters.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Babysitter babysitter = snapshot.getValue(Babysitter.class);
                    if (babysitter != null) {
                        babysitter.setUid(snapshot.getKey());
                        babysitters.add(babysitter);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadBabysitters", "Failed to load data", error.toException());
            }
        });
    }
}

