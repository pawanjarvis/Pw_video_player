package com.example.pwvideoplayer;

import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;

public class VideoService extends Service {

    private static final String CHANNEL_ID = "PW_VIDEO_CHANNEL";
    private static final int NOTIFICATION_ID = 1;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(NOTIFICATION_ID, createNotification());
        acquirePermanentWakeLock();
    }

    private void acquirePermanentWakeLock() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "PWVideo:BackgroundService"
            );
            wakeLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Notification createNotification() {
        createNotificationChannel();
        
        // PWF Physics के लिए professional notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PWF Physics - Course Running")
            .setContentText("Video playing in background")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "PWF Physics Course",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Physics course video is playing continuously");
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service को हमेशा restart होने दें
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}