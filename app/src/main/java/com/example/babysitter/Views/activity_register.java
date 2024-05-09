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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babysitter.GPSTracker;
import com.example.babysitter.Models.Babysitter;
import com.example.babysitter.Models.Parent;
import com.example.babysitter.R;
import com.example.babysitter.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class activity_register extends AppCompatActivity {

    private EditText etName, etPhone, etMail, etAddress, etAge, etMaritalStatus, etDescription, etNumberOfChildren, etPassword, etHourlyWage, etExperience;
    private RadioGroup rgUserType, rgSmoke;
    private RadioButton rbBabysitter, rbParent, rbSmokeYes, rbSmokeNo;
    private TextView alreadyAccount;
    private Button btnRegister;

    double latitude,longitude;
    private LinearLayout smokingLayout; // LinearLayout for smoking TextView and RadioButtons
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize all EditText, RadioGroup, RadioButton, and Button components
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etMail);
        etAddress = findViewById(R.id.etAddress);
        etAge = findViewById(R.id.etAge);
        etMaritalStatus = findViewById(R.id.etMaritalStatus);
        etDescription = findViewById(R.id.etDescription);
        etNumberOfChildren = findViewById(R.id.etNumberOfChildren);
        etPassword = findViewById(R.id.etPassword);
        etHourlyWage = findViewById(R.id.etHourlyWage); // Hourly Wage EditText
        etExperience=findViewById(R.id.etExperience);

        rgUserType = findViewById(R.id.rgUserType);
        rgSmoke = findViewById(R.id.rgSmoke);
        smokingLayout = findViewById(R.id.smokingLayout); // LinearLayout for smoking

        rbBabysitter = findViewById(R.id.rbBabysitter);
        rbParent = findViewById(R.id.rbParent);
        rbSmokeYes = findViewById(R.id.rbSmokeYes);
        rbSmokeNo = findViewById(R.id.rbSmokeNo);

        btnRegister = findViewById(R.id.btnRegister);
        alreadyAccount =findViewById(R.id.tvAlreadyAccount);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");


        // Toggle visibility of fields based on user type selection
        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbBabysitter) {
                    showBabysitterFields();
                } else if(checkedId == R.id.rbParent) {
                    showParentFields();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_register.this,activity_login.class);
                startActivity(intent);
            }
        });

        etAddress.setOnClickListener(view -> {
            GPSTracker gpsTracker = new GPSTracker(activity_register.this);
            if(gpsTracker.canGetLocation()){
                 latitude = gpsTracker.getLatitude();
                 longitude = gpsTracker.getLongitude();
                getAddressFromLocation(etAddress,latitude,longitude);
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
    private void showBabysitterFields() {
        etAge.setVisibility(View.VISIBLE);
        smokingLayout.setVisibility(View.VISIBLE); // Show the LinearLayout for smoking
        etMaritalStatus.setVisibility(View.VISIBLE);
        etDescription.setVisibility(View.VISIBLE);
        etHourlyWage.setVisibility(View.VISIBLE); // Show the hourly wage field
        etExperience.setVisibility(View.VISIBLE);

        etNumberOfChildren.setVisibility(View.GONE);
    }

    private void showParentFields() {
        etAge.setVisibility(View.GONE);
        smokingLayout.setVisibility(View.GONE); // Hide the LinearLayout for smoking
        etMaritalStatus.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);
        etHourlyWage.setVisibility(View.GONE); // Hide the hourly wage field
        etExperience.setVisibility(View.GONE);

        etNumberOfChildren.setVisibility(View.VISIBLE);
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();


        if(name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    String uid=task.getResult().getUser().getUid();
                    if(rbBabysitter.isChecked()) {
                        int age = Integer.parseInt(etAge.getText().toString().trim());
                        boolean smoke = rgSmoke.getCheckedRadioButtonId() == R.id.rbSmokeYes;
                        String maritalStatus = etMaritalStatus.getText().toString().trim();
                        String description = etDescription.getText().toString().trim();
                        double hourlyWage = Double.parseDouble(etHourlyWage.getText().toString().trim());
                        double experience=Double.parseDouble(etExperience.getText().toString().trim());
                        Babysitter babysitter = new Babysitter(uid,name, phone, email, address, password, age, smoke, maritalStatus, description, hourlyWage,experience,latitude,longitude);
                        database.getReference().child("Users").child("Babysitter").child(uid).setValue(babysitter);
                    } else if(rbParent.isChecked()) {
                        int numberOfChildren = Integer.parseInt(etNumberOfChildren.getText().toString().trim());

                        Parent parent = new Parent(uid,name, phone, email, address, password, numberOfChildren,latitude,longitude);
                        database.getReference().child("Users").child("Parent").child(uid).setValue(parent);
                    }

                    Toast.makeText(activity_register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity_register.this,activity_login.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(activity_register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
