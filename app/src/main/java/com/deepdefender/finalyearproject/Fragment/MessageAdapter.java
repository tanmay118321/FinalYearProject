package com.deepdefender.finalyearproject.Fragment;

import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepdefender.finalyearproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter
        extends RecyclerView.Adapter<MessageAdapter.VH> {

    List<MessageModel> list;
    boolean isAdmin;

    public MessageAdapter(List<MessageModel> list, boolean isAdmin) {
        this.list = list;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_message, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {

        MessageModel m = list.get(i);

        h.msgText.setText(m.text);
        h.txtCommittee.setText(
                m.committee != null ? m.committee : "Committee"
        );

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        h.txtDate.setText(
                sdf.format(new Date(m.timestamp))
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView txtCommittee, msgText, txtDate;

        VH(View v) {
            super(v);
            txtCommittee = v.findViewById(R.id.txtCommittee);
            msgText = v.findViewById(R.id.msgText);
            txtDate = v.findViewById(R.id.txtDate);
        }
    }
}
