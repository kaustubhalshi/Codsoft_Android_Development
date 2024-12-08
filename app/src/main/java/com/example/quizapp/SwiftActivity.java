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

public class SwiftActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView questionNumberText;
    private TextView correctAnswerText;
    private ProgressBar progressBar;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button previousBtn, nextBtn;

    private int currentQuestionIndex = 0;

    private String[] questions = {
            "How do you declare a constant in Swift?",
            "What is the correct syntax to define a function in Swift?",
            "What is the output of the following code?\n" +
                    "\n" +
                    "let x = 5\n" +
                    "let y = 10\n" +
                    "print(x + y)",
            "What keyword is used to define a variable in Swift?",
            "Which of the following is NOT a Swift data type?",
            "What is the output of the following code?\n" +
                    "\n" +
                    "var names = [\"Alice\", \"Bob\", \"Charlie\"]\n" +
                    "print(names[1])",
            "What is the purpose of the guard statement in Swift?",
            "How do you define a class in Swift?",
            "What is the difference between == and === in Swift?",
            "What is the output of this code?\n" +
                    "\n" +
                    "let isSwiftFun = true\n" +
                    "if isSwiftFun {\n" +
                    "    print(\"Yes, Swift is fun!\")\n" +
                    "} else {\n" +
                    "    print(\"No, Swift is not fun.\")\n" +
                    "}"
    };


    private String[][] options = {
            {"let x = 10", "var x = 10", "const x = 10", "final x = 10"},
            {"func myFunction() { }", "def myFunction { }", "function myFunction() { }", "myFunction() => { }"},
            {"15", "510", "5 + 10", "Error"},
            {"var", "let", "val", "const"},
            {"String", "Int", "Float", "Decimal"},
            {"Alice", "Bob", "Charlie", "Error"},
            {"To terminate the program", "To handle errors", "To exit early if a condition is not met", "To define a variable"},
            {"class MyClass { }", "struct MyClass { }", "Class MyClass { }", "function MyClass { }"},
            {"== compares objects, === compares values", "== checks value equality, === checks reference equality", "== is for numbers, === is for strings", "Both are identical in Swift"},
            {"Yes, Swift is fun!", "No, Swift is not fun.", "Error", "True"}
    };

    private int[] correctAnswers = {0, 1, 0, 0, 3, 1, 2, 0, 1, 0};

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

        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;
        }

        loadQuestion();

        previousBtn.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                saveSelectedAnswer();
                currentQuestionIndex--;
                loadQuestion();
            } else {
                Toast.makeText(SwiftActivity.this, "This is the first question.", Toast.LENGTH_SHORT).show();
            }
        });

        nextBtn.setOnClickListener(v -> {
            saveSelectedAnswer();
            showAnswerFeedback();

            nextBtn.setEnabled(false);
            previousBtn.setEnabled(false);

            new Handler().postDelayed(() -> {
                nextBtn.setEnabled(true);
                previousBtn.setEnabled(true);
            }, 2000);
        });
    }

    private void loadQuestion() {
        questionText.setText(questions[currentQuestionIndex]);
        questionNumberText.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.length);

        option1.setText(options[currentQuestionIndex][0]);
        option2.setText(options[currentQuestionIndex][1]);
        option3.setText(options[currentQuestionIndex][2]);
        option4.setText(options[currentQuestionIndex][3]);

        optionsGroup.clearCheck();
        if (selectedAnswers[currentQuestionIndex] != -1) {
            int selectedOption = selectedAnswers[currentQuestionIndex];
            if (selectedOption == 0) option1.setChecked(true);
            else if (selectedOption == 1) option2.setChecked(true);
            else if (selectedOption == 2) option3.setChecked(true);
            else if (selectedOption == 3) option4.setChecked(true);
        }

        correctAnswerText.setVisibility(View.GONE);
        progressBar.setProgress(0);
    }

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

        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        animator.setDuration(2000);
        animator.start();

        new Handler().postDelayed(() -> {
            if (currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                loadQuestion();
            } else {
                goToResults();
            }
        }, 2000);
    }

    private boolean checkAnswer() {
        int selectedOption = optionsGroup.getCheckedRadioButtonId();
        if (selectedOption == -1) return false;

        int selectedAnswerIndex = optionsGroup.indexOfChild(findViewById(selectedOption));
        return selectedAnswerIndex == correctAnswers[currentQuestionIndex];
    }

    private void saveSelectedAnswer() {
        int selectedOption = optionsGroup.getCheckedRadioButtonId();
        if (selectedOption != -1) {
            int selectedAnswerIndex = optionsGroup.indexOfChild(findViewById(selectedOption));
            selectedAnswers[currentQuestionIndex] = selectedAnswerIndex;
        }
    }

    private void goToResults() {
        int attemptedQuestions = 0;
        int correctCount = 0;
        int incorrectCount = 0;

        for (int i = 0; i < selectedAnswers.length; i++) {
            if (selectedAnswers[i] != -1) {
                attemptedQuestions++;
                if (selectedAnswers[i] == correctAnswers[i]) {
                    correctCount++;
                } else {
                    incorrectCount++;
                }
            }
        }

        Intent intent = new Intent(SwiftActivity.this, ResultActivity.class);
        intent.putExtra("attemptedQuestions", attemptedQuestions);
        intent.putExtra("correctAnswers", correctCount);
        intent.putExtra("incorrectAnswers", incorrectCount);
        startActivity(intent);
        finish();
    }
}
