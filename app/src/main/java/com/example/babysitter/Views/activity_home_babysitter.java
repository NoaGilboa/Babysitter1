package com.example.babysitter.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.Adpters.ParentAdapter;
import com.example.babysitter.Models.BabysittingEvent;
import com.example.babysitter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class activity_home_babysitter extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ParentAdapter adapter;
    private List<BabysittingEvent> messages;
    private FirebaseDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_babysiter);
        recyclerView = findViewById(R.id.rvParents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        adapter = new ParentAdapter(messages, this);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        loadMessages();

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(activity_home_babysitter.this, activity_login.class));
                finish();
            }
        });

        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_home_babysitter.this, activity_setting.class));
            }
        });

        findViewById(R.id.btnSortByOldetoNewer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(messages, new Comparator<BabysittingEvent>() {
                    @Override
                    public int compare(BabysittingEvent m1, BabysittingEvent m2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try {
                            Date date1 = sdf.parse(m1.getSelectedDate());
                            Date date2 = sdf.parse(m2.getSelectedDate());
                            // Null check and compare the dates
                            if (date1 != null && date2 != null) {
                                return date2.compareTo(date1); // For descending order
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.btnSortByNewerToOlder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(messages, new Comparator<BabysittingEvent>() {
                    @Override
                    public int compare(BabysittingEvent m1, BabysittingEvent m2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try {
                            Date date1 = sdf.parse(m1.getSelectedDate());
                            Date date2 = sdf.parse(m2.getSelectedDate());
                            // Null check and compare the dates
                            if (date1 != null && date2 != null) {
                                return date1.compareTo(date2); // For ascending order
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadMessages() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (auth.getCurrentUser() != null) {
            String currentUserId = auth.getCurrentUser().getUid();
            DatabaseReference messagesRef = database.getReference("Messages");

            // Create a query to fetch messages where the babysitterUid equals the current user's UID
            Query query = messagesRef.orderByChild("babysitterUid").equalTo(currentUserId);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messages.clear(); // Clear the existing list to avoid duplicates
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BabysittingEvent message = snapshot.getValue(BabysittingEvent.class);
                        if (message != null) {
                            messages.add(message);
                            Log.d("loadMessages", "Loaded message for babysitter UID: " + currentUserId + ", Message: " + message.getMessageText());
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("loadMessages", "Failed to load messages for babysitter: " + currentUserId, databaseError.toException());
                }
            });

        } else {
            Log.e("loadMessages", "No user logged in");
        }
    }



}