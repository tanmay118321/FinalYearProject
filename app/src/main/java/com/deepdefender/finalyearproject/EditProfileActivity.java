package com.deepdefender.finalyearproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etHostel, etBlock, etRoom;
    private MaterialButton btnSave;
    private TextView btnCancel, btnDiscard, btnSaveTop;
    private ImageView imgProfile, btnChangePhoto;

    private DatabaseReference userRef;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            uploadImageAsBase64(uri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bindViews();
        initFirebase();
        loadProfile();
        setupActions();
    }

    private void bindViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etHostel = findViewById(R.id.etHostel);
        etBlock = findViewById(R.id.etBlock);
        etRoom = findViewById(R.id.etRoom);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDiscard = findViewById(R.id.btnDiscard);
        btnSaveTop = findViewById(R.id.btnSaveTop);

        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
    }

    private void initFirebase() {
        String uid = FirebaseAuth.getInstance().getUid();
        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
    }

    private void loadProfile() {
        userRef.get().addOnSuccessListener(s -> {
            if (!s.exists()) return;

            etName.setText(s.child("name").getValue(String.class));
            etPhone.setText(s.child("phone").getValue(String.class));
            etHostel.setText(s.child("hostel").getValue(String.class));
            etBlock.setText(s.child("block").getValue(String.class));
            etRoom.setText(s.child("room").getValue(String.class));
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            etEmail.setText(user.getEmail());
        } else {
            etEmail.setText("Not Available");
        }
    }

    // 🔥 BASE64 IMAGE UPLOAD
    private void uploadImageAsBase64(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            String base64Image =
                    Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            Bitmap finalBitmap = bitmap;
            userRef.child("profileImageBase64").setValue(base64Image)
                    .addOnSuccessListener(v -> imgProfile.setImageBitmap(finalBitmap));

            is.close();

        } catch (Exception e) {
            Toast.makeText(this,
                    "Image upload failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupActions() {
        btnChangePhoto.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnCancel.setOnClickListener(v -> finish());
        btnDiscard.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());
        btnSaveTop.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {

        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("name", etName.getText().toString().trim());
        map.put("phone", etPhone.getText().toString().trim());
        map.put("hostel", etHostel.getText().toString().trim());
        map.put("block", etBlock.getText().toString().trim());
        map.put("room", etRoom.getText().toString().trim());

        userRef.updateChildren(map)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this,
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Update failed",
                                Toast.LENGTH_SHORT).show());
    }
}
