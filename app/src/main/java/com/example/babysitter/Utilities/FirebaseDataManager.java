package com.example.babysitter.Utilities;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.BabysittingEvent;
import com.example.babysitter.Models.Parent;
import com.example.babysitter.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseDataManager {
    private FirebaseDatabase database;

    public FirebaseDataManager() {
        database = FirebaseDatabase.getInstance();
    }

    public void saveBabysitter(Babysitter babysitter) {
        database.getReference().child("Users").child("Babysitter").child(babysitter.getUid()).setValue(babysitter);
    }

    public void saveParent(Parent parent) {
        database.getReference().child("Users").child("Parent").child(parent.getUid()).setValue(parent);
    }

    public void checkUserRole(String uid, String role, OnRoleCheckListener listener) {
        database.getReference().child("Users").child(role).child(uid).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        listener.onRoleConfirmed();
                    } else {
                        listener.onRoleDenied();
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    public void loadUserData(String uid, OnUserDataLoadedListener listener) {
        DatabaseReference usersRef = database.getReference().child("Users");

        // Check each user type path until the correct one is found
        checkUserTypeAndLoad(usersRef.child("Babysitter"), uid, "Babysitter", listener, () ->
                checkUserTypeAndLoad(usersRef.child("Parent"), uid, "Parent", listener, () ->
                        listener.onFailure(new Exception("User not found"))
                )
        );
    }

    private void checkUserTypeAndLoad(DatabaseReference userTypeRef, String uid, String type, OnUserDataLoadedListener listener, Runnable onNotFound) {
        userTypeRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if ("Babysitter".equals(type)) {
                        listener.onBabysitterLoaded(dataSnapshot.getValue(Babysitter.class));
                    } else if ("Parent".equals(type)) {
                        listener.onParentLoaded(dataSnapshot.getValue(Parent.class));
                    }
                } else {
                    onNotFound.run();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }


    public void saveUserData(Object user, OnUserDataSavedListener listener) {
        String userType = user instanceof Babysitter ? "Babysitter" : "Parent";
        String uid = (user instanceof Babysitter) ? ((Babysitter) user).getUid() : ((Parent) user).getUid();
        DatabaseReference userRef = database.getReference().child("Users").child(userType).child(uid);
        userRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSuccess();
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    public void loadAllBabysitters(OnBabysittersLoadedListener listener) {
        DatabaseReference babysittersRef = database.getReference("Users").child("Babysitter");
        babysittersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Babysitter> loadedBabysitters = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Babysitter babysitter = snapshot.getValue(Babysitter.class);
                    if (babysitter != null) {
                        loadedBabysitters.add(babysitter);
                    }
                }
                listener.onBabysittersLoaded(loadedBabysitters);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void sortBabysittersByDistance(String userId, List<Babysitter> babysitters, OnBabysittersSortedListener listener) {
        DatabaseReference parentRef = database.getReference("Users").child("Parent").child(userId);
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Parent parent = dataSnapshot.getValue(Parent.class);
                if (parent != null && parent.getLatitude() != 0 && parent.getLongitude() != 0) {
                    Collections.sort(babysitters, (b1, b2) -> {
                        double dist1 = calculateDistance(parent.getLatitude(), parent.getLongitude(), b1.getLatitude(), b1.getLongitude());
                        double dist2 = calculateDistance(parent.getLatitude(), parent.getLongitude(), b2.getLatitude(), b2.getLongitude());
                        return Double.compare(dist1, dist2);
                    });
                    listener.onSorted(new ArrayList<>(babysitters)); // Ensure a new list instance is passed
                } else {
                    Log.e("SortDistance", "Invalid or missing parent location data.");
                    listener.onFailure(new Exception("Invalid or missing parent location data"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    public void loadBabysittingEvents(String babysitterUid, OnEventsLoadedListener listener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query eventsQuery = databaseRef.child("Messages").orderByChild("babysitterUid").equalTo(babysitterUid);
        eventsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<BabysittingEvent> events = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BabysittingEvent event = snapshot.getValue(BabysittingEvent.class);
                    if (event != null) {
                        events.add(event);
                    } else {
                        Log.e("FirebaseDataManager", "Failed to parse babysitting event from Firebase.");
                    }
                }
                if (!events.isEmpty()) {
                    listener.onEventsLoaded(events);
                } else {
                    listener.onFailure(new Exception("No events found for babysitter UID: " + babysitterUid));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDataManager", "Error loading events: " + databaseError.getMessage());
                listener.onFailure(new Exception("Failed to load babysitting events: " + databaseError.getMessage()));
            }
        });
    }

    public interface OnEventsLoadedListener {
        void onEventsLoaded(List<BabysittingEvent> events);
        void onFailure(Exception exception);
    }

    public interface OnBabysittersSortedListener {
        void onSorted(List<Babysitter> sortedBabysitters);

        void onFailure(Exception exception);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public interface OnBabysittersLoadedListener {
        void onBabysittersLoaded(List<Babysitter> babysitters);

        void onFailure(Exception exception);
    }


    public interface OnUserDataLoadedListener {
        void onBabysitterLoaded(Babysitter babysitter);

        void onParentLoaded(Parent parent);

        void onFailure(Exception exception);
    }

    public interface OnUserDataSavedListener {
        void onSuccess();

        void onFailure(Exception exception);
    }

    public interface OnRoleCheckListener {
        void onRoleConfirmed();

        void onRoleDenied();

        void onError(Exception e);
    }


}