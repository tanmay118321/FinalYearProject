package com.deepdefender.finalyearproject.Fragment;

import android.os.Bundle;
import android.view.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.deepdefender.finalyearproject.R;
import com.google.firebase.database.*;

import java.util.*;

public class UserMessageFragment extends Fragment {

    RecyclerView rv;
    List<MessageModel> list = new ArrayList<>();
    MessageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater i, ViewGroup c, Bundle b) {

        View v = i.inflate(R.layout.fragment_commonmessage, c, false);

        rv = v.findViewById(R.id.recycler);
        View inputLayout = (View) v.findViewById(R.id.input).getParent();


        inputLayout.setVisibility(View.GONE);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(list, false);
        rv.setAdapter(adapter);

        FirebaseDatabase.getInstance()
                .getReference("announcements")
                .addValueEventListener(new ValueEventListener() {

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

        return v;
    }
}
