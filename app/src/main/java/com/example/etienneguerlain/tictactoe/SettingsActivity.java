package com.example.etienneguerlain.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


// This activity displays the settings with radio button lists
public class SettingsActivity extends AppCompatActivity {


    RadioGroup _themesRadioGroup;
    RadioGroup _modeRadioGroup;
    RadioGroup _handsRadioGroup;
    ImageView _crossCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We load the activity_settings.xml layout
        setContentView(R.layout.activity_settings);

        // Retrieving the radio button groups and theme illustration from the loaded layout
        _themesRadioGroup = (RadioGroup) findViewById(R.id.themesRadioGroup);
        _modeRadioGroup = (RadioGroup) findViewById(R.id.modesRadioGroup);
        _handsRadioGroup = (RadioGroup) findViewById(R.id.handRadioGroup);
        _crossCursor = findViewById(R.id.crossCursor);  // Theme illustration

        // Replacing the current theme illustration picture with the one that correctly illustrates
        // the currently selected theme
        setCursorImage();

        // At first, radio button groups are empty. We need to fill them with values taken from Settings enumerations
        populateThemes();
        populateModes();
        populateHands();
    }


    private void populateThemes(){

        // We create an array of radio buttons
        // Its size is the number of values in the Themes enumeration (defined in the Settings class)
        RadioButton[] themesRadioButtons = new RadioButton[Settings.THEMES.values().length];

        // For each value of the enumeration
        for(int i=0; i<themesRadioButtons.length; i++){

            // We retrieve the currently evaluated theme
            final Settings.THEMES currentTheme = Settings.THEMES.values()[i];

            // We create a new radio button (Remind that at first only empty radio button groups exist!)
            themesRadioButtons[i] = new RadioButton(this);

            // We write the name of the theme
            themesRadioButtons[i].setText(Settings.THEMES.values()[i].toString());

            // We define a unique button id
            themesRadioButtons[i].setId(i + 100);

            // If the currently evaluated theme is also the currently selected
            // we need to check the radio button
            if(Settings.getInstance().getTheme() == Settings.THEMES.values()[i]){
                themesRadioButtons[i].setChecked(true);
            }

            // Eventually, we define the onClickListener of the radio button
            themesRadioButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // We change the selected theme of the Settings singleton
                    Settings.getInstance().setTheme(currentTheme);

                    // We refresh the theme illustration picture
                    setCursorImage();


                }
            });

            // Once the radio button has been created and initialized, we add it to the interface
            _themesRadioGroup.addView(themesRadioButtons[i]);
        }
    }


    // Same logic as in populateThemes here, but for the Modes (difficulty level)
    private void populateModes(){

        RadioButton[] modesRadioButtons = new RadioButton[Settings.MODES.values().length];

        for(int i =0; i<modesRadioButtons.length; i++){
            final Settings.MODES currentMode = Settings.MODES.values()[i];
            modesRadioButtons[i] = new RadioButton(this);
            modesRadioButtons[i].setText(Settings.MODES.values()[i].toString());
            modesRadioButtons[i].setId(i+200);
            if(Settings.getInstance().getMode() == Settings.MODES.values()[i]){
                modesRadioButtons[i].setChecked(true);
            }
            modesRadioButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings.getInstance().setMode(currentMode);
                }
            });
            _modeRadioGroup.addView(modesRadioButtons[i]);
        }
    }

    // Same logic as in populateThemes here, but for the Hands (who plays first)
    private void populateHands(){
        RadioButton[] handsRadioButtons = new RadioButton[Settings.HAND.values().length];

        for(int i = 0; i<handsRadioButtons.length; i++){
            final Settings.HAND currentHand = Settings.HAND.values()[i];
            handsRadioButtons[i] = new RadioButton(this);
            handsRadioButtons[i].setText(Settings.HAND.values()[i].toString());
            handsRadioButtons[i].setId(i+300);
            if(Settings.getInstance().getHand() == Settings.HAND.values()[i]){
                handsRadioButtons[i].setChecked(true);
            }
            handsRadioButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Settings.getInstance().setHand(currentHand);
                }
            });
            _handsRadioGroup.addView(handsRadioButtons[i]);
        }
    }


    // This method refreshes the currently selected theme illustration
    private void setCursorImage(){
        int cursorImage = R.drawable.cross;

        // Depending on the currently selected theme (retrieved from the Settings singleton)...
        switch (Settings.getInstance().getTheme()) {
            case CHRISTMAS:
                // ... we get the correct illustration from app/res/drawable...
                cursorImage = R.drawable.christmas_cross;
                break;
            case EASTER:
                cursorImage = R.drawable.easter_cross;
                break;
            case BEACH:
                cursorImage = R.drawable.beach_cross;
                break;
            case NIGHT:
                cursorImage = R.drawable.night_cross;
                break;
            default:
                cursorImage = R.drawable.cross;

        }

        // ... and set the illustration picture with it
        _crossCursor.setImageResource(cursorImage);
    }
}
