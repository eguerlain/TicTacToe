package com.example.etienneguerlain.tictactoe;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    View colorBackground;

    ImageView crossCursor;  // Shows the user the currently selected theme

    Button playButton;
    Button settingsButton;
    Button leaveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loading of the menu layout
        setContentView(R.layout.activity_menu);

        // Retrieving of elements from the loaded layout
        colorBackground = findViewById(R.id.colorBackground);
        crossCursor = findViewById(R.id.crossCursor);   // Illustration of the current theme

        // Initialization of the buttons
        setButtons();
    }


    // Method called when activity comes back again, after displaying another activity
    // (e.g.: User has changed settings in the settings activity, hence we refresh this menu activity
    // to display the correct background color and cross cursor illustration according to the settings)
    @Override
    protected void onStart() {
        super.onStart();
        setBackgroundColor();   // Refresh background color
        setCrossCursor();       // Refresh the currently selected theme illustration
    }

    private void setBackgroundColor(){

        // Retrieving the current theme from the settings singleton
        Settings.THEMES currentTheme = Settings.getInstance().getTheme();
        int parsedColor = Color.parseColor("#ffffff");

        // Depending on the theme, we define the color that will be applied to the background
        // (Colors are stored in app/res/values/colors.xml)
        switch (currentTheme) {
            case CHRISTMAS:
                parsedColor = getResources().getColor(R.color.christmasBackground);
                break;
            case EASTER:
                parsedColor = getResources().getColor(R.color.easterBackground);
                break;
            case BEACH:
                parsedColor = getResources().getColor(R.color.beachBackground);
                break;
            case NIGHT:
                parsedColor = getResources().getColor(R.color.nightBackground);
                break;
            default:
                parsedColor = Color.parseColor("#ffffff");

        }

        // Applying the color to the background view
        colorBackground.setBackgroundColor(parsedColor);
    }



    // Depending on the current theme, we set the correct illustration picture
    // (Same logic as in setBackgroundColor)
    private void setCrossCursor(){

        Settings.THEMES currentTheme = Settings.getInstance().getTheme();
        int cursorImage = R.drawable.cross;

        switch (currentTheme) {
            case CHRISTMAS:
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

        crossCursor.setImageResource(cursorImage);
    }

    // This method retrieves buttons from the loaded layout (activity_menu.xml)
    // and defines onClickListeners on them
    private void setButtons(){
        playButton = findViewById(R.id.playButton);
        settingsButton = findViewById(R.id.settingsButton);
        leaveButton = findViewById(R.id.leaveButton);

        setButtonsListeners();
    }

    // For each buttons, we set onClickListener to run action when user clicks on a button
    private void setButtonsListeners(){

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We launch the Game Activity
                Intent IntentToGameActivity = new Intent(MenuActivity.this, GameActivity.class);
                MenuActivity.this.startActivity(IntentToGameActivity);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We launch the Settings Activity
                Intent IntentToSettingsActivity = new Intent(MenuActivity.this, SettingsActivity.class);
                MenuActivity.this.startActivity(IntentToSettingsActivity);
            }
        });

        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We finish this actity
                // Since it is the first on the pile of activities, the application is closed
                finish();
            }
        });
    }
}
