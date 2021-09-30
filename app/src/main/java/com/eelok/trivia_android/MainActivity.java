package com.eelok.trivia_android;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.eelok.trivia_android.data.AnswerListAsyncResponse;
import com.eelok.trivia_android.data.Repository;
import com.eelok.trivia_android.databinding.ActivityMainBinding;
import com.eelok.trivia_android.model.Question;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private List<Question> questionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        questionArrayList = new Repository().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                binding.questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());

//                binding.textViewOutOf.setText(getString(R.string.text_formated) +" " + currentQuestionIndex + "/" + questionArrayList.size());
                //todo  почему нужно тут
                updateCounterQuestionView(questionArrayList);
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuestionIndex = (currentQuestionIndex + 1) % questionArrayList.size();
                updateQuestionView();
            }
        });

        binding.buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
                updateQuestionView();
            }
        });

        binding.buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                updateQuestionView();
            }
        });

    }

    private void updateCounterQuestionView(List<Question> questionArrayList) {
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formated),
                currentQuestionIndex, questionArrayList.size()));
    }

    private void updateQuestionView() {
        String question = questionArrayList.get(currentQuestionIndex).getAnswer();
        binding.questionTextview.setText(question);
        updateCounterQuestionView(questionArrayList);
    }

    private void checkAnswer(boolean userChoice) {
        boolean correctAnswer = questionArrayList.get(currentQuestionIndex).isAnswerTrue();
        int messageId = 0;
        if (correctAnswer == userChoice) {
            messageId = R.string.correct_answer;
            fadeAnimation();
        } else {
            messageId = R.string.wrong_answer;
            shakeAnimation();
        }
        Snackbar.make(binding.cardView, messageId, Snackbar.LENGTH_SHORT).show();
    }

    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatCount(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        binding.cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextview.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextview.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}