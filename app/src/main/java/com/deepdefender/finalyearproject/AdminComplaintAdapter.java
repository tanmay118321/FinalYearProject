package com.deepdefender.finalyearproject;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminComplaintAdapter
        extends RecyclerView.Adapter<AdminComplaintAdapter.ViewHolder>
        implements Filterable {

    Context context;
    List<ComplaintModel> list;
    List<ComplaintModel> fullList;
    DatabaseReference ref;

    public AdminComplaintAdapter(Context context,
                                 List<ComplaintModel> list,
                                 DatabaseReference ref) {
        this.context = context;
        this.list = list;
        this.fullList = new ArrayList<>(list);
        this.ref = ref;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_complaint, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        ComplaintModel c = list.get(i);

        h.subject.setText(c.subject);
        h.details.setText(c.details);
        h.category.setText(c.category);
        h.time.setText(formatTime(c.timestamp));

        h.status.setText(c.status.toUpperCase());

        if ("Critical".equalsIgnoreCase(c.status)) {
            h.status.setBackgroundColor(Color.parseColor("#FDECEA"));
            h.status.setTextColor(Color.RED);
        } else if ("Pending".equalsIgnoreCase(c.status)) {
            h.status.setBackgroundColor(Color.parseColor("#FFF4CC"));
            h.status.setTextColor(Color.parseColor("#C9A400"));
        } else {
            h.status.setBackgroundColor(Color.parseColor("#E8F5E9"));
            h.status.setTextColor(Color.parseColor("#2E7D32"));
            h.resolve.setText("Done");
            h.resolve.setEnabled(false);
        }

        h.resolve.setOnClickListener(v ->
                ref.child(c.id).child("status").setValue("Resolved")
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject, details, category, status, time;
        Button resolve;

        ViewHolder(View v) {
            super(v);
            subject = v.findViewById(R.id.txtSubject);
            details = v.findViewById(R.id.txtDetails);
            category = v.findViewById(R.id.txtCategory);
            status = v.findViewById(R.id.txtStatus);
            time = v.findViewById(R.id.txtTime);
            resolve = v.findViewById(R.id.btnResolve);
        }
    }

    private String formatTime(long t) {
        return new SimpleDateFormat("dd MMM, hh:mm a",
                Locale.getDefault()).format(t);
    }

    // 🔍 SEARCH FILTER
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence q) {
                List<ComplaintModel> filtered = new ArrayList<>();

                if (q == null || q.length() == 0) {
                    filtered.addAll(fullList);
                } else {
                    String key = q.toString().toLowerCase();
                    for (ComplaintModel c : fullList) {
                        if (c.subject.toLowerCase().contains(key)
                                || c.category.toLowerCase().contains(key)
                                || c.status.toLowerCase().contains(key)) {
                            filtered.add(c);
                        }
                    }
                }

                FilterResults r = new FilterResults();
                r.values = filtered;
                return r;
            }

            @Override
            protected void publishResults(CharSequence q, FilterResults r) {
                list.clear();
                list.addAll((List) r.values);
                notifyDataSetChanged();
            }
        };
    }

    public void refreshFullList(List<ComplaintModel> updated) {
        fullList.clear();
        fullList.addAll(updated);
    }
}
