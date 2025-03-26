package ca.yorku.eecs.mack.groupproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Card extends Activity {
    private final int RESULT_CORRECT = 1, RESULT_INCORRECT = 2;

    private HiraganaItem hiraganaItem;
    private TextView testingCharacter, evaluation;
    private EditText inputAnswer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);

        hiraganaItem = (HiraganaItem) getIntent().getSerializableExtra("hiraganaItem");

        testingCharacter = findViewById(R.id.hiraganaCharacter);
        inputAnswer = findViewById(R.id.inputAnswer);

        testingCharacter.setText(hiraganaItem.getHiragana());
    }

    public void clickAnswer(View view) {
        String answer = inputAnswer.getText().toString().trim();
        // Make sure answer field isn't empty
        if (answer.isEmpty()) {
            inputAnswer.setError("Answer cannot be blank.");
        } else {
            if (answer.equals(hiraganaItem.getRomaji())) {
                setResult(RESULT_CORRECT);
                finish();
            } else {
                setResult(RESULT_INCORRECT);
                finish();
            }
        }
    }

    public void clickDontKnow(View view) {
        setResult(RESULT_INCORRECT);
        finish();
    }

}
