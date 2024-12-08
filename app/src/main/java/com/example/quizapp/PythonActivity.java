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

public class PythonActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView questionNumberText;
    private TextView correctAnswerText;
    private ProgressBar progressBar;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button previousBtn, nextBtn;

    private int currentQuestionIndex = 0;

    private String[] questions = {
            "What is the output of the following code? \n\nx = 10\n" +
                    "y = 5\n" +
                    "print(x + y)",
            "Which of the following is the correct syntax for defining a function in Python?",
            "What does the following code output? \n\n x = [1, 2, 3]\n" +
                    "x.append(4)\n" +
                    "print(x)",
            "Which Python keyword is used to create an object in a class?",
            "What is the output of this code?\n\nx = \"Hello\"\n" +
                    "y = \"World\"\n" +
                    "print(x + y)",
            "What is the output of the following code?\n\n x = \"Python\"\n" +
                    "print(x[2:5])",
            "Which of the following is the correct way to open a file in write mode in Python?",
            "What will be the output of the following code?\n\ndef func(a, b=3):\n" +
                    "    return a + b\n" +
                    "\n" +
                    "print(func(2))",
            "What is the output of this code?\n\nx = [1, 2, 3, 4]\n" +
                    "print(x[::-1])",
            "What will be the output of the following code?\n\nx = {1: 'a', 2: 'b', 3: 'c'}\n" +
                    "x[2] = 'z'\n" +
                    "print(x)"
    };

    private String[][] options = {
            {"15", "5", "105", "Error"},
            {"def function_name[]:", "function_name():", "def function_name():", "function function_name():"},
            {"[1, 2, 3]", "[1, 2, 3, 4]", "4", "[4, 1, 2, 3]"},
            {"self", "object", "class", "def"},
            {"Hello World", "Hello+World", "HelloWorld", "Error"},
            {"Pyt", "tho", "yth", "ton"},
            {"open('file.txt', 'w')", "open('file.txt', 'r')", "open('file.txt', 'wb')", "open('file.txt', 'a')"},
            {"5", "3", "23", "Error"},
            {"[1, 2, 3, 4]", "[4, 3, 2, 1]", "Error", "[1, 3, 2, 4]"},
            {"{1: 'a', 2: 'z', 3: 'c'}", "{1: 'a', 2: 'b', 3: 'c'}", "{1: 'a', 2: 'z', 3: 'z'}", "Error"}
    };

    private int[] correctAnswers = {0, 2, 1, 2, 2, 1, 0, 0, 1, 0};

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
                Toast.makeText(PythonActivity.this, "This is the first question.", Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(PythonActivity.this, ResultActivity.class);
        intent.putExtra("attemptedQuestions", attemptedQuestions);
        intent.putExtra("correctAnswers", correctCount);
        intent.putExtra("incorrectAnswers", incorrectCount);
        startActivity(intent);
        finish();
    }
}
