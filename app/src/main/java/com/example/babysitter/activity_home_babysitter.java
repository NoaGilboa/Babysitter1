package com.example.babysitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private List<Message> messages;
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
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message m1, Message m2) {
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
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message m1, Message m2) {
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
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Note: Adjusted path to match your database structure
        DatabaseReference messagesRef = database.getReference("Messages").child("babysitterUID: " + currentUserId);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through each parent UID under the babysitter
                    for (DataSnapshot messageSnapshot : parentSnapshot.getChildren()) {
                        // Now, we are iterating through each message under each parent UID
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message != null) {
                            messages.add(message);
                            Log.d("loadMessages", "Message added pid: " + message.getParentUid());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadMessages", "Failed to load data", error.toException());
            }
        });
    }


}
