package com.deepdefender.finalyearproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ComplaintActivity extends AppCompatActivity {

    EditText subject, details;
   Button submit;
    String selectedCategory = "Food Quality";

    DatabaseReference ref;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        subject = findViewById(R.id.edtSubject);
        details = findViewById(R.id.edtDetails);
        submit = findViewById(R.id.btnSubmit);



        ref = FirebaseDatabase.getInstance().getReference("complaints");



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
