package com.deepdefender.finalyearproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminAttendanceAdapter extends RecyclerView.Adapter<AdminAttendanceAdapter.ViewHolder> {

    private List<AttendanceModel> list;
    private Context context;

    public AdminAttendanceAdapter(Context context, List<AttendanceModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_attendance_student, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        AttendanceModel s = list.get(position);

        h.tvName.setText(s.name);
        h.tvRoom.setText(s.room);
        h.tvStatus.setText(s.status);
        h.tvTime.setText(s.time);

        // 🔥 LOAD BASE64 IMAGE
        if (s.profileImageBase64 != null && !s.profileImageBase64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(s.profileImageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                h.imgAvatar.setImageBitmap(bitmap);
            } catch (Exception e) {
                h.imgAvatar.setImageResource(R.drawable.avatar_placeholder);
            }
        } else {
            h.imgAvatar.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvRoom, tvStatus, tvTime;
        ImageView imgAvatar;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvRoom = v.findViewById(R.id.tvRoom);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvTime = v.findViewById(R.id.tvTime);
            imgAvatar = v.findViewById(R.id.imgAvatar);
        }
    }
}
