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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDashboardFragment extends Fragment {

    // Header
    TextView txtUser;

    // Menu
    TextView txtMenuName, txtMenuDesc, txtMenuTime;

    // Quick Actions
    LinearLayout layoutActions;
    CardView cardMonthlyBill, cardSubmitComplaint;
    TextView txtBillDue;

    // Logout
    Button btnLogout;

    // Firebase
    DatabaseReference db;

    public StudentDashboardFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_dashboard, container, false);

        // ===== Bind fixed IDs =====
        txtUser = view.findViewById(R.id.txtUser);
        txtMenuName = view.findViewById(R.id.txtMenuName);
        txtMenuDesc = view.findViewById(R.id.txtMenuDesc);
        txtMenuTime = view.findViewById(R.id.txtMenuTime);
        layoutActions = view.findViewById(R.id.layoutActions);
        btnLogout = view.findViewById(R.id.btnLogout);

        // ===== Safe card extraction =====
        cardMonthlyBill = (CardView) layoutActions.getChildAt(0);
        cardSubmitComplaint = (CardView) layoutActions.getChildAt(1);

        // ===== Safely find Due TextView inside Monthly Bill card =====
        txtBillDue = findDueTextView(cardMonthlyBill);

        db = FirebaseDatabase.getInstance().getReference();

        loadUser();
        loadTodayMenu();
        loadMonthlyBill();

        cardMonthlyBill.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MonthlyBillActivity.class)));

        cardSubmitComplaint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ComplaintActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), Login.class));
            requireActivity().finish();
        });

        return view;
    }

    // ================= SAFE VIEW SEARCH =================

    private TextView findDueTextView(View parent) {
        if (parent instanceof TextView) {
            return (TextView) parent;
        }

        if (parent instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) parent;
            for (int i = 0; i < vg.getChildCount(); i++) {
                TextView result = findDueTextView(vg.getChildAt(i));
                if (result != null && result.getText().toString().contains("Due")) {
                    return result;
                }
            }
        }
        return null;
    }

    // ================= Firebase =================

    private void loadUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            txtUser.setText("Student");
        }
    }

    private void loadTodayMenu() {
        db.child("today_menu")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        txtMenuName.setText(snapshot.child("name").getValue(String.class));
                        txtMenuDesc.setText(snapshot.child("description").getValue(String.class));
                        txtMenuTime.setText(snapshot.child("time").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadMonthlyBill() {
        db.child("monthly_bill")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (txtBillDue == null) return;

                        Double total = snapshot.child("total").getValue(Double.class);
                        if (total != null) {
                            txtBillDue.setText("● Due: ₹" + total);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
