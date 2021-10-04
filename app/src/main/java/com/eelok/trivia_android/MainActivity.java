package com.eelok.trivia_android;

import android.graphics.Color;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.util.Log;
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
import com.eelok.trivia_android.model.Score;
import com.eelok.trivia_android.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private List<Question> questionArrayList;
    private int countScore = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        prefs = new Prefs(MainActivity.this);

        currentQuestionIndex = prefs.getQuizState();
        Log.d("QUIZ_STAT", "onCreate " + currentQuestionIndex);
        binding.highestScore.setText(MessageFormat.format("Highest score: {0}", String.valueOf(prefs.getHighestScore())));
        updateCurrentScore();

        questionArrayList = new Repository().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                binding.questionTextview.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                //todo  почему нужно тут
                updateCounterQuestionView(questionArrayList);
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextQuestion();
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

    private void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionArrayList.size();
        updateQuestionView();
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
            incrementScore();
            fadeAnimation();
        } else {
            messageId = R.string.wrong_answer;
            decrementScore();
            shakeAnimation();
        }
        Snackbar.make(binding.cardView, messageId, Snackbar.LENGTH_SHORT).show();
    }

    private void updateCurrentScore() {
        binding.currentScore.setText(String.format(getString(R.string.current_score), score.getScore()));
    }

    private void incrementScore() {
        countScore = countScore + 10;
        score.setScore(countScore);
        updateCurrentScore();
    }

    private void decrementScore() {
        if (countScore > 0) {
            countScore = countScore - 10;
        }
        score.setScore(countScore);
        updateCurrentScore();
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
                getNextQuestion();
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
                getNextQuestion();
                binding.questionTextview.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.saveQuizState(currentQuestionIndex);
        Log.d("STATE", "onPause " + prefs.getQuizState());
        Log.d("score", "onPause" + prefs.getHighestScore());
        super.onPause();
    }
}