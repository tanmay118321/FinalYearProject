package com.deepdefender.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.*;

import com.google.firebase.database.*;

import java.util.*;

public class AdminSideComplaint extends AppCompatActivity {

    RecyclerView recycler;
    AdminComplaintAdapter adapter;
    List<ComplaintModel> list;
    ImageView backbtn;


    SearchView searchView;
    TextView pendingTxt, resolvedTxt, criticalTxt;

    AppCompatButton btnAll, btnPending, btnResolved, btnCritical;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_side_complaint);

        recycler = findViewById(R.id.recyclerComplaints);
        searchView = findViewById(R.id.searchBox);
        backbtn=findViewById(R.id.btnBack);

        pendingTxt = findViewById(R.id.txtPendingCount);
        resolvedTxt = findViewById(R.id.txtResolvedCount);
        criticalTxt = findViewById(R.id.txtCriticalCount);

        btnAll = findViewById(R.id.btnAll);
        btnPending = findViewById(R.id.btnPending);
        btnResolved = findViewById(R.id.btnResolved);
        btnCritical = findViewById(R.id.btnCritical);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("complaints");

        adapter = new AdminComplaintAdapter(this, list, ref);
        recycler.setAdapter(adapter);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminSideComplaint.this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        enableSearch();
        enableSwipe();
        enableFilters();
        loadComplaints();
    }

    // SEARCH
    private void enableSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String q) { return false; }
            public boolean onQueryTextChange(String q) {
                adapter.getFilter().filter(q);
                return true;
            }
        });
    }

    // FILTER BUTTONS
    private void enableFilters() {
        btnAll.setOnClickListener(v -> adapter.getFilter().filter(""));
        btnPending.setOnClickListener(v -> adapter.getFilter().filter("pending"));
        btnResolved.setOnClickListener(v -> adapter.getFilter().filter("resolved"));
        btnCritical.setOnClickListener(v -> adapter.getFilter().filter("critical"));
    }

    // SWIPE TO RESOLVE
    private void enableSwipe() {
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView r,
                                          RecyclerView.ViewHolder v,
                                          RecyclerView.ViewHolder t) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                        ComplaintModel c = list.get(vh.getAdapterPosition());
                        ref.child(c.id).child("status").setValue("Resolved");
                        Toast.makeText(AdminSideComplaint.this,
                                "Complaint Resolved", Toast.LENGTH_SHORT).show();
                    }
                });
        helper.attachToRecyclerView(recycler);
    }

    // LOAD DATA + COUNTERS + CRITICAL ALERT
    private void loadComplaints() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                list.clear();
                int pending = 0, resolved = 0, critical = 0;

                for (DataSnapshot s : snap.getChildren()) {
                    ComplaintModel c = s.getValue(ComplaintModel.class);
                    if (c != null) {
                        c.id = s.getKey();
                        list.add(c);

                        if ("Pending".equalsIgnoreCase(c.status)) pending++;
                        if ("Resolved".equalsIgnoreCase(c.status)) resolved++;
                        if ("Critical".equalsIgnoreCase(c.status)) {
                            critical++;
                            Toast.makeText(AdminSideComplaint.this,
                                    "⚠ Critical Complaint: " + c.subject,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                pendingTxt.setText(String.valueOf(pending));
                resolvedTxt.setText(String.valueOf(resolved));
                criticalTxt.setText(String.valueOf(critical));

                adapter.refreshFullList(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
