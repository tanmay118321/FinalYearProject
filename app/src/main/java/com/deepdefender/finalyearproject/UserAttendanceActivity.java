package com.deepdefender.finalyearproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserAttendanceActivity extends AppCompatActivity {

    MaterialButton btnVerify;
    ImageButton btnBack;
    TextView tvGeoStatus;

    DatabaseReference ref;
    FusedLocationProviderClient locationClient;

    String selectedMeal = "Lunch";
    double messLat = 21.4437406, messLng = 79.9878234;

    String studentId, studentName, room, profileImageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attendance);

        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);
        tvGeoStatus = findViewById(R.id.tvGeoStatus);

        ref = FirebaseDatabase.getInstance().getReference("Attendance");
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        loadUserDetails();

        btnVerify.setOnClickListener(v -> checkLocationAndVerify());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserDetails() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        studentName = snap.child("name").getValue(String.class);
                        room = snap.child("room").getValue(String.class);
                        studentId = snap.child("phone").getValue(String.class);
                        profileImageBase64 = snap.child("profileImageBase64").getValue(String.class);
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void checkLocationAndVerify() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        locationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener(loc -> {

            if (loc == null) {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                return;
            }

            float[] result = new float[1];
            Location.distanceBetween(loc.getLatitude(), loc.getLongitude(),
                    messLat, messLng, result);

            if (result[0] <= 100) biometricVerify();
            else {
                tvGeoStatus.setText("Outside Geofence Range");
                tvGeoStatus.setTextColor(Color.RED);
            }
        });
    }

    private void biometricVerify() {

        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        markAttendance();
                    }
                });

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verify Identity")
                .setSubtitle("Confirm to mark attendance")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(info);
    }

    private void markAttendance() {

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        ref.child(date).child(selectedMeal).child(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        if (snap.exists()) {
                            Toast.makeText(UserAttendanceActivity.this,
                                    "Attendance already marked",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            AttendanceModel model =
                                    new AttendanceModel(studentName, room, "CHECKED IN", time, profileImageBase64);

                            ref.child(date).child(selectedMeal).child(studentId).setValue(model);

                            Toast.makeText(UserAttendanceActivity.this,
                                    "Attendance Marked Successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
