package ca.yorku.eecs.mack.groupproject;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Learning extends AppCompatActivity {
    private List<HiraganaItem> allHiragana, remainingHiragana;
    private HiraganaItem currentHiragana;
    private static final String TAG = "LearningActivity";
    private String currentMode;
    private long sessionStartTime;
    private int totalCards;
    private int correctCount;
    private int incorrectCount;
    private int timeoutCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning);
        currentMode = getIntent().getStringExtra("MODE");

        sessionStartTime = System.currentTimeMillis();

        // Initialize the ordered list of all of our hiragana characters
        createHiraganaList();
        totalCards = allHiragana.size();
        // Duplicate it and shuffle it to create our working list
        remainingHiragana = new ArrayList<>(allHiragana);
        Collections.shuffle(remainingHiragana);

        Log.d(TAG, "Starting learning session. Total characters: " + remainingHiragana.size());
        // Start the process of learning
        startLearning();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final int RESULT_CORRECT = 1, RESULT_INCORRECT = 2;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                int result = data.getIntExtra("RESULT", RESULT_INCORRECT);
                boolean isTimeout = data.getBooleanExtra("TIMEOUT", false);

                if (isTimeout) {
                    handleTimeout();
                } else {
                    handleAnswerResult(result == RESULT_CORRECT);
                    Log.d(TAG, "Remaining before processing: " + remainingHiragana.size());
                }

                if (isTimeout) {
                    remainingHiragana.remove(currentHiragana);
                    currentHiragana.resetCorrectInRow();
                    remainingHiragana.add(currentHiragana);
                    Log.d(TAG, "Remaining before processing: " + remainingHiragana.size());
                } else {
                    if (result == 1) {
                        remainingHiragana.remove(currentHiragana);
                        currentHiragana.incrementCorrectInRow();
                        if (currentHiragana.getCorrectInRow() < 2) {
                            remainingHiragana.add(currentHiragana);
                        }
                        Log.d(TAG, "After processing | Remaining: " + remainingHiragana.size());
                    } else {
                        currentHiragana.resetCorrectInRow();
                        remainingHiragana.remove(currentHiragana);
                        remainingHiragana.add(currentHiragana);
                        Log.d(TAG, "After processing | Remaining: " + remainingHiragana.size());
                    }
                }

                if (remainingHiragana.isEmpty()) {
                    showCompletionScreen();
                } else {
                    currentHiragana = remainingHiragana.get(0);
                    Log.d(TAG, "Next character: " + currentHiragana.getHiragana() + ", Sound: " + currentHiragana.getRomaji());
                    newCard();
                }
            } else {
                Log.e(TAG, "Unexpected result: " + resultCode);
            }
        }
    }

    private void createHiraganaList() {
        allHiragana = new ArrayList<>();
        allHiragana.add(new HiraganaItem("あ", "a"));
        allHiragana.add(new HiraganaItem("か", "ka"));
        allHiragana.add(new HiraganaItem("ま", "ma"));
        allHiragana.add(new HiraganaItem("ら", "ra"));
        allHiragana.add(new HiraganaItem("い", "i"));
        allHiragana.add(new HiraganaItem("き", "ki"));
        allHiragana.add(new HiraganaItem("み", "mi"));
        allHiragana.add(new HiraganaItem("り", "ri"));
        allHiragana.add(new HiraganaItem("う", "u"));
        allHiragana.add(new HiraganaItem("く", "ku"));
        allHiragana.add(new HiraganaItem("む", "mu"));
        allHiragana.add(new HiraganaItem("る", "ru"));
        allHiragana.add(new HiraganaItem("え", "e"));
        allHiragana.add(new HiraganaItem("け", "ke"));
        allHiragana.add(new HiraganaItem("め", "me"));
        allHiragana.add(new HiraganaItem("れ", "re"));
        allHiragana.add(new HiraganaItem("お", "o"));
        allHiragana.add(new HiraganaItem("こ", "ko"));
        allHiragana.add(new HiraganaItem("も", "mo"));
        allHiragana.add(new HiraganaItem("ろ", "ro"));
    }


    private void newCard() {
        // Start a new card class and pass it via intent
        Intent intent = new Intent(this, Card.class);
        intent.putExtra("MODE", currentMode);
        intent.putExtra("hiraganaItem", currentHiragana);
        startActivityForResult(intent, 1);
    }

    private void startLearning() {
        currentHiragana = remainingHiragana.get(0);
        newCard();
    }

    private void handleAnswerResult(boolean isCorrect) {
        if(isCorrect) {
            correctCount++;
        } else {
            incorrectCount++;
        }
    }

    private void handleTimeout() {
        timeoutCount++;
    }

    private void showCompletionScreen() {
        long sessionDuration = System.currentTimeMillis() - sessionStartTime;

        Intent intent = new Intent(this, Learning_Complete.class);
        intent.putExtra("TOTAL_CARDS", totalCards);
        intent.putExtra("CORRECT", correctCount);
        intent.putExtra("INCORRECT", incorrectCount);
        intent.putExtra("TIMEOUTS", timeoutCount);
        intent.putExtra("DURATION", sessionDuration);
        startActivity(intent);
        finish();
    }
}