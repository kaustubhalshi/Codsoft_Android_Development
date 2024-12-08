package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class MainActivity extends AppCompatActivity implements QuizBottomSheetDialog.QuizStartListener {

    private static final String SHARED_PREFS_NAME = "QuizAppPrefs";
    private static final String POINTS_KEY = "points";
    private static final String USER_FIRST_NAME_KEY = "userFirstName";

    private TextView pointsText;
    private TextView userNameTextView;
    private int points = 0;
    private Handler handler = new Handler();

    protected void onResume() {
        super.onResume();


        File imageFile = new File(getFilesDir(), "profile_image.png");
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .circleCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) findViewById(R.id.profileImage));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("QuizAppPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");

        if (name.isEmpty() || email.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);

            finish();
        }

        pointsText = findViewById(R.id.pointsText);
        userNameTextView = findViewById(R.id.userName);
        ImageView profileImage = findViewById(R.id.profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


        loadPoints();

        loadUserName();

        LinearLayout pythonLayout = findViewById(R.id.pythonLayout);
        LinearLayout javaLayout = findViewById(R.id.javaLayout);
        LinearLayout cppLayout = findViewById(R.id.CppLayout);
        LinearLayout javaScriptLayout = findViewById(R.id.javaScriptLayout);
        LinearLayout rubyLayout = findViewById(R.id.rubyLayout);
        LinearLayout swiftLayout = findViewById(R.id.swiftLayout);

        pythonLayout.setOnClickListener(v -> showQuizBottomSheet("Python"));
        javaLayout.setOnClickListener(v -> showQuizBottomSheet("Java"));
        cppLayout.setOnClickListener(v -> showQuizBottomSheet("C++"));
        javaScriptLayout.setOnClickListener(v -> showQuizBottomSheet("JavaScript"));
        rubyLayout.setOnClickListener(v -> showQuizBottomSheet("Ruby"));
        swiftLayout.setOnClickListener(v -> showQuizBottomSheet("Swift"));
    }

    private void loadUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String firstName = sharedPreferences.getString(USER_FIRST_NAME_KEY, null);

        if (firstName == null) {
            Intent intent = getIntent();
            firstName = intent.getStringExtra("USER_FIRST_NAME");

            if (firstName != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USER_FIRST_NAME_KEY, firstName);
                editor.apply();
            }
        }

        if (firstName != null) {

            userNameTextView.setText("Hi, " + firstName);
        }
    }

    private void showQuizBottomSheet(String quizType) {
        QuizBottomSheetDialog bottomSheet = QuizBottomSheetDialog.newInstance(quizType);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void onQuizStarted() {
        handler.postDelayed(() -> {
            points += 50;
            pointsText.setText(String.valueOf(points));
            savePoints();
        }, 60000);
    }

    private void savePoints() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(POINTS_KEY, points);
        editor.apply();
    }

    private void loadPoints() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        points = sharedPreferences.getInt(POINTS_KEY, 0);
        pointsText.setText(String.valueOf(points));
    }
}
