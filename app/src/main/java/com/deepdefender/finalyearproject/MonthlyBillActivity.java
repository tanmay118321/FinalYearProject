package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.database.*;

import java.io.File;

public class MonthlyBillActivity extends AppCompatActivity {

    private TextView txtMonth, txtAmount, txtStatus;
    private LinearLayout breakdownContainer, historyContainer;
    private Button btnDownloadInvoice, btnPayNow;
    private ImageView btnBack;

    private DatabaseReference billRef;

    private String invoicePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_bill);

        initViews();
        initFirebase();
        initClicks();

        loadLatestBill();
        loadHistory();
    }

    private void initViews() {
        txtMonth = findViewById(R.id.txtMonth);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        breakdownContainer = findViewById(R.id.breakdownContainer);
        historyContainer = findViewById(R.id.historyContainer);

        btnDownloadInvoice = findViewById(R.id.btnDownloadInvoice);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initFirebase() {
        billRef = FirebaseDatabase.getInstance().getReference("monthlyBills");
    }

    private void initClicks() {
        btnBack.setOnClickListener(v -> finish());

        btnDownloadInvoice.setOnClickListener(v -> openInvoice());

        btnPayNow.setOnClickListener(v ->
                Toast.makeText(this,
                        "Payment gateway integration pending",
                        Toast.LENGTH_SHORT).show()
        );
    }

    /* ---------------- OPEN INVOICE ---------------- */
    private void openInvoice() {

        if (invoicePath == null || invoicePath.trim().isEmpty()) {
            Toast.makeText(this, "Invoice not available", Toast.LENGTH_SHORT).show();
            return;
        }

        File pdfFile = new File(
                getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                invoicePath
        );

        if (!pdfFile.exists()) {
            Toast.makeText(this, "Invoice file missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
        }
    }

    /* ---------------- LATEST BILL ---------------- */
    private void loadLatestBill() {

        billRef.orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            Toast.makeText(MonthlyBillActivity.this,
                                    "No bill available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DataSnapshot billSnap : snapshot.getChildren()) {

                            String month = billSnap.child("month").getValue(String.class);
                            Integer total = billSnap.child("total").getValue(Integer.class);
                            Boolean paid = billSnap.child("paid").getValue(Boolean.class);

                            Integer veg = billSnap.child("vegetables").getValue(Integer.class);
                            Integer kirana = billSnap.child("kirana").getValue(Integer.class);
                            Integer gas = billSnap.child("gas").getValue(Integer.class);
                            Integer workers = billSnap.child("workers").getValue(Integer.class);

                            invoicePath = billSnap.child("invoicePath").getValue(String.class);

                            if (month == null || total == null) return;

                            txtMonth.setText(month);
                            txtAmount.setText("₹ " + total);

                            if (paid != null && paid) {
                                txtStatus.setText("PAID");
                                txtStatus.setTextColor(getColor(R.color.green));
                                btnPayNow.setVisibility(View.GONE);
                            } else {
                                txtStatus.setText("PENDING");
                                txtStatus.setTextColor(getColor(R.color.orange));
                            }

                            loadBreakdown(veg, kirana, gas, workers);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MonthlyBillActivity.this,
                                "Failed to load bill", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /* ---------------- BREAKDOWN ---------------- */
    private void loadBreakdown(Integer veg, Integer kirana,
                               Integer gas, Integer workers) {

        breakdownContainer.removeAllViews();

        addRow("Vegetables", veg);
        addRow("Kirana / Grocery", kirana);
        addRow("Gas Cylinder", gas);
        addRow("Workers Salary", workers);
    }

    private void addRow(String title, Integer amount) {
        TextView tv = new TextView(this);
        tv.setText(title + "  •  ₹ " + (amount == null ? 0 : amount));
        tv.setTextSize(16f);
        tv.setPadding(24, 16, 24, 16);
        breakdownContainer.addView(tv);
    }

    /* ---------------- HISTORY ---------------- */
    private void loadHistory() {

        billRef.orderByChild("timestamp")
                .limitToLast(6)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        historyContainer.removeAllViews();

                        for (DataSnapshot billSnap : snapshot.getChildren()) {

                            String month = billSnap.child("month").getValue(String.class);
                            Integer total = billSnap.child("total").getValue(Integer.class);

                            if (month == null || total == null) continue;

                            TextView tv = new TextView(MonthlyBillActivity.this);
                            tv.setText(month + "  —  ₹ " + total);
                            tv.setPadding(24, 16, 24, 16);
                            tv.setTextSize(15f);

                            historyContainer.addView(tv);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}
