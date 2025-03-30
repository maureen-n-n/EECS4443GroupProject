package ca.yorku.eecs.mack.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Locale;

public class Card extends Activity {
    private final int RESULT_CORRECT = 1, RESULT_INCORRECT = 2;

    private HiraganaItem hiraganaItem;
    private TextView testingCharacter, evaluationText;
    private EditText inputAnswer;
    private TextView timerText;
    private String currentMode;
    private static final long TIMER_DURATION = 30000;
    private static final long TIMER_INTERVAL = 1000; //This is basically the onTick on the timer
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = TIMER_DURATION;
    private CircularProgressIndicator circleProgress;
    private TextView cardCounter;
    private String currentHiragana;
    private String correctRomaji;
    private int cardIndex;



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CardActivity", "Mode received: " + currentMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);

        testingCharacter = findViewById(R.id.hiraganaCharacter);
        inputAnswer = findViewById(R.id.inputAnswer);
        evaluationText = findViewById(R.id.evaluation);
        timerText = findViewById(R.id.timerText);
        circleProgress = findViewById(R.id.circleProgress);
        cardCounter = findViewById(R.id.card_counter);

        currentHiragana = getIntent().getStringExtra("HIRAGANA");
        correctRomaji = getIntent().getStringExtra("ROMAJI");
        currentMode = getIntent().getStringExtra("MODE");
        int currentProgress = getIntent().getIntExtra("CURRENT_PROGRESS", 0);
        int remainingCards = getIntent().getIntExtra("REMAINING_CARDS", 0);
        boolean showRed = getIntent().getBooleanExtra("SHOW_RED", false);

        if (currentHiragana == null || correctRomaji == null) {
            Log.e("CardActivity", "Missing HIRAGANA or ROMAJI extra");
            finish();
            return;
        }

        testingCharacter.setText(currentHiragana);
        evaluationText.setVisibility(View.GONE);
        cardCounter.setText(String.format(Locale.getDefault(), "Correct Cards: %d \nMastery Remaining: %d", currentProgress, remainingCards));
        cardCounter.setTextColor(showRed ? ContextCompat.getColor(this, R.color.progress_red) : ContextCompat.getColor(this, R.color.progress_green));

        setupUI();
        setupTimer();
    }

    private void setupTimer() {
        if (currentMode == null) return;

        switch (currentMode) {
            case "untimed":
                break;
            case "hidden":
            case "emphasized":
                startCountDownTimer();
                break;
        }
    }

    private void setupUI() {
        if (currentMode == null) return;

        switch (currentMode) {
            case "emphasized":
                timerText.setVisibility(View.VISIBLE);
                timerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                timerText.setTextColor(ContextCompat.getColor(this, R.color.timer_red));
                break;
            case "hidden":
                circleProgress.setVisibility(View.VISIBLE);
                circleProgress.setProgress(100);
                timerText.setVisibility(View.GONE);
                break;
            case "untimed":
            default:
                circleProgress.setVisibility(View.GONE);
                timerText.setVisibility(View.GONE);
                break;
        }
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (currentMode.equals("hidden")) {
                    float progress = (millisUntilFinished * 100f) / timeLeftInMillis;
                    circleProgress.setProgress((int) progress);
                } else if (currentMode.equals("emphasized")) {
                    updateTimerDisplay(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                if (currentMode.equals("hidden")) {
                    circleProgress.setProgress(0);
                }
                handleTimerFinish();
            }
        }.start();
    }

    private void updateTimerDisplay(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }

    private void handleTimerFinish() {
        findViewById(R.id.inputAnswer).setEnabled(false);
        findViewById(R.id.button_answer).setEnabled(false);
        findViewById(R.id.dont_know_button).setEnabled(false);

        TextView evaluation = findViewById(R.id.evaluation);
        evaluation.setText(R.string.timeout_message);
        evaluation.setTextColor(ContextCompat.getColor(this, R.color.timer_red));

        new Handler().postDelayed(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("TIMEOUT", true);
            resultIntent.putExtra("HIRAGANA", currentHiragana);
            resultIntent.putExtra("ROMAJI", correctRomaji);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void clickAnswer(View view) {
        String userAnswer = inputAnswer.getText().toString().trim();
        if (userAnswer.isEmpty()) {
            inputAnswer.setError("Answer cannot be blank.");
            return;
        }

        boolean isCorrect = userAnswer.equalsIgnoreCase(correctRomaji);

        evaluationText.setVisibility(View.VISIBLE);
        if (isCorrect) {
            evaluationText.setText("Correct!");
            evaluationText.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            String message = "Incorrect. Correct answer was: " + correctRomaji;
            evaluationText.setText(message);
            evaluationText.setTextColor(ContextCompat.getColor(this, R.color.red));
        }

        disableControls();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("RESULT", isCorrect ? RESULT_CORRECT : RESULT_INCORRECT);
        resultIntent.putExtra("HIRAGANA", currentHiragana);
        resultIntent.putExtra("ROMAJI", correctRomaji);

        new Handler().postDelayed(() -> {
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 2000);
    }

    public void clickDontKnow(View view) {
        evaluationText.setVisibility(View.VISIBLE);
        evaluationText.setText("Try again later!");
        evaluationText.setTextColor(ContextCompat.getColor(this, R.color.red));

        disableControls();

        new Handler().postDelayed(() -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("RESULT", RESULT_INCORRECT);
            resultIntent.putExtra("HIRAGANA", currentHiragana);
            resultIntent.putExtra("ROMAJI", correctRomaji);
            setResult(RESULT_OK, resultIntent);
            finish();
        }, 2000);
    }

    private void updateCounter(int progress, boolean showRed) {
        String text = String.format(Locale.getDefault(), "%d/40", progress);
        cardCounter.setText(text);

        if (showRed) {
            cardCounter.setTextColor(ContextCompat.getColor(this, R.color.progress_red));
            new Handler().postDelayed(() -> {
                cardCounter.setTextColor(ContextCompat.getColor(this, R.color.progress_green));
            }, 1000); // Red for 1 second
        } else {
            cardCounter.setTextColor(ContextCompat.getColor(this, R.color.progress_green));
        }
    }

    private void returnToLearningActivity(boolean answeredCorrectly) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("RESULT", answeredCorrectly ? 1 : 2);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void disableControls() {
        findViewById(R.id.button_answer).setEnabled(false);
        findViewById(R.id.dont_know_button).setEnabled(false);
        inputAnswer.setEnabled(false);
    }



}
