package com.deepdefender.finalyearproject.Fragment;


import android.content.Intent;
import android.graphics.*;
import android.util.Base64;
import android.view.*;
import android.widget.*;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.deepdefender.finalyearproject.ImagePreviewActivity;
import com.deepdefender.finalyearproject.R;


import java.text.SimpleDateFormat;
import java.util.*;


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

        if (m.imageBase64 != null && !m.imageBase64.isEmpty()) {
            h.msgText.setVisibility(View.GONE);
            h.img.setVisibility(View.VISIBLE);

            byte[] bytes = Base64.decode(m.imageBase64, Base64.DEFAULT);
            h.img.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            h.img.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ImagePreviewActivity.class);
                intent.putExtra("img", m.imageBase64);
                v.getContext().startActivity(intent);
            });
        }

        h.txtCommittee.setText(m.sender);
        h.txtDate.setText(new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(new Date(m.time)));


        if (m.imageBase64 != null && !m.imageBase64.isEmpty()) {
            h.msgText.setVisibility(View.GONE);
            h.img.setVisibility(View.VISIBLE);
            byte[] bytes = Base64.decode(m.imageBase64, Base64.DEFAULT);
            h.img.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        } else {
            h.img.setVisibility(View.GONE);
            h.msgText.setVisibility(View.VISIBLE);
            h.msgText.setText(m.message);
        }
    }


    @Override
    public int getItemCount() { return list.size(); }


    static class VH extends RecyclerView.ViewHolder {
        TextView txtCommittee, msgText, txtDate;
        ImageView img;


        VH(View v) {
            super(v);
            txtCommittee = v.findViewById(R.id.txtCommittee);
            msgText = v.findViewById(R.id.msgText);
            txtDate = v.findViewById(R.id.txtDate);
            img = v.findViewById(R.id.img);
        }
    }
}