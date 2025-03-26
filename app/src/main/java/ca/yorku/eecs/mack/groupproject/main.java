package ca.yorku.eecs.mack.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class main extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    public void init() {

    }
    public void clickVowelIntro(View view) {
        startActivity(new Intent(this, Vowel_Intro.class));
    }

    public void clickKIntro(View view) {
        startActivity(new Intent(this, K_Intro.class));
    }

    public void clickMIntro(View view) {
        startActivity(new Intent(this, M_Intro.class));
    }

    public void clickRIntro(View view) {
        startActivity(new Intent(this, R_Intro.class));
    }

    public void clickCardTest(View view) {
        Intent intent = new Intent(this, Learning.class);
        startActivity(intent);
        finish();
    }
}
