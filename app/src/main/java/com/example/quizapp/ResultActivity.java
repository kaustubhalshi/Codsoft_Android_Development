package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView attemptedQuestionsText, correctQuestionsText, incorrectQuestionsText, scoreText;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        attemptedQuestionsText = findViewById(R.id.attemptedQuestionsText);
        correctQuestionsText = findViewById(R.id.correctQuestionsText);
        incorrectQuestionsText = findViewById(R.id.incorrectQuestionsText);
        scoreText = findViewById(R.id.scoreText);
        finishButton = findViewById(R.id.finishButton);

        // Get the intent that started this activity
        Intent intent = getIntent();
        int attemptedQuestions = intent.getIntExtra("attemptedQuestions", 0);
        int correctAnswers = intent.getIntExtra("correctAnswers", 0);
        int incorrectAnswers = intent.getIntExtra("incorrectAnswers", 0);

        // Calculate the score
        int score = correctAnswers * 10;  // Each question is worth 10 points

        // Display the results
        attemptedQuestionsText.setText("Attempted Questions: " + attemptedQuestions);
        correctQuestionsText.setText("Correct Questions: " + correctAnswers);
        incorrectQuestionsText.setText("Incorrect Questions: " + incorrectAnswers);
        scoreText.setText("Score: " + score + "/100");

        finishButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
            finish();
        });

    }
}
