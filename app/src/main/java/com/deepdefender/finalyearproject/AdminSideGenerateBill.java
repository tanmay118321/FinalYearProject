package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class AdminSideGenerateBill extends AppCompatActivity {

    EditText edtVegetables, edtKirana, edtGas, edtWorkers;
    TextView txtSubtotal;
    Button btnReview;

    int veg = 0, kirana = 0, gas = 0, workers = 0;
    String editMonth = null;

    DatabaseReference billRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_side_generate_bill);

        edtVegetables = findViewById(R.id.edtVegetables);
        edtKirana = findViewById(R.id.edtKirana);
        edtGas = findViewById(R.id.edtGas);
        edtWorkers = findViewById(R.id.edtWorkers);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        btnReview = findViewById(R.id.btnReview);

        billRef = FirebaseDatabase.getInstance()
                .getReference("monthlyBills");

        editMonth = getIntent().getStringExtra("month");

        if (editMonth != null) {
            loadExistingBill(editMonth);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                veg = getVal(edtVegetables);
                kirana = getVal(edtKirana);
                gas = getVal(edtGas);
                workers = getVal(edtWorkers);

                txtSubtotal.setText("Subtotal: ₹" + (veg + kirana + gas + workers));
            }
        };

        edtVegetables.addTextChangedListener(watcher);
        edtKirana.addTextChangedListener(watcher);
        edtGas.addTextChangedListener(watcher);
        edtWorkers.addTextChangedListener(watcher);

        btnReview.setOnClickListener(v -> {
            Intent i = new Intent(this, ReviewBillActivity.class);
            i.putExtra("month", editMonth); // null for new bill
            i.putExtra("vegetables", veg);
            i.putExtra("kirana", kirana);
            i.putExtra("gas", gas);
            i.putExtra("workers", workers);
            startActivity(i);
        });
    }

    private void loadExistingBill(String month) {

        String key = month.replace(" ", "_");

        billRef.child(key).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot s) {
                        if (!s.exists()) return;

                        edtVegetables.setText(String.valueOf(s.child("vegetables").getValue(Integer.class)));
                        edtKirana.setText(String.valueOf(s.child("kirana").getValue(Integer.class)));
                        edtGas.setText(String.valueOf(s.child("gas").getValue(Integer.class)));
                        edtWorkers.setText(String.valueOf(s.child("workers").getValue(Integer.class)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {}
                }
        );
    }

    private int getVal(EditText e) {
        String v = e.getText().toString().trim();
        return v.isEmpty() ? 0 : Integer.parseInt(v);
    }
}
