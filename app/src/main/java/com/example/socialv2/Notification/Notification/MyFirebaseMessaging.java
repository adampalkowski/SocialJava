package com.example.socialv2.Notification.Notification;

import static com.example.socialv2.BaseApp.CHANNEL_1_ID;
import static com.example.socialv2.BaseApp.CHANNEL_2_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.socialv2.MainActivity;
import com.example.socialv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            updateToken(s);
        }
    }

    private void updateToken(String s) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(s);
        assert firebaseUser != null;
        reference.child(firebaseUser.getUid()).setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sent = remoteMessage.getData().get("sent");
        String user=remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences=getSharedPreferences("PREFS",MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser","none");



        if (firebaseUser != null) {
            assert sent != null;
            if (sent.equals(firebaseUser.getUid())) {
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(remoteMessage);
                    } else {
                        sendNotifcation(remoteMessage);
                    }

                }


            }
        }


    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        assert user != null;
        int j=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this,MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("userid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        OreoNotification oreoNotification= new OreoNotification(this);
        Notification.Builder builder=oreoNotification.getOreoNotification(title,body,pendingIntent
        ,defaultSound,icon);
        int i=0;
        if(j>0){
            i=j;
        }
        oreoNotification.getManager().notify(i,builder.build());
    }

    private void sendNotifcation(RemoteMessage remoteMessage) {
        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        notificationManager = NotificationManagerCompat.from(this);

        sendOnChannel2(title, body);

     /*
        int j =Integer.parseInt(user.replaceAll("[\\D]",""));



       Intent  resultIntent  = new Intent(this, ChatActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("userid",user);
        resultIntent .putExtras(bundle);
        resultIntent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,j,resultIntent,PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        int i=0;
        if(j>0){
            i=j;
        }

        notificationManager.notify(i,builder.build());
        Log.d(TAG,"worked");
    }

    public void sendOnChannel1(View v,String tit,String msg) {
        String title = tit;
        String message =msg;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
        */
    }

    public void sendOnChannel1(String tit, String msg) {
        String title = tit;
        String message = msg;
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2(String tit, String msg) {
        String title = tit;
        String message = msg;


        Intent activityIntent = new Intent(this, MainActivity.class);
        Bundle bundle= new Bundle();
        bundle.putBoolean("Chatting_Start_key", true);
        activityIntent.putExtras(bundle);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lotti);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_profile)
                .setContentTitle(title)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.lorem_ipsum))
                        .setBigContentTitle("Big Content Title"))
                .setAutoCancel(true)
                .setContentText(message)
                .setColor(Color.BLUE)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }

}
