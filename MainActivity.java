package com.example.pwvideoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Screen always ON
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        initializeVideoPlayer();
        startVideoService();
        acquireWakeLock();
    }

    private void initializeVideoPlayer() {
        videoView = findViewById(R.id.videoView);
        
        // PWF Physics Online Course Video URLs
        String[] videoUrls = {
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        };
        
        // First video play करें
        playOnlineVideo(videoUrls[0]);
        
        // Cycle through videos
        setupVideoCycle(videoUrls);
    }

    private void playOnlineVideo(String videoUrl) {
        try {
            Uri videoUri = Uri.parse(videoUrl);
            videoView.setVideoURI(videoUri);
            
            videoView.setOnPreparedListener(mp -> {
                videoView.start();
                mp.setLooping(false); // Single play
                mp.setVolume(0, 0); // Mute audio
            });
            
            videoView.setOnErrorListener((mp, what, extra) -> {
                // Next video try करें error आने पर
                return true;
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoCycle(String[] videoUrls) {
        videoView.setOnCompletionListener(mp -> {
            // Current video का index ढूंढें
            int currentIndex = -1;
            for (int i = 0; i < videoUrls.length; i++) {
                if (videoView.getVideoURI().toString().contains(videoUrls[i])) {
                    currentIndex = i;
                    break;
                }
            }
            
            // Next video play करें
            int nextIndex = (currentIndex + 1) % videoUrls.length;
            playOnlineVideo(videoUrls[nextIndex]);
        });
    }

    private void acquireWakeLock() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "PWVideo:WakeLock"
            );
            wakeLock.acquire(30 * 60 * 1000L); // 30 minutes
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVideoService() {
        try {
            Intent serviceIntent = new Intent(this, VideoService.class);
            startService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Background में भी चलता रहे
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}