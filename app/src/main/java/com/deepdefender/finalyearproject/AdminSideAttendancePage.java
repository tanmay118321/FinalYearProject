package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminSideAttendancePage extends AppCompatActivity {

    RecyclerView rv;
    AdminAttendanceAdapter adapter;
    List<AttendanceModel> list = new ArrayList<>();

    DatabaseReference ref;

    TextView tvCount, tvCapacity;
    ProgressBar progress;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_admin_side_attendance_page);

        rv = findViewById(R.id.rvAttendance);
        tvCount = findViewById(R.id.tvCount);
        tvCapacity = findViewById(R.id.tvCapacity);
        progress = findViewById(R.id.progress);
        etSearch = findViewById(R.id.etSearch);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminAttendanceAdapter(this, list);
        rv.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("Attendance");

        loadAttendance();
        setupSearch();

        MaterialButton btnExportPdf = findViewById(R.id.btnExportPdf);
        btnExportPdf.setOnClickListener(v -> {
            try {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                File f = AttendancePdfUtil.export(this, date, list);
                Toast.makeText(this, "Saved: " + f.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadAttendance() {

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ref.child(today).child("Lunch").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                list.clear();
                int checked = (int) snap.getChildrenCount();

                tvCount.setText(checked + "/150");
                int percent = (checked * 100) / 150;
                tvCapacity.setText(percent + "% Capacity");
                progress.setProgress(percent);

                for (DataSnapshot s : snap.getChildren()) {
                    AttendanceModel m = s.getValue(AttendanceModel.class);
                    list.add(m);
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c){ filter(s.toString()); }
            @Override public void afterTextChanged(Editable s){}
        });
    }

    private void filter(String q) {
        List<AttendanceModel> f = new ArrayList<>();
        for (AttendanceModel m : list) {
            if (m.name.toLowerCase().contains(q.toLowerCase()) ||
                    m.room.toLowerCase().contains(q.toLowerCase()))
                f.add(m);
        }
        rv.setAdapter(new AdminAttendanceAdapter(this, f));
    }
}
