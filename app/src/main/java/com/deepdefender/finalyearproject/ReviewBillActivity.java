package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReviewBillActivity extends AppCompatActivity {

    // UI
    TextView txtGrandTotal, txtPerStudent, txtStudents;
    TextView txtKiranaAmount, txtVegetablesAmount, txtGasAmount;
    Button btnFinalize, btnCancel;
    TextView txtCycleMonth, txtCycleDate;


    // Data
    int veg = 0, kirana = 0, gas = 0, workers = 0;
    int total, students = 120, perStudent;

    String month, billKey;

    DatabaseReference billRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_bill);

        // ---------- UI ----------
        txtGrandTotal = findViewById(R.id.txtGrandTotal);
        txtPerStudent = findViewById(R.id.txtPerStudent);
        txtStudents = findViewById(R.id.txtStudents);

        txtKiranaAmount = findViewById(R.id.txtKiranaAmount);
        txtVegetablesAmount = findViewById(R.id.txtVegetablesAmount);
        txtGasAmount = findViewById(R.id.txtGasAmount);

        btnFinalize = findViewById(R.id.btnFinalize);
        btnCancel = findViewById(R.id.btnCancel);

        txtStudents.setText(String.valueOf(students));

        txtCycleMonth = findViewById(R.id.txtCycleMonth);
        txtCycleDate = findViewById(R.id.txtCycleDate);
        setCurrentMonthUI();

        billRef = FirebaseDatabase.getInstance()
                .getReference("monthlyBills");

        // 🔹 AUTO CURRENT MONTH
        month = getCurrentMonth();
        billKey = month.replace(" ", "_");

        loadCurrentMonthBill();

        btnFinalize.setOnClickListener(v -> finalizeBill());
        btnCancel.setOnClickListener(v -> finish());
    }

    /* ---------------- GET CURRENT MONTH ---------------- */
    private String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    /* ---------------- LOAD BILL ---------------- */
    private void loadCurrentMonthBill() {

        billRef.child(billKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {

                        if (s.exists()) {
                            veg = s.child("vegetables").getValue(Integer.class);
                            kirana = s.child("kirana").getValue(Integer.class);
                            gas = s.child("gas").getValue(Integer.class);
                            workers = s.child("workers").getValue(Integer.class);
                        } else {
                            // 🔹 Coming from AdminSideGenerateBill
                            veg = getIntent().getIntExtra("vegetables", 0);
                            kirana = getIntent().getIntExtra("kirana", 0);
                            gas = getIntent().getIntExtra("gas", 0);
                            workers = getIntent().getIntExtra("workers", 0);
                        }

                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(
                                ReviewBillActivity.this,
                                "Failed to load bill",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /* ---------------- UPDATE UI ---------------- */
    private void updateUI() {

        total = veg + kirana + gas + workers;
        perStudent = total / students;

        txtGrandTotal.setText("₹" + total);
        txtPerStudent.setText("₹" + perStudent);

        txtKiranaAmount.setText("₹" + kirana);
        txtVegetablesAmount.setText("₹" + veg);
        txtGasAmount.setText("₹" + gas);
    }

    /* ---------------- FINALIZE / UPDATE BILL ---------------- */
    private void finalizeBill() {

        billRef.child(billKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {

                        if (s.exists()) {
                            String status = s.child("status").getValue(String.class);
                            if ("PAID".equals(status)) {
                                Toast.makeText(
                                        ReviewBillActivity.this,
                                        "Paid bill cannot be edited",
                                        Toast.LENGTH_LONG
                                ).show();
                                return;
                            }
                        }

                        saveBill();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /* ---------------- SAVE BILL ---------------- */
    private void saveBill() {

        Map<String, Object> bill = new HashMap<>();
        bill.put("month", month);
        bill.put("vegetables", veg);
        bill.put("kirana", kirana);
        bill.put("gas", gas);
        bill.put("workers", workers);
        bill.put("total", total);
        bill.put("students", students);
        bill.put("perStudent", perStudent);
        bill.put("status", "FINAL");
        bill.put("timestamp", System.currentTimeMillis());

        billRef.child(billKey)
                .updateChildren(bill)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            "Bill saved for " + month,
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                });
    }
    private void setCurrentMonthUI() {

        Calendar cal = Calendar.getInstance();

        // Month + Year
        SimpleDateFormat monthFormat =
                new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthYear = monthFormat.format(cal.getTime());

        // Start date
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();

        // End date
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = cal.getTime();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd MMM", Locale.getDefault());

        txtCycleMonth.setText(monthYear + " Cycle");
        txtCycleDate.setText(
                dateFormat.format(start) + " - " +
                        dateFormat.format(end)
        );
    }

}
