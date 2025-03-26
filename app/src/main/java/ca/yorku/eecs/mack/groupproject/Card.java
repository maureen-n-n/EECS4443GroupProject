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
        evaluation = findViewById(R.id.evaluation);
        evaluation.setText("Try Again Later");
        disableControls();

        new android.os.Handler().postDelayed(() -> {
            setResult(RESULT_INCORRECT);
            finish();
        }, 2000);
    }

    private void disableControls() {
        findViewById(R.id.button_answer).setEnabled(false);
        findViewById(R.id.dont_know_button).setEnabled(false);
        inputAnswer.setEnabled(false);
    }

}
