package com.eelok.trivia_android.data;

import com.eelok.trivia_android.model.Question;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {

    void processFinished(ArrayList<Question> questionArrayList);

}
