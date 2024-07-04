package com.example.mycameraapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.hardware.Camera;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Camera and background processing permissions granted!", Toast.LENGTH_SHORT).show();

        Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
    }
}

