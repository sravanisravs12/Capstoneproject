package com.example.cameratask4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "MainActivity";
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView capturedImageView;
    private boolean previewing = false;
    private byte[] previousFrame;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int MOTION_THRESHOLD = 15000; // Adjust as necessary

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.cameraPreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        capturedImageView = findViewById(R.id.capturedImageView);

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    try {
                        camera.takePicture(null, null, pictureCallback);
                    } catch (Exception e) {
                        Log.e(TAG, "Error taking picture: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (camera == null) {
            openCamera();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(this);
            camera.startPreview();
            previewing = true;
        } catch (IOException e) {
            Log.e(TAG, "Error setting camera preview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            previewing = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        try {
            camera = Camera.open();
            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
            Camera.Size previewSize = getOptimalPreviewSize(previewSizes, surfaceView.getWidth(), surfaceView.getHeight());
            params.setPreviewSize(previewSize.width, previewSize.height);
            camera.setParameters(params);
        } catch (Exception e) {
            Log.e(TAG, "Error opening camera: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                capturedImageView.setImageBitmap(bitmap);
                capturedImageView.setVisibility(View.VISIBLE);
            }
            camera.startPreview(); // Restart preview for continuous capture
        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (previousFrame != null) {
            int diff = calculateFrameDifference(data, previousFrame);
            if (diff > MOTION_THRESHOLD) {
                Log.d(TAG, "Motion detected. Capturing image.");
                camera.takePicture(null, null, pictureCallback);
            }
        }
        previousFrame = data.clone();
    }

    private int calculateFrameDifference(byte[] currentFrame, byte[] previousFrame) {
        int diff = 0;
        for (int i = 0; i < currentFrame.length; i++) {
            diff += Math.abs((currentFrame[i] & 0xFF) - (previousFrame[i] & 0xFF));
        }
        return diff;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
}
}