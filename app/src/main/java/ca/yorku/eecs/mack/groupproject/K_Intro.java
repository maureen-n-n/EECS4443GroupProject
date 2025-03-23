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
 * in the first column, five hiragana characters will be shown
 * in the second column, its romanized pronunciation will be shown
 * in the third column, a button to play a .wav file of its pronunciation will be shown
 */

public class K_Intro extends Activity implements OnClickListener {

    private MediaPlayer mediaPlayer;
    private AssetManager assetManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();

        assetManager = getAssets();

        Button playButton1 = findViewById(R.id.playButton1);
        Button playButton2 = findViewById(R.id.playButton2);
        Button playButton3 = findViewById(R.id.playButton3);
        Button playButton4 = findViewById(R.id.playButton4);
        Button playButton5 = findViewById(R.id.playButton5);

        playButton1.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ka.mp3"));
        playButton2.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ki.mp3"));
        playButton3.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ku.mp3"));
        playButton4.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ke.mp3"));
        playButton5.setOnClickListener(v -> playSound("hiragana_sounds/section_2/kanasound-ko.mp3"));

    }

    private void initialize() {

    }

    private void playSound(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = assetManager.openFd(filePath);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {

    }
}
