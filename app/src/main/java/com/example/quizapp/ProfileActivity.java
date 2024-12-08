package com.example.quizapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;
    private static final int GALLERY_PERMISSION_CODE = 103;
    private static final String PROFILE_IMAGE_FILE = "profile_image.png";
    private static final String PREFS_NAME = "QuizAppPrefs";

    // UI Elements
    private ImageView profileImageView;
    private EditText nameInput;
    private EditText emailInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        profileImageView = findViewById(R.id.profileImage);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        saveButton = findViewById(R.id.saveButton);

        // Load saved data into the UI
        loadSavedData();

        // Set click listeners
        profileImageView.setOnClickListener(v -> showImagePickerDialog());
        saveButton.setOnClickListener(v -> saveDetailsAndProceed());
    }

    private void loadSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved values
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");

        // Log loaded data for debugging
        Log.d(TAG, "Loaded Data: Name=" + name + ", Email=" + email);

        // Update UI
        nameInput.setText(name);
        emailInput.setText(email);

        // Load profile image
        File imageFile = new File(getFilesDir(), PROFILE_IMAGE_FILE);
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .circleCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(profileImageView);
        }
    }

    private void saveDetailsAndProceed() {
        // Get user inputs
        String fullName = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            nameInput.setError("Name is required");
            nameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email address");
            emailInput.requestFocus();
            return;
        }

        // Extract the first name
        String firstName = fullName.split(" ")[0];

        // Save details to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", fullName);
        editor.putString("email", email);
        editor.putString("userFirstName", firstName);
        editor.apply();

        // Log saved data
        Log.d(TAG, "Saved Data: Name=" + fullName + ", Email=" + email + ", FirstName=" + firstName);

        // Reload data for confirmation
        loadSavedData();

        // Navigate to MainActivity
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.putExtra("USER_FIRST_NAME", firstName);
        startActivity(intent);

        // Apply transition animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Finish current activity
        finish();
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                requestCameraPermission();
            } else {
                requestGalleryPermission();
            }
        });
        builder.show();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_CODE);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                saveProfileImage(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    saveProfileImage(bitmap);
                } catch (IOException e) {
                    Log.e(TAG, "Error loading image from gallery", e);
                }
            }
        }
    }

    private void saveProfileImage(Bitmap bitmap) {
        try {
            File file = new File(getFilesDir(), PROFILE_IMAGE_FILE);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // Update profile image
            Glide.with(this)
                    .load(file)
                    .circleCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(profileImageView);
        } catch (IOException e) {
            Log.e(TAG, "Error saving profile image", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Gallery permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
