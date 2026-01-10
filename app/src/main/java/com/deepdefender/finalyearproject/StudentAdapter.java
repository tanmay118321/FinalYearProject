package com.deepdefender.finalyearproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.Holder> {

    public interface OnMenuClick {
        void onClick(StudentModel student, View anchor);
    }

    private final Context context;
    private final List<StudentModel> list;
    private final OnMenuClick listener;

    public StudentAdapter(Context context, List<StudentModel> list, OnMenuClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context)
                .inflate(R.layout.item_student, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        StudentModel s = list.get(pos);

        h.txtName.setText(s.name);
        h.txtEmail.setText(s.email);
        h.txtRoom.setText("Room: " + s.room);

        h.btnMenu.setOnClickListener(v -> listener.onClick(s, v));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtRoom;
        ImageView btnMenu;

        Holder(View v) {
            super(v);
            txtName = v.findViewById(R.id.txtName);
            txtEmail = v.findViewById(R.id.txtEmail);
            txtRoom = v.findViewById(R.id.txtRoom);
            btnMenu = v.findViewById(R.id.btnMenu);
        }
    }
}
