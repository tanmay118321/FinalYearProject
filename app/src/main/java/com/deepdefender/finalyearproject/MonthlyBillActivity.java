package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonthlyBillActivity extends AppCompatActivity {

    private TextView txtMonth, txtAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_bill);

        txtMonth = findViewById(R.id.txtMonth);
        txtAmount = findViewById(R.id.txtAmount);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("monthlyBills")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(MonthlyBillActivity.this,
                            "No bill available",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // SAFE READS
                String month = snapshot.child("month").getValue(String.class);
                String amount = snapshot.child("amount").getValue(String.class);

                if (month == null || amount == null) {
                    Toast.makeText(MonthlyBillActivity.this,
                            "Bill data incomplete",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                txtMonth.setText(month);
                txtAmount.setText("₹ " + amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyBillActivity.this,
                        "Failed to load bill",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
