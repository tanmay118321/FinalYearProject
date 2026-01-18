// Full working AdminMessageFragment with text + image announcements using Firebase Realtime Database
package com.deepdefender.finalyearproject.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.deepdefender.finalyearproject.R;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class AdminMessageFragment extends Fragment {

    RecyclerView rv;
    EditText input;
    ImageButton send, btnImage;

    List<MessageModel> list = new ArrayList<>();
    DatabaseReference ref;
    MessageAdapter adapter;

    ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == Activity.RESULT_OK && r.getData() != null) {
                    sendImage(r.getData().getData());
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i, ViewGroup c, Bundle b) {
        View v = i.inflate(R.layout.fragment_commonmessage, c, false);

        rv = v.findViewById(R.id.recycler);
        input = v.findViewById(R.id.input);
        send = v.findViewById(R.id.send);
        btnImage = v.findViewById(R.id.btnImage);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(list, true);
        rv.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("announcements");

        send.setOnClickListener(x -> sendText());
        btnImage.setOnClickListener(x -> pickImage());

        loadMessages();
        return v;
    }

    private void sendText() {
        String msg = input.getText().toString().trim();
        if (msg.isEmpty()) return;

        String key = ref.push().getKey();

        MessageModel model = new MessageModel(
                msg,
                "Mess Committee",
                System.currentTimeMillis()   // <-- IMPORTANT FIX
        );

        ref.child(key).setValue(model);
        input.setText("");
    }


    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePicker.launch(i);
    }

    private void sendImage(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            String key = ref.push().getKey();
            MessageModel model = new MessageModel(
                    "Mess Committee",
                    System.currentTimeMillis(),   // <-- IMPORTANT FIX
                    base64
            );

            ref.child(key).setValue(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadMessages() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    MessageModel m = d.getValue(MessageModel.class);
                    if (m != null) list.add(m);
                }

                Collections.sort(list, (a, b) -> Long.compare(a.time, b.time));
                adapter.notifyDataSetChanged();

                if (!list.isEmpty())
                    rv.scrollToPosition(list.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
