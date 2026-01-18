package com.deepdefender.finalyearproject.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.ComplaintActivity;
import com.deepdefender.finalyearproject.Login;
import com.deepdefender.finalyearproject.MonthlyBillActivity;
import com.deepdefender.finalyearproject.R;
import com.deepdefender.finalyearproject.UserAttendanceActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;

public class StudentDashboardFragment extends Fragment {

    // Header
    private TextView txtUser;

    // Menu
    private TextView txtMenuName, txtMenuDesc, txtMenuTime;

    // Tabs
    private TextView tabBreakfast, tabLunch, tabDinner;

    // Cards
    private CardView cardMonthlyBill, cardSubmitComplaint,cardAttendance;
    private TextView txtBillDue;

    private ShapeableImageView imgProfile;
    private Button btnLogout;

    // Firebase
    private DatabaseReference db;
    private String uid;

    private String selectedMeal = "lunch";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_student_dashboard,
                container,
                false
        );
        FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                .addOnCompleteListener(task -> {});
        bindViews(view);
        initFirebase();
        setupTabs();
        setupClicks();

        loadUser();
        loadMenu(selectedMeal);
        loadMonthlyBill();
        loadProfileImage(); // 🔥 BASE64 LOAD

        return view;
    }

    // ================= VIEW BINDING =================

    private void bindViews(View v) {

        txtUser = v.findViewById(R.id.txtUser);
        txtMenuName = v.findViewById(R.id.txtMenuName);
        txtMenuDesc = v.findViewById(R.id.txtMenuDesc);
        txtMenuTime = v.findViewById(R.id.txtMenuTime);

        tabBreakfast = v.findViewById(R.id.tabBreakfast);
        tabLunch = v.findViewById(R.id.tabLunch);
        tabDinner = v.findViewById(R.id.tabDinner);

        cardMonthlyBill = v.findViewById(R.id.cardMonthlyBill);
        cardSubmitComplaint = v.findViewById(R.id.cardSubmitComplaint);
        txtBillDue = v.findViewById(R.id.txtBillDue);

        imgProfile = v.findViewById(R.id.imgProfile);
        btnLogout = v.findViewById(R.id.btnLogout);
        cardAttendance=v.findViewById(R.id.cardAttendance);
        cardAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), UserAttendanceActivity.class);
                startActivity(intent);
            }
        });
    }

    // ================= FIREBASE =================

    private void initFirebase() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(requireActivity(), Login.class));
            requireActivity().finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getUid();
        db = FirebaseDatabase.getInstance().getReference();
    }

    private void loadUser() {

        db.child("users")
                .child(uid)
                .child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!isAdded()) return;

                        if (snapshot.exists()) {
                            String name = snapshot.getValue(String.class);
                            txtUser.setText("Hello, " + name);
                        } else {
                            txtUser.setText("Hello, Student");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        txtUser.setText("Hello, Student");
                    }
                });
    }

    // ================= PROFILE IMAGE (BASE64) =================

    private void loadProfileImage() {

        db.child("users")
                .child(uid)
                .child("profileImageBase64")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!isAdded()) return;

                        if (!snapshot.exists()) {
                            imgProfile.setImageResource(R.drawable.avatar_placeholder);
                            return;
                        }

                        String base64 = snapshot.getValue(String.class);

                        try {
                            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                            Bitmap bitmap =
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgProfile.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            imgProfile.setImageResource(R.drawable.avatar_placeholder);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // ================= MENU =================

    private void setupTabs() {
        tabBreakfast.setOnClickListener(v -> switchTab("breakfast"));
        tabLunch.setOnClickListener(v -> switchTab("lunch"));
        tabDinner.setOnClickListener(v -> switchTab("dinner"));
    }

    private void switchTab(String meal) {
        selectedMeal = meal;
        resetTabs();

        if (meal.equals("breakfast")) tabBreakfast.setTextColor(requireContext().getColor(R.color.black));
        else if (meal.equals("lunch")) tabLunch.setTextColor(requireContext().getColor(R.color.black));
        else tabDinner.setTextColor(requireContext().getColor(R.color.black));

        loadMenu(meal);
    }

    private void resetTabs() {
        tabBreakfast.setTextColor(requireContext().getColor(R.color.gray));
        tabLunch.setTextColor(requireContext().getColor(R.color.gray));
        tabDinner.setTextColor(requireContext().getColor(R.color.gray));
    }

    private void loadMenu(String mealType) {

        db.child("today_menu")
                .child(mealType)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {

                        if (!isAdded() || !s.exists()) return;

                        txtMenuName.setText(s.child("name").getValue(String.class));
                        txtMenuDesc.setText(s.child("description").getValue(String.class));
                        txtMenuTime.setText(s.child("time").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // ================= MONTHLY BILL =================

    private void loadMonthlyBill() {

        db.child("monthlyBills")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        if (!isAdded() || !snap.exists()) return;

                        for (DataSnapshot bill : snap.getChildren()) {

                            Long total = bill.child("total").getValue(Long.class);
                            Boolean paid = bill.child("paid").getValue(Boolean.class);

                            if (total == null) return;

                            if (paid != null && paid) {
                                txtBillDue.setText("● Paid");
                                txtBillDue.setTextColor(requireContext().getColor(R.color.green));
                            } else {
                                txtBillDue.setText("● Due: ₹" + total);
                                txtBillDue.setTextColor(requireContext().getColor(R.color.red));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // ================= CLICKS =================

    private void setupClicks() {

        cardMonthlyBill.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MonthlyBillActivity.class)));

        cardSubmitComplaint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ComplaintActivity.class)));

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {

        if (!isAdded()) return;

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
