package ca.yorku.eecs.mack.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ModeSelection extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selection);
    }

    public void onModeSelected(View view) {
        String mode = view.getTag().toString();
        Intent intent = new Intent(this, Learning.class);
        intent.putExtra("MODE", mode);
        startActivity(intent);
        finish();
    }
}
