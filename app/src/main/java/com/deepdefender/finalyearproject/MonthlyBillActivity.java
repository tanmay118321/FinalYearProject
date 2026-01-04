package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class MonthlyBillActivity extends AppCompatActivity {

    TextView txtAmount, txtDueDate, txtMonth, txtStatus;
    LinearLayout breakdownContainer, historyContainer;
    ImageView btnBack;

    DatabaseReference billRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_bill);

        txtAmount = findViewById(R.id.txtAmount);
        txtDueDate = findViewById(R.id.txtDueDate);
        txtMonth = findViewById(R.id.txtMonth);
        txtStatus = findViewById(R.id.txtStatus);
        breakdownContainer = findViewById(R.id.breakdownContainer);
        historyContainer = findViewById(R.id.historyContainer);
        btnBack = findViewById(R.id.btnBack);

        billRef = FirebaseDatabase.getInstance().getReference("monthly_bill");

        btnBack.setOnClickListener(v -> finish());

        loadBill();
    }

    private void loadBill() {
        billRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                txtAmount.setText("$" + snapshot.child("total").getValue());
                txtDueDate.setText("Due " + snapshot.child("due_date").getValue());
                txtMonth.setText(snapshot.child("month").getValue().toString());
                txtStatus.setText(snapshot.child("status").getValue().toString());

                breakdownContainer.removeAllViews();
                for (DataSnapshot item : snapshot.child("breakdown").getChildren()) {
                    addRow(breakdownContainer,
                            item.child("title").getValue().toString(),
                            item.child("amount").getValue().toString());
                }

                historyContainer.removeAllViews();
                for (DataSnapshot item : snapshot.child("history").getChildren()) {
                    addRow(historyContainer,
                            item.child("month").getValue().toString(),
                            "$" + item.child("amount").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void addRow(LinearLayout parent, String title, String amount) {
        LinearLayout row = (LinearLayout) LayoutInflater.from(this)
                .inflate(android.R.layout.simple_list_item_2, parent, false);

        ((TextView) row.findViewById(android.R.id.text1)).setText(title);
        ((TextView) row.findViewById(android.R.id.text2)).setText(amount);

        parent.addView(row);
    }
}
