package stutonk.simplyshinro;

/**
 * Needs thorough documentation when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * <p>
 * * Adapted from the tutorial at
 * http://www.dreamincode.net/forums/topic/270612-how-to-get-started-with-expandablelistview/
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class ExpandableListChild {
    private String name;
    private int number;
    private int difficulty;
    private int difficultyColor;
    private boolean completed;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficultyColor() {
        return difficultyColor;
    }

    public void setDifficultyColor(int difficultyColor) {
        this.difficultyColor = difficultyColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
