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

public class JavaScriptActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView questionNumberText;
    private TextView correctAnswerText;
    private ProgressBar progressBar;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button previousBtn, nextBtn;

    private int currentQuestionIndex = 0;

    private String[] questions = {
            "What is the correct syntax to print a message in the console in JavaScript?",
            "Which of the following is the correct way to declare a variable in JavaScript?",
            "What will the following code output?\n" +
                    "\n" +
                    "let x = \"5\";\n" +
                    "let y = 10;\n" +
                    "console.log(x + y);",
            "Which of these data types is NOT supported in JavaScript?",
            "What will be the output of the following code?\n" +
                    "\n" +
                    "console.log(typeof null);",
            "Which keyword is used to define a constant in JavaScript?",
            "What does the following code output?\n" +
                    "\n" +
                    "let x = [1, 2, 3];\n" +
                    "console.log(x.length);",
            "What is the purpose of the isNaN() function in JavaScript?",
            "Which of the following methods is used to add an element to the end of an array in JavaScript?",
            "What is the result of the following code?\n" +
                    "\n" +
                    "function test() {\n" +
                    "    console.log(this);\n" +
                    "}\n" +
                    "test();"
    };



    private String[][] options = {
            {"print(\"Hello World\");", "log(\"Hello World\");", "console.log(\"Hello World\");", "document.log(\"Hello World\");"},
            {"variable x = 10;", "let x = 10;", "int x = 10;", "x := 10;"},
            {"15", "5 10", "510", "Error"},
            {"String", "Boolean", "Float", "Undefined"},
            {"object", "null", "undefined", "string"},
            {"let", "var", "constant", "const"},
            {"2", "3", "4", "Error"},
            {"To check if a value is null", "To check if a value is not a number", "To check if a value is an array", "To check if a value is a string"},
            {"add()", "push()", "insert()", "append()"},
            {"undefined", "null", "{The window object (in browsers)", "An error"}
    };

    private int[] correctAnswers = {2, 1, 2, 2, 0, 3, 1, 1, 1, 2};

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
                Toast.makeText(JavaScriptActivity.this, "This is the first question.", Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(JavaScriptActivity.this, ResultActivity.class);
        intent.putExtra("attemptedQuestions", attemptedQuestions);
        intent.putExtra("correctAnswers", correctCount);
        intent.putExtra("incorrectAnswers", incorrectCount);
        startActivity(intent);
        finish();
    }
}
