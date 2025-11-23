package com.pwf.physics;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toast.makeText(this, "PWF Physics App Started", Toast.LENGTH_LONG).show();
        
        // Video Service start करें
        startVideoService();
        
        setupVideoPlayer();
    }

    private void startVideoService() {
        try {
            Intent serviceIntent = new Intent(this, VideoService.class);
            startService(serviceIntent);
            Toast.makeText(this, "Background Service Started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Service Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupVideoPlayer() {
        videoView = findViewById(R.id.videoView);
        
        // Multiple backup video URLs
        String[] videoUrls = {
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        };
        
        Uri videoUri = Uri.parse(videoUrls[0]);
        videoView.setVideoURI(videoUri);
        
        // Media Controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        
        videoView.setOnPreparedListener(mp -> {
            Toast.makeText(MainActivity.this, "Video Started - PWF Physics", Toast.LENGTH_LONG).show();
            videoView.start();
        });
        
        videoView.setOnErrorListener((mp, what, extra) -> {
            String errorMsg = "Video Error: " + what + ", " + extra;
            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            
            // Try next video URL
            tryNextVideo();
            return true;
        });
        
        videoView.setOnCompletionListener(mp -> {
            Toast.makeText(MainActivity.this, "Video Completed - Restarting", Toast.LENGTH_SHORT).show();
            videoView.start(); // Auto-restart
        });
    }

    private void tryNextVideo() {
        // Alternative video URLs try करें
        String backupUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
        Uri backupUri = Uri.parse(backupUrl);
        videoView.setVideoURI(backupUri);
        videoView.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Service stop करें
        try {
            Intent serviceIntent = new Intent(this, VideoService.class);
            stopService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}