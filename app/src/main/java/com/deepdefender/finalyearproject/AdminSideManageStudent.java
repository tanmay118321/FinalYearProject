package com.deepdefender.finalyearproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class AdminSideManageStudent extends AppCompatActivity {

    // UI
    private RecyclerView recyclerStudents;
    private EditText etSearch;
    private TextView txtFooter;
    private FloatingActionButton fabAddStudent;

    // Chips
    private Chip chipAll, chipActive, chipDue, chipInactive;

    // Data
    private StudentAdapter adapter;
    private final List<StudentModel> allStudents = new ArrayList<>();
    private final List<StudentModel> filteredStudents = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_side_manage_students);

        bindViews();
        setupRecycler();
        setupFirebase();
        setupSearch();
        setupChips();

        fabAddStudent.setOnClickListener(v -> showAddStudentDialog());

        loadStudents();
    }

    // ================= BIND =================
    private void bindViews() {
        recyclerStudents = findViewById(R.id.recyclerStudents);
        etSearch = findViewById(R.id.etSearch);
        txtFooter = findViewById(R.id.txtFooter);
        fabAddStudent = findViewById(R.id.fabAddStudent);

        chipAll = findViewById(R.id.chipAll);
        chipActive = findViewById(R.id.chipActive);
        chipDue = findViewById(R.id.chipDue);
        chipInactive = findViewById(R.id.chipInactive);
    }

    private void setupRecycler() {
        recyclerStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(this, filteredStudents, this::showMenu);
        recyclerStudents.setAdapter(adapter);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    // ================= LOAD STUDENTS =================
    private void loadStudents() {
        db.collection("users")
                .whereEqualTo("role", "student")
                .addSnapshotListener((snap, e) -> {

                    if (e != null) {
                        Toast.makeText(this,
                                "Firestore error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (snap == null) return;

                    allStudents.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        StudentModel s = d.toObject(StudentModel.class);
                        if (s != null) {
                            s.uid = d.getId();
                            allStudents.add(s);
                        }
                    }

                    filteredStudents.clear();
                    filteredStudents.addAll(allStudents);
                    adapter.notifyDataSetChanged();

                    txtFooter.setText("Showing " + filteredStudents.size() + " students");
                });
    }

    // ================= SEARCH =================
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int a, int b, int d) {
                filterByText(c.toString());
            }
            @Override public void beforeTextChanged(CharSequence c,int a,int b,int d){}
            @Override public void afterTextChanged(Editable e){}
        });
    }

    private void filterByText(String q) {
        filteredStudents.clear();
        for (StudentModel s : allStudents) {
            if ((s.name != null && s.name.toLowerCase().contains(q.toLowerCase()))
                    || (s.email != null && s.email.toLowerCase().contains(q.toLowerCase()))) {
                filteredStudents.add(s);
            }
        }
        adapter.notifyDataSetChanged();
        txtFooter.setText("Showing " + filteredStudents.size() + " students");
    }

    // ================= CHIPS =================
    private void setupChips() {
        chipAll.setOnClickListener(v -> applyFilter("all"));
        chipActive.setOnClickListener(v -> applyFilter("active"));
        chipDue.setOnClickListener(v -> applyFilter("due"));
        chipInactive.setOnClickListener(v -> applyFilter("inactive"));
    }

    private void applyFilter(String type) {
        filteredStudents.clear();

        for (StudentModel s : allStudents) {
            switch (type) {
                case "active":
                    if ("active".equals(s.status)) filteredStudents.add(s);
                    break;
                case "inactive":
                    if ("inactive".equals(s.status)) filteredStudents.add(s);
                    break;
                case "due":
                    if (s.paymentDue) filteredStudents.add(s);
                    break;
                default:
                    filteredStudents.add(s);
            }
        }

        adapter.notifyDataSetChanged();
        txtFooter.setText("Showing " + filteredStudents.size() + " students");
    }

    // ================= BOTTOM SHEET =================
    private void showMenu(StudentModel s, View v) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheet = getLayoutInflater().inflate(R.layout.bottom_sheet_student, null);

        sheet.findViewById(R.id.actionDelete).setOnClickListener(x -> {
            db.collection("users").document(s.uid).delete();
            dialog.dismiss();
        });

        dialog.setContentView(sheet);
        dialog.show();
    }

    // ================= ADD STUDENT =================
    private void showAddStudentDialog() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View v = getLayoutInflater().inflate(R.layout.bottom_sheet_add_student, null);

        EditText etName = v.findViewById(R.id.etName);
        EditText etEmail = v.findViewById(R.id.etEmail);
        EditText etRoom = v.findViewById(R.id.etRoom);

        v.findViewById(R.id.btnAdd).setOnClickListener(x -> {

            StudentModel s = new StudentModel();
            s.name = etName.getText().toString().trim();
            s.email = etEmail.getText().toString().trim();
            s.room = etRoom.getText().toString().trim();
            s.role = "student";
            s.status = "active";
            s.paymentDue = false;

            db.collection("users").add(s);
            dialog.dismiss();
        });

        dialog.setContentView(v);
        dialog.show();
    }
}
