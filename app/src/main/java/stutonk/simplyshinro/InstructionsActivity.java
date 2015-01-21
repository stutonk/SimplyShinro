package stutonk.simplyshinro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;


public class InstructionsActivity extends ActionBarActivity {

    TextView detailedInstructionsText, instructionsReturn;
    Typeface robotoThin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        robotoThin = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");

        detailedInstructionsText = (TextView) findViewById(R.id.detailed_instructions_text);
        detailedInstructionsText.setTypeface(robotoThin);

        instructionsReturn = (TextView) findViewById(R.id.instructions_return);
        instructionsReturn.setTypeface(robotoThin);
    }

    public void returnToMain(View v) {
        finish();
    }

}
