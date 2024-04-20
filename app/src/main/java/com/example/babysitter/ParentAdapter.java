package com.example.babysitter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babysitter.Users.Parent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ParentViewHolder> {
    private List<Message> messages;
    private Context context;

    public ParentAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parent, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getParentUid() != null && !message.getParentUid().isEmpty()) {
            DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Parent").child(message.getParentUid());
            parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Parent parent = dataSnapshot.getValue(Parent.class);
                        Log.d("ParentAdapter", "Parent: " + parent.getName() + ", " + parent.getPhone());

                        if (parent != null) {
                            holder.tvParentName.setText("Parent Name: " + parent.getName());
                            holder.tvParentPhone.setText("Parent Phone: " + parent.getPhone());
                            holder.tvParentEmail.setText("Parent Email: " + parent.getMail());
                            holder.tvParentAddress.setText("Parent Address: " + parent.getAddress());
                            holder.tvParentNumberOfChildren.setText("Number of Children: " + parent.getNumberOfChildren());
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ParentAdapter", "Failed to load parent details", error.toException());
            }
        });
        } else {
            Log.d("ParentAdapter", "Message with missing parentUid: " + message.getMessageText());
        }
        // Set message details
        holder.tvMessage.setText("Message: " + message.getMessageText());
        holder.tvSelectedDate.setText("Date: " + message.getSelectedDate());
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView tvParentName, tvParentPhone, tvParentEmail, tvParentAddress, tvParentNumberOfChildren, tvMessage, tvSelectedDate;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParentName = itemView.findViewById(R.id.tvParentName);
            tvParentPhone = itemView.findViewById(R.id.tvParentPhone);
            tvParentEmail = itemView.findViewById(R.id.tvParentEmail);
            tvParentAddress = itemView.findViewById(R.id.tvParentAddress);
            tvParentNumberOfChildren = itemView.findViewById(R.id.tvParentNumberOfChildren);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSelectedDate = itemView.findViewById(R.id.tvSelectedDate);
        }
    }
}
