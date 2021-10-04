package com.eelok.trivia_android.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String HIGHEST_SCORE = "highest_score";
    private static final String QUIZ_STATE = "quiz_state";
    private final SharedPreferences preferences;


    public Prefs(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveHighestScore(int score){
        int lastHighestScore = preferences.getInt(HIGHEST_SCORE, 0);

        if(score > lastHighestScore){
            preferences.edit().putInt(HIGHEST_SCORE, score).apply();
        }
    }

    public int getHighestScore(){
        return preferences.getInt(HIGHEST_SCORE, 0);
    }

    public void saveQuizState(int index){
        preferences.edit().putInt(QUIZ_STATE, index).apply();
    }

    public int getQuizState(){
        return preferences.getInt(QUIZ_STATE, 0);
    }

}
