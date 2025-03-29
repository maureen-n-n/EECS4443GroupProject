package ca.yorku.eecs.mack.groupproject;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
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
    private int totalCorrectProgress = 0;
    private boolean showRedFlag = false;
    private int currentCardIndex;


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

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean isTimeout = data.getBooleanExtra("TIMEOUT", false);
            int result = data.getIntExtra("RESULT", RESULT_INCORRECT);
            String answeredHiragana = data.getStringExtra("HIRAGANA");
            String answeredRomaji = data.getStringExtra("ROMAJI");
            int previousProgress = totalCorrectProgress;

            Log.d(TAG, "=== Handling result ===");
            Log.d(TAG, "Received result: " +
                    (isTimeout ? "TIMEOUT" :
                            (result == RESULT_CORRECT ? "CORRECT" : "INCORRECT")) +
                    " for " + answeredHiragana + " (" + answeredRomaji + ")");

            if (answeredHiragana == null || answeredRomaji == null) {
                return;
            }

            HiraganaItem answeredItem = findCard(answeredHiragana, answeredRomaji);
            if (answeredItem == null) return;

            if (isTimeout) {
                handleTimeout();
                timeoutCount++;
                answeredItem.resetCorrectInRow();
            } else if (result == RESULT_CORRECT) {
                correctCount++;
                totalCorrectProgress++;
                answeredItem.incrementCorrectInRow();
            } else {
                incorrectCount++;
                answeredItem.resetCorrectInRow();
            }

            remainingHiragana.remove(answeredItem);

            if (result == RESULT_CORRECT) {
                if (answeredItem.getCorrectInRow() >= 2) {
                } else {
                    remainingHiragana.add(answeredItem);
                }
            } else {
                remainingHiragana.add(answeredItem);
            }

            Log.d(TAG, "Remaining cards: " + remainingHiragana.size());

            if (remainingHiragana.isEmpty()) {
                Log.i(TAG, "All cards completed!");
                showCompletionScreen();
            } else {
                currentHiragana = remainingHiragana.get(0);
                Log.d(TAG, "Next card: " + currentHiragana.getHiragana() +
                        " (" + currentHiragana.getRomaji() + ")");

                Intent nextIntent = new Intent(this, Card.class);
                nextIntent.putExtra("MODE", currentMode);
                nextIntent.putExtra("HIRAGANA", currentHiragana.getHiragana());
                nextIntent.putExtra("ROMAJI", currentHiragana.getRomaji());
                nextIntent.putExtra("CURRENT_PROGRESS", totalCorrectProgress);
                nextIntent.putExtra("SHOW_RED", showRedFlag);

                Log.d(TAG, "Launching next card with progress: " + totalCorrectProgress + "/40");
                startActivityForResult(nextIntent, 1);
            }
        }
    }

    private HiraganaItem findCard(String hiragana, String romaji) {
        for (HiraganaItem item : remainingHiragana) {
            if (item.getHiragana().equals(hiragana) && item.getRomaji().equals(romaji)) {
                return item;
            }
        }
        Log.e(TAG, "Card not found: " + hiragana);
        return null;
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
        if (remainingHiragana.isEmpty()) return;

        currentHiragana = remainingHiragana.get(0);
        Intent intent = new Intent(this, Card.class);
        intent.putExtra("MODE", currentMode);
        intent.putExtra("HIRAGANA", currentHiragana.getHiragana());
        intent.putExtra("ROMAJI", currentHiragana.getRomaji());
        intent.putExtra("CURRENT_PROGRESS", totalCorrectProgress);
        intent.putExtra("SHOW_RED", showRedFlag);
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

    private void calculateTotalProgress() {
        totalCorrectProgress = 0;
        for (HiraganaItem item : allHiragana) {
            totalCorrectProgress += item.getCorrectInRow();
        }
    }

    private void showCompletionScreen() {
        long sessionDuration = System.currentTimeMillis() - sessionStartTime;
        int totalAttempts = correctCount + incorrectCount + timeoutCount;


        Intent intent = new Intent(this, Learning_Complete.class);
        intent.putExtra("TOTAL_CARDS", totalAttempts);
        intent.putExtra("CORRECT", correctCount);
        intent.putExtra("INCORRECT", incorrectCount);
        intent.putExtra("TIMEOUTS", timeoutCount);
        intent.putExtra("DURATION", sessionDuration);
        startActivity(intent);
        finish();
    }
}