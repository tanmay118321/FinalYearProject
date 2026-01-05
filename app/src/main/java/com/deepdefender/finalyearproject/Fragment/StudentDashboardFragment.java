package com.deepdefender.finalyearproject.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.deepdefender.finalyearproject.ComplaintActivity;
import com.deepdefender.finalyearproject.Login;
import com.deepdefender.finalyearproject.MonthlyBillActivity;
import com.deepdefender.finalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class StudentDashboardFragment extends Fragment {

    // Header
    private TextView txtUser;

    // Menu
    private TextView txtMenuName, txtMenuDesc, txtMenuTime;

    // Quick Actions
    private CardView cardMonthlyBill, cardSubmitComplaint;
    private TextView txtBillDue;

    // Logout
    private Button btnLogout;

    // Firebase
    private DatabaseReference db;
    private TextView tabBreakfast, tabLunch, tabDinner;

    // Firebase

    private String selectedMeal = "lunch"; // default
    public StudentDashboardFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        View view = inflater.inflate(
                R.layout.fragment_student_dashboard,
                container,
                false
        );

        bindViews(view);
        initFirebase();
        setupTabs();
        setupClicks();

        loadMenu(selectedMeal);

        return view;
    }
    private void showLogoutDialog() {

        if (!isAdded()) return;

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(requireActivity(), Login.class));
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
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

        btnLogout = v.findViewById(R.id.btnLogout);
    }

    private void initFirebase() {
        db = FirebaseDatabase.getInstance().getReference();
    }

    private void setupClicks() {

        cardMonthlyBill.setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        MonthlyBillActivity.class)));

        cardSubmitComplaint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        ComplaintActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), Login.class));
            requireActivity().finish();
        });
    }

    // ================= FIREBASE =================

    private void loadUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            txtUser.setText("Student");
        }
    }

    private void loadTodayMenu() {

        db.child("today_menu")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        if (!s.exists()) return;

                        txtMenuName.setText(
                                s.child("name").getValue(String.class));

                        txtMenuDesc.setText(
                                s.child("description").getValue(String.class));

                        txtMenuTime.setText(
                                s.child("time").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
    private void setupTabs() {

        tabBreakfast.setOnClickListener(v -> switchTab("breakfast"));
        tabLunch.setOnClickListener(v -> switchTab("lunch"));
        tabDinner.setOnClickListener(v -> switchTab("dinner"));
    }

    private void switchTab(String meal) {

        selectedMeal = meal;

        resetTabs();

        if (meal.equals("breakfast")) {
            tabBreakfast.setTextColor(requireContext().getColor(R.color.black));
            tabBreakfast.setTextSize(16);
        } else if (meal.equals("lunch")) {
            tabLunch.setTextColor(requireContext().getColor(R.color.black));
            tabLunch.setTextSize(16);
        } else {
            tabDinner.setTextColor(requireContext().getColor(R.color.black));
            tabDinner.setTextSize(16);
        }

        loadMenu(meal);
    }

    private void resetTabs() {
        tabBreakfast.setTextColor(requireContext().getColor(R.color.gray));
        tabLunch.setTextColor(requireContext().getColor(R.color.gray));
        tabDinner.setTextColor(requireContext().getColor(R.color.gray));
    }

    // ================= FIREBASE MENU =================

    private void loadMenu(String mealType) {

        db.child("today_menu")
                .child(mealType)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {

                        if (!isAdded() || getContext() == null) return;
                        if (!s.exists()) return;

                        txtMenuName.setText(
                                s.child("name").getValue(String.class));

                        txtMenuDesc.setText(
                                s.child("description").getValue(String.class));

                        txtMenuTime.setText(
                                s.child("time").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
    private void loadMonthlyBill() {

        db.child("monthlyBills")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        // ✅ Fragment safety check
                        if (!isAdded() || getContext() == null) return;

                        if (!snap.exists()) return;

                        for (DataSnapshot bill : snap.getChildren()) {

                            Long total = bill.child("total")
                                    .getValue(Long.class);

                            Boolean paid = bill.child("paid")
                                    .getValue(Boolean.class);

                            if (total == null) return;

                            if (paid != null && paid) {
                                txtBillDue.setText("● Paid");
                                txtBillDue.setTextColor(
                                        getContext().getColor(R.color.green));
                            } else {
                                txtBillDue.setText("● Due: ₹" + total);
                                txtBillDue.setTextColor(
                                        getContext().getColor(R.color.red));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        txtUser = null;
        txtMenuName = null;
        txtMenuDesc = null;
        txtMenuTime = null;
        txtBillDue = null;
    }


}
