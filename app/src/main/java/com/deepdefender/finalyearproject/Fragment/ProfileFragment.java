package com.deepdefender.finalyearproject.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.EditProfileActivity;
import com.deepdefender.finalyearproject.Login;
import com.deepdefender.finalyearproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileFragment extends Fragment {

    private TextView txtName, txtStudentId, txtEmail, txtPhone;
    private TextView txtHostel, txtBlock, txtRoom;
    private MaterialButton btnLogout;
    private TextView btnEdit;
    private ImageView imgProfile;

    private DatabaseReference userRef;
    private FirebaseUser user;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile2, container, false);

        bindViews(view);
        initFirebase();
        loadProfile();
        loadProfileImage();
        setupClicks();

        return view;
    }

    private void bindViews(View v) {
        txtName = v.findViewById(R.id.txtName);
        txtStudentId = v.findViewById(R.id.txtStudentId);
        txtEmail = v.findViewById(R.id.txtEmail);
        txtPhone = v.findViewById(R.id.txtPhone);
        txtHostel = v.findViewById(R.id.txtHostel);
        txtBlock = v.findViewById(R.id.txtBlock);
        txtRoom = v.findViewById(R.id.txtRoom);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnEdit = v.findViewById(R.id.btnEdit);
        imgProfile = v.findViewById(R.id.imgProfile);
    }

    private void initFirebase() {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(requireActivity(), Login.class));
            requireActivity().finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());
    }

    private void loadProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                if (!s.exists()) return;

                txtName.setText(getSafe(s, "name"));
                txtStudentId.setText("Student ID: " + getSafe(s, "studentId"));
                txtPhone.setText(getSafe(s, "phone"));
                txtHostel.setText(getSafe(s, "hostel"));
                txtBlock.setText(getSafe(s, "block"));
                txtRoom.setText(getSafe(s, "room"));

                String email = s.child("email").getValue(String.class);
                if (email == null || email.isEmpty()) {
                    email = (user.getEmail() != null)
                            ? user.getEmail()
                            : "Not Available";
                }
                txtEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private String getSafe(DataSnapshot s, String key) {
        String v = s.child(key).getValue(String.class);
        return v == null ? "Not Available" : v;
    }

    // 🔥 BASE64 IMAGE LOAD
    private void loadProfileImage() {
        userRef.child("profileImageBase64")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            imgProfile.setImageResource(R.drawable.avatar_placeholder);
                            return;
                        }

                        String base64 = snapshot.getValue(String.class);
                        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        imgProfile.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void setupClicks() {
        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), EditProfileActivity.class)));

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (d, w) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(requireActivity(), Login.class));
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
