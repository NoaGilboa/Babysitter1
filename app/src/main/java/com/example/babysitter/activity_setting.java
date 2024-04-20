package com.example.babysitter;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.Users.Babysitter;
import com.example.babysitter.Users.Parent;
import com.example.babysitter.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class activity_setting extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadData();

        binding.btnSave.setOnClickListener(v -> saveData());

        findViewById(R.id.btnHome).setOnClickListener(v -> navigateToHome());

        binding.etAddress.setOnClickListener(view -> {
            GPSTracker gpsTracker = new GPSTracker(activity_setting.this);
            if (gpsTracker.canGetLocation()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                getAddressFromLocation(binding.etAddress, latitude, longitude);
            } else {
                gpsTracker.showSettingsAlert();
            }
        });

    }

    private void loadData() {
        String currentUserId = auth.getCurrentUser().getUid();
        databaseReference.child("Users").child("Babysitter").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Babysitter babysitter = snapshot.getValue(Babysitter.class);
                            if (babysitter != null) {
                                updateUIWithBabysitter(babysitter);
                            }
                        } else {
                            // If not found in "Babysitter", check "Parent"
                            databaseReference.child("Users").child("Parent").child(currentUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Parent parent = snapshot.getValue(Parent.class);
                                                if (parent != null) {
                                                    updateUIWithParent(parent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(activity_setting.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity_setting.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUIWithBabysitter(Babysitter babysitter) {
        // Populate the UI with babysitter data
        binding.etName.setText(babysitter.getName());
        binding.etPhone.setText(babysitter.getPhone());
        binding.etMail.setText(babysitter.getMail());
        binding.etAddress.setText(babysitter.getAddress());
        binding.etAge.setText(String.valueOf(babysitter.getAge()));
        binding.etMaritalStatus.setText(babysitter.getMaritalStatus());
        binding.etDescription.setText(babysitter.getDescription());
        binding.etHourlyWage.setText(String.valueOf(babysitter.getHourlyWage()));
        binding.etExperience.setText(String.valueOf(babysitter.getExperience()));

        // Set the visibility of Babysitter exclusive fields
        binding.etAge.setVisibility(View.VISIBLE);
        binding.rgSmoke.setVisibility(View.VISIBLE);
        binding.etMaritalStatus.setVisibility(View.VISIBLE);
        binding.etDescription.setVisibility(View.VISIBLE);
        binding.etHourlyWage.setVisibility(View.VISIBLE);
        binding.etExperience.setVisibility(View.VISIBLE);

        // Set the visibility of Parent exclusive field to gone
        binding.etNumberOfChildren.setVisibility(View.GONE);

        // Set the radio button for smoking
        if (babysitter.isSmoke()) {
            binding.rgSmoke.check(R.id.rbSmokeYes);
        } else {
            binding.rgSmoke.check(R.id.rbSmokeNo);
        }
    }


    private void updateUIWithParent(Parent parent) {
        // Populate the UI with parent data
        binding.etName.setText(parent.getName());
        binding.etPhone.setText(parent.getPhone());
        binding.etMail.setText(parent.getMail());
        binding.etAddress.setText(parent.getAddress());
        binding.etNumberOfChildren.setText(String.valueOf(parent.getNumberOfChildren()));

        // Set the visibility of Parent exclusive field
        binding.etNumberOfChildren.setVisibility(View.VISIBLE);

        // Set the visibility of Babysitter exclusive fields to gone
        binding.etAge.setVisibility(View.GONE);
        binding.rgSmoke.setVisibility(View.GONE);
        binding.etMaritalStatus.setVisibility(View.GONE);
        binding.etDescription.setVisibility(View.GONE);
        binding.etHourlyWage.setVisibility(View.GONE);
        binding.etExperience.setVisibility(View.GONE);
    }


    private void saveData() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etMail.getText().toString().trim();
        String uid = auth.getCurrentUser().getUid();
        String address = binding.etAddress.getText().toString().trim();
        double latitude = 0;
        double longitude = 0;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.etAge.getVisibility() == View.VISIBLE) {
            // Update babysitter data
            updateBabysitterData(uid, name, phone, email, address, latitude, longitude);
        } else {
            // Update parent data
            updateParentData(uid, name, phone, email, address, latitude, longitude);
        }
    }

    private void updateParentData(String uid, String name, String phone, String email, String address, double latitude, double longitude) {
        DatabaseReference ref = databaseReference.child("Users").child("Parent").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Parent parentOld = dataSnapshot.getValue(Parent.class);
                if (parentOld != null) {
                    String password = parentOld.getPassword();
                    Parent updatedParent = new Parent(uid, name, phone, email, address, password,
                            Integer.parseInt(binding.etNumberOfChildren.getText().toString().trim()), latitude, longitude);

                    ref.setValue(updatedParent)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(activity_setting.this, "Parent information updated successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity_setting.this, "Failed to update parent information.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(activity_setting.this, "Parent data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("updateParentData", "Failed to read parent data", databaseError.toException());
            }
        });
    }



    private void updateBabysitterData(String uid, String name, String phone, String email, String address, double latitude, double longitude) {
        DatabaseReference ref = databaseReference.child("Users").child("Babysitter").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Babysitter babysitterOld = dataSnapshot.getValue(Babysitter.class);
                if (babysitterOld != null) {
                    String password = babysitterOld.getPassword();
                    Babysitter updatedBabysitter = new Babysitter(uid, name, phone, email, address, password,
                            Integer.parseInt(binding.etAge.getText().toString().trim()),
                            binding.rgSmoke.getCheckedRadioButtonId() == R.id.rbSmokeYes,
                            binding.etMaritalStatus.getText().toString().trim(),
                            binding.etDescription.getText().toString().trim(),
                            Double.parseDouble(binding.etHourlyWage.getText().toString().trim()),
                            Double.parseDouble(binding.etExperience.getText().toString().trim()), latitude, longitude);

                    ref.setValue(updatedBabysitter)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(activity_setting.this, "Babysitter information updated successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity_setting.this, "Failed to update babysitter information.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(activity_setting.this, "Babysitter data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("updateBabysitterData", "Failed to read babysitter", databaseError.toException());
            }
        });
    }



    private void navigateToHome() {
        if (binding.etAge.getVisibility() == View.VISIBLE) {
            startActivity(new Intent(activity_setting.this, activity_home_babysitter.class));
        } else {
            startActivity(new Intent(activity_setting.this, activity_home_parent.class));
        }

    }

    public void getAddressFromLocation(EditText etAddress, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressObj = addresses.get(0);
                String address = addressObj.getAddressLine(0);
                etAddress.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}