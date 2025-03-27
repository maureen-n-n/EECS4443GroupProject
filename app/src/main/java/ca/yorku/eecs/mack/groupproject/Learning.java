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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning);
        currentMode = getIntent().getStringExtra("MODE");

        // Initialize the ordered list of all of our hiragana characters
        createHiraganaList();
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

        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean isTimeout = data.getBooleanExtra("TIMEOUT", false);

            if (isTimeout) {
                remainingHiragana.remove(currentHiragana);
                currentHiragana.resetCorrectInRow();
                remainingHiragana.add(currentHiragana);
            } else {
                Log.d(TAG, "Remaining before processing: " + remainingHiragana.size());
                if (resultCode == RESULT_CORRECT) {
                    remainingHiragana.remove(currentHiragana);
                    currentHiragana.incrementCorrectInRow();
                    Log.d(TAG, "After processing | Remaining: " + remainingHiragana.size());
                    if (currentHiragana.getCorrectInRow() < 2) {
                        remainingHiragana.add(currentHiragana);
                    }
                } else if (resultCode == RESULT_INCORRECT) {
                    currentHiragana.resetCorrectInRow();
                    remainingHiragana.remove(currentHiragana);
                    remainingHiragana.add(0, currentHiragana);
                    Log.d(TAG, "After processing | Remaining: " + remainingHiragana.size());
                }
            }

            if (remainingHiragana.isEmpty()) {
                Log.i(TAG, "All characters completed!");
                startActivity(new Intent(this, Learning_Complete.class));
                finish();
            } else {
                currentHiragana = remainingHiragana.get(0);
                newCard();
                Log.d(TAG, "Next character: " + currentHiragana.getHiragana() + ", Sound: " + currentHiragana.getRomaji());
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
        // Select the first character in our list
        currentHiragana = remainingHiragana.get(0);
        // Create our first card
        newCard();
    }
}