package stutonk.simplyshinro;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.util.Scanner;

import shinro.ShinroPuzzle;

/**
 * Needs thorough documentation when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class GameActivity extends ActionBarActivity {

    private int packResourceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //load the thePuzzle from ChooserActivity
        packResourceId = getIntent().getIntExtra("resource", 0);
        int puzzleNumber = getIntent().getIntExtra("puzzle", 0);

        //create game view
        destroyGameView();
        createGameView(getPuzzleFromResource(packResourceId, puzzleNumber));
    }

    private Puzzle getPuzzleFromResource(int resource, int puzzleNumber) {
        Puzzle thePuzzle = new Puzzle();

        //set resource and number
        thePuzzle.setResource(resource);
        thePuzzle.setNumber(puzzleNumber);

        InputStream inputStream = getResources().openRawResource(resource);
        Scanner scanner = new Scanner(inputStream);
        scanner.nextLine(); //burn packName
        String numPuzzlesString = scanner.nextLine(); //burn numPuzzles

        //get numPuzzles
        Scanner stringScanner = new Scanner(numPuzzlesString);
        stringScanner.next(); //burn "numPuzzles:"
        thePuzzle.setResourceSize(Integer.parseInt(stringScanner.next()));
        stringScanner.close();

        //skip irrelevant puzzles
        for (int i = 1; i < thePuzzle.getNumber(); i++) {
            scanner.nextLine(); //burn puzzleNum
            scanner.nextLine(); //burn difficulty
            scanner.nextLine(); //burn thePuzzle;
        }

        //get name
        thePuzzle.setName(scanner.nextLine());
        String difficultyString = scanner.nextLine();
        String puzzleString = scanner.nextLine();
        scanner.close();

        //get difficulty
        scanner = new Scanner(difficultyString);
        scanner.next(); //burn "difficulty"
        thePuzzle.setDifficulty(Integer.parseInt(scanner.next()));
        scanner.close();

        //get raw thePuzzle
        scanner = new Scanner(puzzleString);
        int[] intArray = new int[ShinroPuzzle.SIZE * ShinroPuzzle.SIZE];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = Integer.parseInt(scanner.next());
        }
        thePuzzle.setShinroPuzzle(intArray);

        return thePuzzle;
    }

    private void createGameView(Puzzle puzzle) {
        GameView gameView = new GameView(this, puzzle);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_layout);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.addView(gameView, params);
    }

    private void destroyGameView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.game_layout);
        if (relativeLayout.getChildCount() > 0) {
            relativeLayout.removeAllViews();
        }
    }

    public void incrementPuzzle(Puzzle puzzle) {
        //update chooser

        if (puzzle.getNumber() + 1 > puzzle.getResourceSize()) {
            finish();
        }
        else {
            Puzzle nextPuzzle = getPuzzleFromResource(puzzle.getResource(), puzzle.getNumber() + 1);
            destroyGameView();
            createGameView(nextPuzzle);
        }
    }
}
