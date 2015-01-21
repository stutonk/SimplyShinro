package stutonk.simplyshinro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Needs thorough documentation when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class MainActivity extends ActionBarActivity {

    private TextView title_view1, title_view2, play_view, instructions_view, quit_view, beg_view;
    private Typeface robotoThin;
    private static final boolean BEG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robotoThin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");

        title_view1 = (TextView) findViewById(R.id.title_view1);
        title_view1.setTypeface(robotoThin);
        title_view2 = (TextView) findViewById(R.id.title_view2);
        title_view2.setTypeface(robotoThin);

        play_view = (TextView) findViewById(R.id.play_view);
        play_view.setTypeface(robotoThin);
        instructions_view = (TextView) findViewById(R.id.instructions_view);
        instructions_view.setTypeface(robotoThin);

        quit_view = (TextView) findViewById(R.id.quit_view);
        quit_view.setTypeface(robotoThin);

        beg_view = (TextView) findViewById(R.id.beg_view);
        beg_view.setTypeface(robotoThin);
        if (BEG) {
            beg_view.setText(R.string.beg);
        }
        else {
            beg_view.setText(R.string.thank);
        }
    }

    public void launchChooser(View view) {
        Intent intent = new Intent(this, ChooserActivity.class);
        startActivity(intent);
    }

    public void launchInstructions(View view) {
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void exitGame(View view) {
        finish();
    }
}
