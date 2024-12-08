package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.animation.ObjectAnimator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JavaActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView questionNumberText;
    private TextView correctAnswerText;
    private ProgressBar progressBar;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button previousBtn, nextBtn;

    private int currentQuestionIndex = 0;

    // Array of questions
    private String[] questions = {
            "Which of the following is the correct syntax for declaring a variable in Java?",
            "Which of these access modifiers makes a variable visible only within the same package?",
            "What will be the output of the following code? \n\n int x = 5;\n" +"System.out.println(++x);",
            "Which of the following is not a valid data type in Java?",
            "Which of the following loops is guaranteed to execute at least once?",
            "What is the output of the following code?\n\n String str = \"Java\";\n" +
                    "System.out.println(str.charAt(2));",
            "In Java, which keyword is used to inherit a class?",
            "Which of the following is a correct way to start a thread in Java?",
            "What will be the output of the following code?\n\n int[] arr = {1, 2, 3, 4};\n" +
                    "System.out.println(arr[2]);",
            "Which of the following statements about Java is true?"
    };

    // Array of options corresponding to each question
    private String[][] options = {
            {"int x = 10;", "integer x = 10;", "num x = 10;", "number x = 10;"},
            {"public", "protected", "private", "default"},
            {"5", "6", "4", "Error"},
            {"int", "double", "char", "real"},
            {"for loop", "while loop", "do-while loop", "for-each loop"},
            {"J", "a", "v", "Error"},
            {"implements", "extends", "inherits", "overrides"},
            {"Thread.run();", "Thread.execute();", "Thread.start();", "Thread.init();"},
            {"1", "2", "3", "4"},
            {"Java is a platform-independent language.", "Java does not support object-oriented programming.", "Java can only be used on Windows.", "Java does not support multithreading."}
    };

    // Array of correct answers (index of the correct option for each question)
    private int[] correctAnswers = {0, 3, 1, 3, 2, 2, 1, 2, 2, 0};

    // Array to store the selected answers for each question (-1 if no answer selected)
    private int[] selectedAnswers = new int[questions.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        questionText = findViewById(R.id.questionText);
        questionNumberText = findViewById(R.id.questionNumberText);
        correctAnswerText = findViewById(R.id.correctAnswerText);
        progressBar = findViewById(R.id.progressBar);
        optionsGroup = findViewById(R.id.optionsGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);

        // Initialize selectedAnswers with -1 to indicate no selection
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;
        }

        // Load the first question
        loadQuestion();

        previousBtn.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                saveSelectedAnswer(); // Save the current selection
                currentQuestionIndex--;
                loadQuestion();
            } else {
                Toast.makeText(JavaActivity.this, "This is the first question.", Toast.LENGTH_SHORT).show();
            }
        });

        nextBtn.setOnClickListener(v -> {
            saveSelectedAnswer(); // Save the current selection
            showAnswerFeedback(); // Show feedback and proceed to the next question after a delay

            // Disable both nextBtn and previousBtn for 5 seconds
            nextBtn.setEnabled(false);
            previousBtn.setEnabled(false);

            // Re-enable both buttons after a delay of 5 seconds
            new Handler().postDelayed(() -> {
                nextBtn.setEnabled(true);
                previousBtn.setEnabled(true);
            }, 2000); // 5 seconds delay
        });
    }

    // Method to load question and options
    private void loadQuestion() {
        questionText.setText(questions[currentQuestionIndex]);
        questionNumberText.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.length); // Update question number

        option1.setText(options[currentQuestionIndex][0]);
        option2.setText(options[currentQuestionIndex][1]);
        option3.setText(options[currentQuestionIndex][2]);
        option4.setText(options[currentQuestionIndex][3]);

        // Clear previous selection and set saved answer if exists
        optionsGroup.clearCheck();
        if (selectedAnswers[currentQuestionIndex] != -1) {
            int selectedOption = selectedAnswers[currentQuestionIndex];
            if (selectedOption == 0) option1.setChecked(true);
            else if (selectedOption == 1) option2.setChecked(true);
            else if (selectedOption == 2) option3.setChecked(true);
            else if (selectedOption == 3) option4.setChecked(true);
        }

        // Hide feedback text and reset progress bar
        correctAnswerText.setVisibility(View.GONE);
        progressBar.setProgress(0);
    }

    // Method to show feedback and delay for 2 seconds
    private void showAnswerFeedback() {
        boolean isCorrect = checkAnswer();
        if (isCorrect) {
            correctAnswerText.setText("✅ Correct! 😊");
            correctAnswerText.setTextColor(getResources().getColor(R.color.correctGreen));
        } else {
            correctAnswerText.setText("❌ Incorrect! 😞");
            correctAnswerText.setTextColor(getResources().getColor(R.color.incorrectRed));
        }
        correctAnswerText.setVisibility(View.VISIBLE);

        // Animate progress bar for 2 seconds smoothly
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        animator.setDuration(2000); // Animation duration: 2 seconds
        animator.start();

        // After animation completes, move to the next question
        new Handler().postDelayed(() -> {
            if (currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                loadQuestion();
            } else {
                goToResults();
            }
        }, 2000); // Delay of 2 seconds to match animation duration
    }

    // Method to check if the selected answer is correct
    private boolean checkAnswer() {
        int selectedOption = optionsGroup.getCheckedRadioButtonId();
        if (selectedOption == -1) return false;  // No option selected

        // Get the index of selected option
        int selectedAnswerIndex = optionsGroup.indexOfChild(findViewById(selectedOption));
        return selectedAnswerIndex == correctAnswers[currentQuestionIndex];
    }

    // Method to save the selected answer
    private void saveSelectedAnswer() {
        int selectedOption = optionsGroup.getCheckedRadioButtonId();
        if (selectedOption != -1) {
            int selectedAnswerIndex = optionsGroup.indexOfChild(findViewById(selectedOption));
            selectedAnswers[currentQuestionIndex] = selectedAnswerIndex;
        }
    }

    // Method to go to the ResultActivity
    private void goToResults() {
        int attemptedQuestions = 0;
        int correctCount = 0;
        int incorrectCount = 0;

        for (int i = 0; i < selectedAnswers.length; i++) {
            if (selectedAnswers[i] != -1) {  // Question was attempted
                attemptedQuestions++;
                if (selectedAnswers[i] == correctAnswers[i]) {
                    correctCount++;
                } else {
                    incorrectCount++;
                }
            }
        }

        // Pass data to ResultActivity
        Intent intent = new Intent(JavaActivity.this, ResultActivity.class);
        intent.putExtra("attemptedQuestions", attemptedQuestions);
        intent.putExtra("correctAnswers", correctCount);
        intent.putExtra("incorrectAnswers", incorrectCount);
        startActivity(intent);
        finish();
    }
}
