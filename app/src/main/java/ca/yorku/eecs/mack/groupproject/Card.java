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
    private TextView testingCharacter, evaluation;
    private EditText inputAnswer;
    private TextView timerText;
    private String currentMode;
    private static final long TIMER_DURATION = 60000;
    private static final long TIMER_INTERVAL = 1000; //This is basically the onTick on the timer
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = TIMER_DURATION;
    private CircularProgressIndicator circleProgress;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("CardActivity", "Mode received: " + currentMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);

        timerText = findViewById(R.id.timerText);
        if(timerText == null) {
            Log.e("CardActivity", "Timer text view not found!");
        }
        currentMode = getIntent().getStringExtra("MODE");

        setupTimer();
        setupUI();

        hiraganaItem = (HiraganaItem) getIntent().getSerializableExtra("hiraganaItem");

        testingCharacter = findViewById(R.id.hiraganaCharacter);
        inputAnswer = findViewById(R.id.inputAnswer);

        testingCharacter.setText(hiraganaItem.getHiragana());
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
        circleProgress = findViewById(R.id.circleProgress);
        TextView timerText = findViewById(R.id.timerText);

        if (currentMode != null) {
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        String answer = inputAnswer.getText().toString().trim();
        evaluation = findViewById(R.id.evaluation);

        if (answer.isEmpty()) {
            inputAnswer.setError("Answer cannot be blank.");
            return;
        }

        int resultCode;
        if (answer.equals(hiraganaItem.getRomaji())) {
            evaluation.setText("Correct!");
            resultCode = RESULT_CORRECT;
        } else {
            evaluation.setText("Incorrect" );
            resultCode = RESULT_INCORRECT;
        }

        disableControls();

        new android.os.Handler().postDelayed(() -> {
            setResult(resultCode);
            finish();
        }, 2000);
    }

    public void clickDontKnow(View view) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        evaluation = findViewById(R.id.evaluation);
        evaluation.setText("Try Again Later");
        disableControls();

        new android.os.Handler().postDelayed(() -> {
            setResult(RESULT_INCORRECT);
            finish();
        }, 2000);
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
