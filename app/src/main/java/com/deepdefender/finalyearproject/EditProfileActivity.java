package com.deepdefender.finalyearproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    // UI
    private EditText etName, etEmail, etPhone, etHostel, etBlock, etRoom;
    private MaterialButton btnSave;
    private TextView btnCancel, btnDiscard, btnSaveTop;
    private ImageView imgProfile, btnChangePhoto;

    // Firebase
    private DatabaseReference userRef;
    private String uid;

    // ================= IMAGE PICKER =================
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) return;
                        saveImageToInternalStorage(uri);
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bindViews();
        initFirebase();
        loadProfile();
        loadSavedImage();
        setupActions();
    }

    // ================= VIEW BINDING =================
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

    // ================= FIREBASE =================
    private void initFirebase() {
        uid = FirebaseAuth.getInstance().getUid();
        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
    }

    private void loadProfile() {
        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            etName.setText(snapshot.child("name").getValue(String.class));
            etEmail.setText(snapshot.child("email").getValue(String.class));
            etPhone.setText(snapshot.child("phone").getValue(String.class));
            etHostel.setText(snapshot.child("hostel").getValue(String.class));
            etBlock.setText(snapshot.child("block").getValue(String.class));
            etRoom.setText(snapshot.child("room").getValue(String.class));
        });
    }

    // ================= IMAGE =================
    private void loadSavedImage() {

        String path = getSharedPreferences("profile", MODE_PRIVATE)
                .getString("profile_image_path", null);

        if (path != null) {
            imgProfile.setImageBitmap(
                    BitmapFactory.decodeFile(path)
            );
        } else {
            imgProfile.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile.jpg");

            FileOutputStream outputStream = new FileOutputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            outputStream.close();
            inputStream.close();

            getSharedPreferences("profile", MODE_PRIVATE)
                    .edit()
                    .putString("profile_image_path", file.getAbsolutePath())
                    .apply();

            imgProfile.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Failed to load image",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ================= ACTIONS =================
    private void setupActions() {

        btnChangePhoto.setOnClickListener(v ->
                imagePicker.launch("image/*"));

        btnCancel.setOnClickListener(v -> finish());
        btnDiscard.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());
        btnSaveTop.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {

        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,
                    "Name is required",
                    Toast.LENGTH_SHORT).show();
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
                                "Failed to update profile",
                                Toast.LENGTH_SHORT).show());
    }
}
