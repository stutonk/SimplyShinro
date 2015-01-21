package stutonk.simplyshinro;

/**
 * Created by Joseph Eib on 1/19/15.
 * <p>
 * Adapted from the tutorial at
 * http://www.dreamincode.net/forums/topic/270612-how-to-get-started-with-expandablelistview/
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
