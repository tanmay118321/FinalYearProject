package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ComplaintActivity extends AppCompatActivity {

    EditText subject, details;
    AppCompatButton submit, food, hygiene, staff, maintenance;
    String selectedCategory = "Food Quality";

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        subject = findViewById(R.id.edtSubject);
        details = findViewById(R.id.edtDetails);
        submit = findViewById(R.id.btnSubmit);

        food = findViewById(R.id.btnFood);
        hygiene = findViewById(R.id.btnHygiene);
        staff = findViewById(R.id.btnStaff);
        maintenance = findViewById(R.id.btnMaintenance);

        ref = FirebaseDatabase.getInstance().getReference("complaints");

        food.setOnClickListener(v -> selectedCategory = "Food Quality");
        hygiene.setOnClickListener(v -> selectedCategory = "Hygiene");
        staff.setOnClickListener(v -> selectedCategory = "Staff Behavior");
        maintenance.setOnClickListener(v -> selectedCategory = "Maintenance");

        submit.setOnClickListener(v -> submitComplaint());
    }

    private void submitComplaint() {
        String id = ref.push().getKey();

        HashMap<String, Object> map = new HashMap<>();
        map.put("subject", subject.getText().toString());
        map.put("details", details.getText().toString());
        map.put("category", selectedCategory);
        map.put("status", "Pending");
        map.put("timestamp", System.currentTimeMillis());

        ref.child(id).setValue(map);
        Toast.makeText(this, "Complaint Submitted", Toast.LENGTH_SHORT).show();
        finish();
    }

}
