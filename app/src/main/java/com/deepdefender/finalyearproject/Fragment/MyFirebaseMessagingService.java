package com.deepdefender.finalyearproject.Fragment;

import android.app.*;
import android.content.*;
import androidx.core.app.NotificationCompat;
import com.deepdefender.finalyearproject.R;
import com.google.firebase.messaging.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage msg) {

        Intent i = new Intent(this,
                com.deepdefender.finalyearproject.MainActivity.class);
        i.putExtra("openFragment", "user");

        PendingIntent pi = PendingIntent.getActivity(
                this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new NotificationCompat.Builder(this, "ann")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Announcement")
                .setContentText(msg.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify(100, n);
    }
}

