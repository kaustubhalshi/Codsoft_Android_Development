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

public class CppActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView questionNumberText;
    private TextView correctAnswerText;
    private ProgressBar progressBar;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button previousBtn, nextBtn;

    private int currentQuestionIndex = 0;


    private String[] questions = {
            "Which of the following is the correct syntax to include a header file in C++?",
            "What is the size of an int in C++ (on most platforms)?",
            "Which of the following is a valid way to declare a pointer in C++?",
            "What will the following code output?\n\n" +
                    "#include <iostream>\n" +
                    "using namespace std;\n" +
                    "\n" +
                    "int main() {\n" +
                    "    int x = 10;\n" +
                    "    cout << x++ << \" \" << ++x;\n" +
                    "    return 0;\n" +
                    "}",
            "Which of these features is NOT part of object-oriented programming?",
            "What does the following code output?\n" +
                    "\n" +

                    "#include <iostream>\n" +
                    "using namespace std;\n" +
                    "class A {\n" +
                    "public:\n" +
                    "    A() { cout << \"Constructor \"; }\n" +
                    "    ~A() { cout << \"Destructor \"; }\n" +
                    "};\n" +
                    "\n" +
                    "int main() {\n" +
                    "    A obj;\n" +
                    "    return 0;\n" +
                    "}",
            "Which of the following is used to dynamically allocate memory in C++?",
            "What is the purpose of a virtual function in C++?",
            "What is the output of the following code?\n" +
                    "\n" +
                    "#include <iostream>\n" +
                    "using namespace std;\n" +
                    "int main() {\n" +
                    "    int arr[] = {1, 2, 3, 4};\n" +
                    "    cout << arr[2];\n" +
                    "    return 0;\n" +
                    "}",
            "Which of the following is NOT a C++ keyword?"
    };




    private String[][] options = {
            {"#include <header>", "#include \"header.h\"", "#include [header.h]", "#header include"},
            {"2 bytes", "4 bytes", "8 bytes", "Depends on the system"},
            {"int p;", "int *p;", "int &p;", "pointer p;"},
            {"10 11", "10 12", "11 12", "Error"},
            {"Encapsulation", "Polymorphism", "Abstraction", "Structured Programming"},
            {"Constructor Destructor", "Destructor Constructor", "Constructor", "Destructor"},
            {"malloc", "alloc", "new", "create"},
            {"To make a function inline", "To allow function overriding in derived classes", "To make the function private", "To overload the function"},
            {"1", "2", "3", "4"},
            {"try", "catch", "throw", "finally"}
    };

    private int[] correctAnswers = {0, 3, 1, 1, 3, 0, 2, 1, 2, 3};

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
                Toast.makeText(CppActivity.this, "This is the first question.", Toast.LENGTH_SHORT).show();
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
            }, 2000); // 5 seconds delay
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


        Intent intent = new Intent(CppActivity.this, ResultActivity.class);
        intent.putExtra("attemptedQuestions", attemptedQuestions);
        intent.putExtra("correctAnswers", correctCount);
        intent.putExtra("incorrectAnswers", incorrectCount);
        startActivity(intent);
        finish();
    }
}
