package ca.yorku.eecs.mack.groupproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Learning_Complete extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learning_complete);

        Intent intent = getIntent();
        int total = intent.getIntExtra("TOTAL_CARDS", 0);
        int correct = intent.getIntExtra("CORRECT", 0);
        int incorrect = intent.getIntExtra("INCORRECT", 0);
        int timeouts = intent.getIntExtra("TIMEOUTS", 0);
        long durationMillis = intent.getLongExtra("DURATION", 0);

        String duration = formatDuration(durationMillis);
        double accuracy = (correct * 100.0) / (correct + incorrect + timeouts);
        int totalAttempts = correct + incorrect + timeouts;

        TextView statsText = findViewById(R.id.stats_text);

        String stats = String.format(Locale.getDefault(),
                "Session Summary:\n\n" +
                        "Total Cards: %d\n" +
                        "Correct: %d\n" +
                        "Incorrect: %d\n" +
                        "Timeouts: %d\n" +
                        "Duration: %s\n" +
                        "Accuracy: %.1f%%",
                totalAttempts, correct, incorrect, timeouts, duration, accuracy);

        statsText.setText(stats);
    }

    private String formatDuration(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
