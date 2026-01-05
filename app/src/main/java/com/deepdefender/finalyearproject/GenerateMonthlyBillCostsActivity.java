package com.deepdefender.finalyearproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GenerateMonthlyBillCostsActivity extends AppCompatActivity {

    EditText veg, kirana, gas, workers;
    TextView subtotalTxt;
    Button btnReview;

    int v = 0, k = 0, g = 0, w = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_monthly_bill_costs);

        veg = findViewById(R.id.edtVegetables);
        kirana = findViewById(R.id.edtKirana);
        gas = findViewById(R.id.edtGas);
        workers = findViewById(R.id.edtWorkers);
        subtotalTxt = findViewById(R.id.txtSubtotal);
        btnReview = findViewById(R.id.btnReview);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                v = get(veg);
                k = get(kirana);
                g = get(gas);
                w = get(workers);

                int total = v + k + g + w;
                subtotalTxt.setText("Subtotal: ₹" + total);
            }
        };

        veg.addTextChangedListener(watcher);
        kirana.addTextChangedListener(watcher);
        gas.addTextChangedListener(watcher);
        workers.addTextChangedListener(watcher);

        btnReview.setOnClickListener(vw -> {
            Intent i = new Intent(this, ReviewBillActivity.class);
            i.putExtra("veg", v);
            i.putExtra("kirana", k);
            i.putExtra("gas", g);
            i.putExtra("workers", w);
            startActivity(i);
        });
    }

    private int get(EditText e) {
        if (e.getText().toString().isEmpty()) return 0;
        return Integer.parseInt(e.getText().toString());
    }
}
