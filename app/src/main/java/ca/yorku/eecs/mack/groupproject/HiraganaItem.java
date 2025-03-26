package ca.yorku.eecs.mack.groupproject;

import java.io.Serializable;

public class HiraganaItem implements Serializable {
    private String hiragana, romaji;
    private int correctInRow;

    public HiraganaItem(String hiragana, String romaji) {
        this.hiragana = hiragana;
        this.romaji = romaji;
        this.correctInRow = 0;
    }

    public String getHiragana() {
        return hiragana;
    }

    public String getRomaji() {
        return romaji;
    }

    public int getCorrectInRow() {
        return correctInRow;
    }

    public void incrementCorrectInRow() {
        this.correctInRow++;
    }

    public void resetCorrectInRow() { this.correctInRow = 0; }
}
