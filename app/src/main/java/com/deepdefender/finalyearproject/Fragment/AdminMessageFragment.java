package com.deepdefender.finalyearproject.Fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.deepdefender.finalyearproject.R;
import com.google.firebase.database.*;

import java.util.*;

public class AdminMessageFragment extends Fragment {

    RecyclerView rv;
    EditText input;
    ImageButton send;

    List<MessageModel> list = new ArrayList<>();
    DatabaseReference ref;
    MessageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater i, ViewGroup c, Bundle b) {

        View v = i.inflate(R.layout.fragment_commonmessage, c, false);

        rv = v.findViewById(R.id.recycler);
        input = v.findViewById(R.id.input);
        send = v.findViewById(R.id.send);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(list, true);
        rv.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("announcements");

        send.setOnClickListener(x -> sendMsg());
        loadMessages();

        return v;
    }

    private void sendMsg() {
        String txt = input.getText().toString().trim();
        if (txt.isEmpty()) return;

        String id = ref.push().getKey();
        ref.child(id).setValue(
                new MessageModel(txt, "Admin", System.currentTimeMillis())
        );

        input.setText("");
    }

    private void loadMessages() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                list.clear();
                for (DataSnapshot d : s.getChildren()) {
                    MessageModel m = d.getValue(MessageModel.class);
                    if (m != null && !m.deleted) list.add(m);
                }
                adapter.notifyDataSetChanged();

                if (!list.isEmpty()) {
                    rv.scrollToPosition(list.size() - 1);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}
