package com.example.automativesurvalliancesix;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int CAMERA_PERMISSION_CODE = 101;
    private final int IMAGE_CAPTURE_CODE = 102;

    private TextView textMediaDisplay;
    private ImageView imageMedia;
    private Button buttonViewMedia;
    private Button buttonCaptureImage;
    private TextView textAlerts;
    private ListView listAlerts;

    private Uri capturedImageUri;

    private List<String> alertData = new ArrayList<>();
    private AlertsAdapter alertsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        textMediaDisplay = findViewById(R.id.text_media_display);
        imageMedia = findViewById(R.id.image_media);
        buttonViewMedia = findViewById(R.id.button_view_media);
        buttonCaptureImage = findViewById(R.id.button_capture_image);
        textAlerts = findViewById(R.id.text_alerts);
        listAlerts = findViewById(R.id.list_alerts);

        // Setup button click listeners
        buttonViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capturedImageUri != null) {
                    showCapturedMediaAndAlert(capturedImageUri);
                } else {
                    showToast("No media to view");
                }
            }
        });

        buttonCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            }
        });


        // Setup list adapter for alerts
        alertsAdapter = new AlertsAdapter(this, alertData);
        listAlerts.setAdapter(alertsAdapter);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE
        );
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                showToast("Camera permission denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                capturedImageUri = imageUri;
                showToast("Image captured successfully!");
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        capturedImageUri = saveBitmapAndGetUri(imageBitmap);
                        showToast("Image captured successfully!");
                    } else {
                        showToast("Failed to capture image");
                    }
                }
            }
        }
    }

    private Uri saveBitmapAndGetUri(Bitmap bitmap) {
        // Implement logic to save bitmap to a file and return its URI
        // Placeholder implementation
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }

    private void showCapturedMediaAndAlert(Uri imageUri) {
        // Display captured image
        Glide.with(this).load(imageUri).into(imageMedia);

        // Update text in ListView
        alertData.clear();
        alertData.add("No Issues");
        alertData.add("Everything Looks Good");
        alertData.add("No Interruption");
        alertsAdapter.notifyDataSetChanged();

        showToast("Displaying captured media and alert");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
}
}