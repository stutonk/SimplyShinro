package stutonk.simplyshinro;

import android.graphics.Color;

import shinro.ShinroPuzzle;

/**
 * Needs thorough documentation when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class Puzzle {
    private int resource;
    private int resourceSize;
    private int number;
    private String name;
    private int difficulty;
    private ShinroPuzzle shinroPuzzle;

    public Puzzle() {
        resource = 0;
        resourceSize = 0;
        number = 0;
        name = "";
        difficulty = 0;
        shinroPuzzle = new ShinroPuzzle();
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getResourceSize() {
        return resourceSize;
    }

    public void setResourceSize(int resourceSize) {
        this.resourceSize = resourceSize;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public ShinroPuzzle getShinroPuzzle() {
        return shinroPuzzle;
    }

    public void setShinroPuzzle(int[] puzzleInts) {
        int[][] matrix = new int[ShinroPuzzle.SIZE][ShinroPuzzle.SIZE];
        int puzzleCount = 0;

        for (int row = 0; row < ShinroPuzzle.SIZE; row++) {
            for (int col = 0; col < ShinroPuzzle.SIZE; col++) {
                matrix[row][col] = puzzleInts[puzzleCount++];
            }
        }
        shinroPuzzle = new ShinroPuzzle(matrix);
    }

    //difficulty should be from 0 to 100
    public static int getDifficultyColor(int difficulty) {
        int red = 0xAA;
        int green = 0xAA;
        int blue = 0xBB;

        red = (int)(red - (((100f - difficulty) / 100f) * red));
        green = (int)(green - ((difficulty / 100f) * green));
        blue = (int)(blue - ((difficulty / 100f) * blue));

        return Color.rgb(red, green, blue);
    }
}
