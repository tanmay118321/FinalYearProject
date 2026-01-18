package com.deepdefender.finalyearproject;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImagePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_image_preview);

        ImageView img = findViewById(R.id.fullImage);

        String base64 = getIntent().getStringExtra("img");
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        img.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
    }
}
