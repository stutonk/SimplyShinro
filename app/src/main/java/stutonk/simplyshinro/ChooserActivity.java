package stutonk.simplyshinro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Needs thorough documentation when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class ChooserActivity extends ActionBarActivity
        implements ExpandableListView.OnChildClickListener{

    private Typeface robotoThin;
    private Button chooseReturn;
    private TextView chooseText;
    private ExtendedExpandableListAdapter adapter;
    private ArrayList<ExpandableListGroup> groups;
    private ExpandableListView puzzleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        robotoThin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");

        chooseReturn = (Button) findViewById(R.id.choose_return);
        chooseReturn.setTypeface(robotoThin);

        chooseText = (TextView) findViewById(R.id.choose_text);
        chooseText.setTypeface(robotoThin);

        puzzleList = (ExpandableListView) findViewById(R.id.puzzle_list);

        //Open packs here
        groups = new ArrayList<>();

        //pack 0
        InputStream inputStream = getResources().openRawResource(R.raw.packdefaultpack);
        Scanner scanner = new Scanner(inputStream);

        ExpandableListGroup pack0 = new ExpandableListGroup();
        pack0.setName(scanner.nextLine());
        pack0.setResourceId(R.raw.packdefaultpack);
        scanner.nextLine(); //burn numPuzzles

        groups.add(pack0);
        ArrayList<ExpandableListChild> pack0Puzzles = new ArrayList<>();

        while (scanner.hasNextLine()) {
            ExpandableListChild child = new ExpandableListChild();
            String string = scanner.nextLine();

            child.setName(string);

            //extract puzzle num
            Scanner stringScanner = new Scanner(string);
            stringScanner.next(); //burn "Puzzle#"
            int puzzleNumber = Integer.parseInt(stringScanner.next());
            child.setNumber(puzzleNumber);
            stringScanner.close();

            //get difficulty color
            stringScanner = new Scanner(scanner.nextLine());
            stringScanner.next(); //burn "difficulty:"
            int puzzleDifficulty = Integer.parseInt(stringScanner.next());
            child.setDifficulty(puzzleDifficulty);
            child.setDifficultyColor(Puzzle.getDifficultyColor(puzzleDifficulty));
            stringScanner.close();

            scanner.nextLine(); //burn the puzzle

            pack0Puzzles.add(child);
        }

        groups.get(0).setChildren(pack0Puzzles);

        //populate the list
        scanner.close();

        adapter = new ExtendedExpandableListAdapter(this, groups);
        puzzleList.setAdapter(adapter);
        puzzleList.setOnChildClickListener(this);
    }

    public void returnToMain(View view) {
        finish();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        Intent intent = new Intent(this, GameActivity.class);

        ExpandableListGroup group = (ExpandableListGroup)
                parent.getExpandableListAdapter().getGroup(groupPosition);

        ExpandableListChild child = (ExpandableListChild)
                parent.getExpandableListAdapter().getChild(groupPosition, childPosition);

        intent.putExtra("resource", group.getResourceId());
        intent.putExtra("puzzle", child.getNumber());

        startActivity(intent);

        return true;
    }
}
