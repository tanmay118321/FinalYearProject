package com.deepdefender.finalyearproject.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.firebase.database.*;

public class ProfileFragment extends Fragment {

    // UI
    private TextView txtName, txtStudentId, txtEmail, txtPhone;
    private TextView txtHostel, txtBlock, txtRoom;
    private MaterialButton btnLogout;
    private TextView btnEdit;
    private ImageView imgProfile;

    // Firebase
    private DatabaseReference userRef;
    private String uid;

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

    // ================= VIEW BINDING =================
    @SuppressLint("WrongViewCast")
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

    // ================= FIREBASE =================
    private void initFirebase() {
        uid = FirebaseAuth.getInstance().getUid();
        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
    }

    private void loadProfile() {

        userRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {

                        if (!s.exists()) return;

                        txtName.setText(s.child("name").getValue(String.class));
                        txtStudentId.setText(
                                "Student ID: " + s.child("studentId").getValue(String.class)
                        );

                        // ✅ EMAIL FIX (fallback safe)
                        String email = s.child("email").getValue(String.class);
                        if (email == null || email.isEmpty()) {
                            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        }
                        txtEmail.setText(email);

                        txtPhone.setText(s.child("phone").getValue(String.class));
                        txtHostel.setText(s.child("hostel").getValue(String.class));
                        txtBlock.setText(s.child("block").getValue(String.class));
                        txtRoom.setText(s.child("room").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ================= PROFILE IMAGE =================
    private void loadProfileImage() {

        String path = requireContext()
                .getSharedPreferences("profile", Context.MODE_PRIVATE)
                .getString("profile_image_path", null);

        if (path != null) {
            imgProfile.setImageBitmap(
                    BitmapFactory.decodeFile(path)
            );
        } else {
            imgProfile.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    // ================= ACTIONS =================
    private void setupClicks() {

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(),
                        EditProfileActivity.class)));

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
