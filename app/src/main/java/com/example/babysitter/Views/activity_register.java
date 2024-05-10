package com.example.babysitter.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.GPSTracker;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.Parent;
import com.example.babysitter.R;
import com.example.babysitter.Utilities.FirebaseDataManager;
import com.example.babysitter.Utilities.FirebaseUserManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class activity_register extends AppCompatActivity {

    private EditText etName, etPhone, etMail, etAddress, etAge, etMaritalStatus, etDescription, etNumberOfChildren, etPassword, etHourlyWage, etExperience;
    private RadioGroup rgUserType, rgSmoke;
    private RadioButton rbBabysitter, rbParent, rbSmokeYes, rbSmokeNo;
    private TextView alreadyAccount;
    private Button btnRegister;

    double latitude, longitude;
    private LinearLayout smokingLayout; // LinearLayout for smoking TextView and RadioButtons
    private ProgressDialog progressDialog;

    private FirebaseUserManager userManager;
    private FirebaseDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = new FirebaseUserManager();
        dataManager = new FirebaseDataManager();

        initializeUIComponents();
        setupUIListeners();
    }

    private void initializeUIComponents() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etMail);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etMaritalStatus = findViewById(R.id.etMaritalStatus);
        etDescription = findViewById(R.id.etDescription);
        etNumberOfChildren = findViewById(R.id.etNumberOfChildren);
        etPassword = findViewById(R.id.etPassword);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        etExperience = findViewById(R.id.etExperience);

        rgUserType = findViewById(R.id.rgUserType);
        rgSmoke = findViewById(R.id.rgSmoke);
        smokingLayout = findViewById(R.id.smokingLayout);

        rbBabysitter = findViewById(R.id.rbBabysitter);
        rbParent = findViewById(R.id.rbParent);
        rbSmokeYes = findViewById(R.id.rbSmokeYes);
        rbSmokeNo = findViewById(R.id.rbSmokeNo);

        btnRegister = findViewById(R.id.btnRegister);
        alreadyAccount = findViewById(R.id.tvAlreadyAccount);
    }

    private void setupUIListeners() {
        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBabysitter) {
                showBabysitterFields();
            } else if (checkedId == R.id.rbParent) {
                showParentFields();
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());
        alreadyAccount.setOnClickListener(v -> startActivity(new Intent(activity_register.this, activity_login.class)));

        etAddress.setOnClickListener(v -> {
            GPSTracker gpsTracker = new GPSTracker(activity_register.this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                getAddressFromLocation(etAddress,latitude, longitude);
            } else {
                gpsTracker.showSettingsAlert();
            }
        });
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
    private void registerUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        userManager.createUser(email, password, this, new FirebaseUserManager.OnUserCreationListener() {
            @Override
            public void onUserCreated(String uid) {
                progressDialog.dismiss();
                if (rbBabysitter.isChecked()) {
                    Babysitter babysitter = new Babysitter(uid, name, phone, email, address, password,
                            Integer.parseInt(etAge.getText().toString().trim()),
                            rgSmoke.getCheckedRadioButtonId() == R.id.rbSmokeYes,
                            etMaritalStatus.getText().toString().trim(),
                            etDescription.getText().toString().trim(),
                            Double.parseDouble(etHourlyWage.getText().toString().trim()),
                            Double.parseDouble(etExperience.getText().toString().trim()),
                            latitude, longitude);
                    dataManager.saveBabysitter(babysitter);
                } else if (rbParent.isChecked()) {
                    Parent parent = new Parent(uid, name, phone, email, address, password,
                            Integer.parseInt(etNumberOfChildren.getText().toString().trim()),
                            latitude, longitude);
                    dataManager.saveParent(parent);
                }
                Toast.makeText(activity_register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(activity_register.this, activity_login.class));
            }

            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(activity_register.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBabysitterFields() {
        etAge.setVisibility(View.VISIBLE);
        smokingLayout.setVisibility(View.VISIBLE);
        etMaritalStatus.setVisibility(View.VISIBLE);
        etDescription.setVisibility(View.VISIBLE);
        etHourlyWage.setVisibility(View.VISIBLE);
        etExperience.setVisibility(View.VISIBLE);

        etNumberOfChildren.setVisibility(View.GONE);
    }

    private void showParentFields() {
        etAge.setVisibility(View.GONE);
        smokingLayout.setVisibility(View.GONE);
        etMaritalStatus.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);
        etHourlyWage.setVisibility(View.GONE);
        etExperience.setVisibility(View.GONE);

        etNumberOfChildren.setVisibility(View.VISIBLE);
    }
}
