package ca.yorku.eecs.mack.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Learning extends AppCompatActivity {
    private List<HiraganaItem> allHiragana, remainingHiragana;
    private HiraganaItem currentHiragana;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning);

        // Initialize the ordered list of all of our hiragana characters
        createHiraganaList();
        // Duplicate it and shuffle it to create our working list
        remainingHiragana = new ArrayList<>(allHiragana);
        Collections.shuffle(remainingHiragana);
        // Start the process of learning
        startLearning();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final int RESULT_CORRECT = 1, RESULT_INCORRECT = 2;

        if (requestCode == 1) {
            if (resultCode == RESULT_CORRECT) {
                // Remove the character and increment the correct in row count
                remainingHiragana.remove(currentHiragana);
                currentHiragana.incrementCorrectInRow();
                // If we still need to answer the copied hiragana correctly again, add it back to the end of the list
                if (currentHiragana.getCorrectInRow() < 2) {
                    remainingHiragana.add(currentHiragana);
                }
                // Select the next character in the list for our next card
                currentHiragana = remainingHiragana.get(0);
            } else if (resultCode == RESULT_INCORRECT) {
                // In this case the next card will be the same character for reinforcement
                currentHiragana.resetCorrectInRow();
            }
            // Request a new card
            newCard();
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