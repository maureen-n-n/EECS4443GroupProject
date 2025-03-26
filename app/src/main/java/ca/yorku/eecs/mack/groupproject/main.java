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

    public void clickCardTest(View view) {
        Intent intent = new Intent(this, Card.class);
        startActivity(intent);
        finish();
    }
}
