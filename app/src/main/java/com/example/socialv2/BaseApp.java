package com.example.socialv2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BaseApp extends Application {
//base app is needed according to this https://www.youtube.com/watch?v=tTbd1Mfi-Sk&ab_channel=CodinginFlow
    //so that there is no error with the channels and notifications

    //channel 1 is going to be for notification that have sound and pop up on screen
    public static final String CHANNEL_1_ID="channel1";
    public static final String CHANNEL_2_ID="channel2";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
