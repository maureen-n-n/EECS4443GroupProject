package ca.yorku.eecs.mack.groupproject;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.IOException;

/**
 * in the first column, five hiragana characters will be shown (か、き、く、け、こ)
 * in the second column, its romanized pronunciation will be shown (ka, ki, ku, ke, ko)
 * in the third column, a button to play a .mp3 file of its pronunciation will be shown
 */

public class K_Intro extends Activity implements OnClickListener {
    private MediaPlayer mediaPlayer;
    private AssetManager assetManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Initialize the activity by setting up buttons and the asset manager
        initialize();
        assetManager = getAssets();
    }

    /**
     * Sets up the buttons and links them to their corresponding audio files.
     * Adds click listeners to each button to trigger audio playback.
     */
    private void initialize() {
        // Find button elements in the layout by their IDs
        Button playButton1 = findViewById(R.id.playButton1);
        Button playButton2 = findViewById(R.id.playButton2);
        Button playButton3 = findViewById(R.id.playButton3);
        Button playButton4 = findViewById(R.id.playButton4);
        Button playButton5 = findViewById(R.id.playButton5);

        // Set click listeners for each button, providing the path to the audio files
        playButton1.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ka.mp3"));
        playButton2.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ki.mp3"));
        playButton3.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ku.mp3"));
        playButton4.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ke.mp3"));
        playButton5.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ko.mp3"));
    }

    private void playSound(String filePath) {
        // Release existing MediaPlayer if it's currently playing
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            // Open the audio file from assets
            AssetFileDescriptor afd = assetManager.openFd(filePath);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            // Prepare and start playback
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Release the MediaPlayer once playback is complete
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });

        } catch (IOException e) {
            e.printStackTrace(); // Log any errors during audio playback
        }
    }

    /**
     * Required override method for the OnClickListener interface.
     * Currently, it is not used but needs to be present for compilation.
     */
    @Override
    public void onClick(View v) {

    }
}
