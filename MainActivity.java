package com.pwf.physics;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toast.makeText(this, "PWF App Started", Toast.LENGTH_SHORT).show();
        
        VideoView videoView = findViewById(R.id.videoView);
        
        // Online Video URL
        String videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        
        // Media Controls
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        
        videoView.setOnPreparedListener(mp -> {
            Toast.makeText(MainActivity.this, "Video Playing", Toast.LENGTH_SHORT).show();
            videoView.start();
        });
        
        videoView.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(MainActivity.this, "Video Error", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}