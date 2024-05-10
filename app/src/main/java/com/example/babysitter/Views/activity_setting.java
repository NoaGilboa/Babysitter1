package com.example.babysitter.Views;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.GPSTracker;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.Parent;
import com.example.babysitter.R;
import com.example.babysitter.Utilities.FirebaseDataManager;
import com.example.babysitter.Utilities.FirebaseUserManager;
import com.example.babysitter.databinding.ActivitySettingsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class activity_setting extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseDataManager dataManager;
    private FirebaseUserManager userManager;

    private double latitude = 0;
    private double longitude = 0;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = new FirebaseDataManager();
        userManager = new FirebaseUserManager();

        loadData();

        binding.btnSave.setOnClickListener(v -> saveData());
        findViewById(R.id.btnHome).setOnClickListener(v -> navigateToHome());
        binding.etAddress.setOnClickListener(view -> {
            GPSTracker gpsTracker = new GPSTracker(activity_setting.this);
            if (gpsTracker.canGetLocation()) {
                this.latitude = gpsTracker.getLatitude();
                this.longitude = gpsTracker.getLongitude();
                getAddressFromLocation(binding.etAddress, latitude, longitude);
            } else {
                gpsTracker.showSettingsAlert();
            }
        });    }

    private void loadData() {
        if (userManager.isUserLoggedIn()) {
            String uid = userManager.getCurrentUserId();
            dataManager.loadUserData(uid,  new FirebaseDataManager.OnUserDataLoadedListener() {
                @Override
                public void onBabysitterLoaded(Babysitter babysitter) {
                    updateUIWithBabysitter(babysitter);
                }

                @Override
                public void onParentLoaded(Parent parent) {
                    updateUIWithParent(parent);
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(activity_setting.this, "Failed to load data: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            startActivity(new Intent(this, activity_login.class)); // Redirect to login if not logged in
            finish();
        }
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
        this.password=babysitter.getPassword();

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
        this.password=parent.getPassword();

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
        if (!userManager.isUserLoggedIn()) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String email = binding.etMail.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String uid = userManager.getCurrentUserId();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.etAge.getVisibility() == View.VISIBLE) {
            Babysitter babysitter = new Babysitter(uid, name, phone, email, address, this.password,
                    Integer.parseInt(binding.etAge.getText().toString()),
                    binding.rgSmoke.getCheckedRadioButtonId() == R.id.rbSmokeYes,
                    binding.etMaritalStatus.getText().toString(),
                    binding.etDescription.getText().toString(),
                    Double.parseDouble(binding.etHourlyWage.getText().toString()),
                    Double.parseDouble(binding.etExperience.getText().toString()), this.latitude, this.longitude);
            dataManager.saveUserData(babysitter, "Babysitter", new FirebaseDataManager.OnUserDataSavedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(activity_setting.this, "Babysitter information updated successfully.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(activity_setting.this, "Failed to update babysitter information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Parent parent = new Parent(uid, name, phone, email, address, this.password,
                    Integer.parseInt(binding.etNumberOfChildren.getText().toString()), this.latitude, this.longitude);
            dataManager.saveUserData(parent, "Parent", new FirebaseDataManager.OnUserDataSavedListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(activity_setting.this, "Parent information updated successfully.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(activity_setting.this, "Failed to update parent information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
