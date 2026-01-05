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

    public StudentDashboardFragment() {}

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

        bindViews(view);
        initFirebase();
        setupClicks();

        loadUser();
        loadTodayMenu();
        loadMonthlyBill();

        return view;
    }

    // ================= VIEW BINDING =================

    private void bindViews(View v) {

        txtUser = v.findViewById(R.id.txtUser);

        txtMenuName = v.findViewById(R.id.txtMenuName);
        txtMenuDesc = v.findViewById(R.id.txtMenuDesc);
        txtMenuTime = v.findViewById(R.id.txtMenuTime);

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

    private void loadMonthlyBill() {

        db.child("monthlyBills")
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

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
                                        requireContext().getColor(
                                                R.color.green));
                            } else {
                                txtBillDue.setText("● Due: ₹" + total);
                                txtBillDue.setTextColor(
                                        requireContext().getColor(
                                                R.color.red));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
