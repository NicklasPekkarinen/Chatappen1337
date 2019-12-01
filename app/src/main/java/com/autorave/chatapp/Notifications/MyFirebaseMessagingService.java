package com.autorave.chatapp.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.autorave.chatapp.MainActivity;
import com.autorave.chatapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("MarcusTag", "Message data payload: " + remoteMessage.getData());
            showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));

        }
        // Check if message contains a notification payload.
        if(remoteMessage.getNotification()!= null){
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            Log.d("MarcusTag", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
    private RemoteViews getCostumDesign(String title,String message){
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.message,message);
        remoteViews.setImageViewResource(R.id.icon,R.mipmap.ic_launcher);
        return remoteViews;
    }

    private void showNotification(String title, String message) {

        Intent intent = new Intent(this, MainActivity.class);
        String channel_Id = "Chat_App";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channel_Id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

            builder = builder.setContent(getCostumDesign(title, message));
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel nfChannel = new NotificationChannel(channel_Id,"ChatApp",NotificationManager.IMPORTANCE_HIGH);
            nfChannel.setSound(uri,null);
            notificationManager.createNotificationChannel(nfChannel);
        }

        notificationManager.notify(0,builder.build());
    }

}
