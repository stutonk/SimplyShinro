package stutonk.simplyshinro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView title_view1, title_view2, play_view, instructions_view, quit_view, beg_view;
    Typeface robotoThin;

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


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
