package com.example.quizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QuizBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String QUIZ_TYPE_KEY = "quiz_type";
    private QuizStartListener quizStartListener;

    public static QuizBottomSheetDialog newInstance(String quizType) {
        QuizBottomSheetDialog fragment = new QuizBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(QUIZ_TYPE_KEY, quizType);
        fragment.setArguments(args);
        return fragment;
    }

    // Interface to communicate with MainActivity
    public interface QuizStartListener {
        void onQuizStarted();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof QuizStartListener) {
            quizStartListener = (QuizStartListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement QuizStartListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_quiz, container, false);

        String quizType = getArguments() != null ? getArguments().getString(QUIZ_TYPE_KEY) : "";

        Button startQuizButton = view.findViewById(R.id.startQuizButton);
        TextView cancelButton = view.findViewById(R.id.cancelButton);

        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (quizType) {
                    case "Python":
                        intent = new Intent(getActivity(), PythonActivity.class);
                        break;
                    case "Java":
                        intent = new Intent(getActivity(), JavaActivity.class);
                        break;
                    case "C++":
                        intent = new Intent(getActivity(), CppActivity.class);
                        break;
                    case "JavaScript":
                        intent = new Intent(getActivity(), JavaScriptActivity.class);
                        break;
                    case "Ruby":
                        intent = new Intent(getActivity(), RubyActivity.class);
                        break;
                    case "Swift":
                        intent = new Intent(getActivity(), SwiftActivity.class);
                        break;
                }

                if (intent != null) {
                    startActivity(intent);
                    if (quizStartListener != null) {
                        quizStartListener.onQuizStarted(); // Notify MainActivity
                    }
                }
                dismiss(); // Close the bottom sheet
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the bottom sheet
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        quizStartListener = null; // Avoid memory leaks
    }
}
