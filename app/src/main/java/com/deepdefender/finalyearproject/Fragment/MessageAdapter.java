package com.deepdefender.finalyearproject.Fragment;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.deepdefender.finalyearproject.R;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

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
        h.text.setText(m.text);
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView text;
        VH(View v) {
            super(v);
            text = v.findViewById(R.id.msgText);
        }
    }
}

