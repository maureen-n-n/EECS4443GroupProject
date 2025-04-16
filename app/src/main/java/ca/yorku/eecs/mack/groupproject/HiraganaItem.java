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

    public HiraganaItem(String hiragana, String romaji, int correctInRow) {
        this.hiragana = hiragana;
        this.romaji = romaji;
        this.correctInRow = correctInRow;
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

    public void resetCorrectInRow() { this.correctInRow = 0; }

    // Creates a deep copy
    public HiraganaItem duplicate() {
        return new HiraganaItem(this.hiragana, this.romaji, this.correctInRow);
    }
    public void decrementCorrectInRow() {
        this.correctInRow = Math.max(0, this.correctInRow - 1);
    }
    public void incrementCorrectInRow() {
        if (correctInRow < 2) {
            correctInRow++;
        }
    }
}
