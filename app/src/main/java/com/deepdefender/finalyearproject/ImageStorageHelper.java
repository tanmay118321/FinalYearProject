package com.deepdefender.finalyearproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageStorageHelper {

    public static String saveToInternal(Context context, Uri uri) throws IOException {

        Bitmap bitmap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                            context.getContentResolver(), uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), uri);
        }

        File file = new File(context.getFilesDir(), "profile.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();

        return file.getAbsolutePath();
    }
}
