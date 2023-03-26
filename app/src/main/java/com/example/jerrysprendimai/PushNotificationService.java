package com.example.jerrysprendimai;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class PushNotificationService extends FirebaseMessagingService {

    NotificationManager mNotificationManager;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        ActivityLogin.setMytoken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        //super.onMessageReceived(message);

        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        //Intent resultIntent2 = new Intent(this, ActivityMain.class);


        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

            // playing audio and vibration when user se reques
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                r.setLooping(false);
            }

            //int resourceImage = getResources().getDrawable(R.mipmap.ic_jerry_foreground);
            //int resourceImage = getResources().getIdentifier(message.getNotification().getIcon(), "drawable", getPackageName());
            Map<String,String> data = message.getData();
            String title = data.get("title");
            String body = data.get("body");
            String objId = data.get("objectId");
            String icon = data.get("icon");
            String sound = data.get("sound");
            int img = getResources().getIdentifier(icon, "drawable", getPackageName());

            //String[] str = message.getNotification().getTitle().split("#");


            Intent resultIntent = new Intent().setClass(this, ActivityMain.class);
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("body", body);
            resultIntent.putExtra("chatObjID", objId);
            resultIntent.setAction(objId + "_action");
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK   |
                                  Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                  Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                  Intent.FLAG_ACTIVITY_CLEAR_TOP );

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                                                                                                                  PendingIntent.FLAG_IMMUTABLE);
            /*PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                                                                     Integer.parseInt(objId),
                                                                     resultIntent,
                                                                     PendingIntent.FLAG_UPDATE_CURRENT |
                                                                     PendingIntent.FLAG_IMMUTABLE);*/

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, objId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
               builder.setSmallIcon(img);
            }
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setContentIntent(pendingIntent);
            builder.setGroup(objId);
            builder.setGroup(objId);
            //builder.addAction(resourceImage, message.getNotification().getBody(), pendingIntent);
            builder.setAutoCancel(true);

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //    mNotificationManager = getSystemService(NotificationManager.class);
            //}
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            String channelId = objId;
            NotificationChannel channel = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                channel = new NotificationChannel( channelId,"Readable title", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("description");
                    mNotificationManager.createNotificationChannel(channel);
                    builder.setChannelId(channelId);
            }

            // notificationId is a unique int for each notification that you must define
            mNotificationManager.notify(Integer.parseInt(objId), builder.build());



    }

    }
}
