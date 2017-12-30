package com.example.etienneguerlain.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;


// This activity launches a new game and handles user interactions with the game engine (a TicTacToe instance)
public class GameActivity extends AppCompatActivity {


    // We keep players (user and CPU) scores. When a game is finished, they are updated accordingly to the game state
    int _playerScore;
    int _cpuScore;

    // Part of the interface that displays the user and CPU scores
    TextView _playerScoreLabel;
    TextView _cpuScoreLabel;

    // Part of the interface displaying the user and CPU names (for now, just "You" and "CPU")
    TextView _playerNameLabel;
    TextView _cpuNameLabel;

    // Game engine instance
    TicTacToe _ticTacToeGame;

    // Grid of buttons, that displays the game grid and allows user to play
    Button[][] _grid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loading of the activity_game.xml layout
        setContentView(R.layout.activity_game);


        // We retrieve the elements of the interface from the loaded layout
        initializeUIElements();

        // For each button of the grid, we create an onClickListener
        bindButtonsAction();


        // We set the scores to 0 (they are reset when user quits the game activity)
        _playerScore = _cpuScore = 0;

        // Creation of a brand new game engine (a new one is created each time user wants to play again)
        _ticTacToeGame = new TicTacToe();


        // We set the background color as defined in the currently selected theme
        setColorTesterColor();

        // We update the labels displaying the scores
        refreshScores();

        // For each cell, this method displays the correct picture (empty cell, cross or circle)
        // and also disables the button if the cell is already taken
        drawGrid();

        // Make the CPU play if in LET hand mode or FAIR
        manageHand();

    }


    // This method retrieve the elements of the interface from the loaded layout
    private void initializeUIElements(){

        // We retrieve the scores and names labels
        _playerScoreLabel = findViewById(R.id.playerScore);
        _cpuScoreLabel = findViewById(R.id.cpuScore);

        _playerNameLabel = findViewById(R.id.playerName);
        _cpuNameLabel = findViewById(R.id.cpuName);


        // We initialize the grid...
        _grid = new Button[3][3];

        // ... and then fill it with buttons from the loaded layout
        _grid[0][0] = findViewById(R.id.cell0_0);
        _grid[0][1] = findViewById(R.id.cell0_1);
        _grid[0][2] = findViewById(R.id.cell0_2);

        _grid[1][0] = findViewById(R.id.cell1_0);
        _grid[1][1] = findViewById(R.id.cell1_1);
        _grid[1][2] = findViewById(R.id.cell1_2);

        _grid[2][0] = findViewById(R.id.cell2_0);
        _grid[2][1] = findViewById(R.id.cell2_1);
        _grid[2][2] = findViewById(R.id.cell2_2);

    }


    // Set an onClickListener on every button of the grid
    private void bindButtonsAction() {

        // For each line of the grid...
        for (int i = 0; i < 3; i++) {
            final int line = i;

            // ... and for each cell of this line...
            for (int j = 0; j < 3; j++) {
                final int column = j;

                // ... we set an OnClickListener
                _grid[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        runActionForCell(line, column);
                    }
                });
            }
        }
    }


    // This method plays on the cell designated by user
    private void runActionForCell(int line, int column){

        // Asks the game engine to play on the designated cell
        _ticTacToeGame.playCell(line, column);


        // Refresh the grid
        drawGrid();

        // Buttons are disabled while CPU is "thinking" to prevent user from playing
        lockButtons();

        // We check if user has won or if it is a tie
        checkGameState();

        // Otherwise, if game state is always "PLAYING", then the CPU has to play
        if(_ticTacToeGame.getState() == TicTacToe.GAME_STATE.PLAYING){

            // By using this runnable, it is possible to delay CPU action
            // It makes the game more natural, since the CPU doesn't play directly after user played
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    makeCpuPlay();
                }
            };

            Handler h = new Handler();
            h.postDelayed(r, 1000);

        }
    }


    // Disables all the buttons of the grid
    private void lockButtons(){

        // For each line of the grid...
        for(int i=0; i<3; i++){

            // ... and for each cell in this line...
            for(int j=0; j<3; j++){

                // ... the cell button is disabled
                _grid[i][j].setEnabled(false);
            }
        }
    }


    // CPU chooses randomly a cell and tries to play it
    private void makeCpuPlayRandom(){

        // Boolean indicating if the last play was valid or not
        boolean validPlay = false;

        // While the last play was invalid...
        do{
            // ... we pick up random line and column numbers...
            Random myRandow = new Random();
            int line = myRandow.nextInt(3);
            int column = myRandow.nextInt(3);

            // ... and ask the game engine to play the corresponding cell
            TicTacToe.PLAY_RESULT result = _ticTacToeGame.playCell(line, column);

            // Then we check the result of this play, to see if we have to try again with other line and column numbers
            validPlay = (result == TicTacToe.PLAY_RESULT.OK);
        } while(!validPlay);


        // Once CPU has played, grid is refreshed
        drawGrid();

        // And the game state is checked to see if CPU won or if it is a tie
        checkGameState();
    }



    // Make the CPU play according to the Mode selected in the Settings instance
    private void makeCpuPlay(){

        // According to the Mode set in the settings...
        switch (Settings.getInstance().getMode()){
            case EASY:
                // ... we play randomly if mode is EASY...
                makeCpuPlayRandom();
                break;
            case NORMAL:
                // ... or we "flip a coin" to play wisely or randomly if mode is "NORMAL"...
                Random myRandom = new Random();
                float myFloat = myRandom.nextFloat();

                if(myFloat < 0.5){
                    makeCpuPlayClever();
                }else{
                    makeCpuPlayRandom();
                }
                break;
            case IMPOSSIBLE:
                // ... or we play wisely if mode is IMPOSSIBLE
                makeCpuPlayClever();
                break;
        }

    }


    // This method makes the CPU play wisely. I didn't implement the logic myself, a friend did it.
    private void makeCpuPlayClever(){
        boolean validPlay = false;

        // As long as the last play was invalid, we go through the same process
        do{
            int line = 99;
            int column = 99;
            int cas = 99;   // Will be 0, 1, 2, 10, 11, 12, 101 or 102
            int letsWin = 0;
            int blockPlayer = 0;


            //  We check if CPU is going to win (by adding cells value. The CPU cell value is 3)

            // we check the lines
            for (int i=0; i<3; i++) {
                if (_ticTacToeGame.getCell(i,0) + _ticTacToeGame.getCell(i,1) + _ticTacToeGame.getCell(i,2) == 6 ) { cas = i;letsWin = 1;}
            }

            // we check the columns
            for (int i=0; i<3; i++) {
                if (_ticTacToeGame.getCell(0,i) + _ticTacToeGame.getCell(1,i) + _ticTacToeGame.getCell(2,i) == 6 ) {cas = 10+i;letsWin = 1;}
            }

            //we check the diagonals
            if (_ticTacToeGame.getCell(0,0) + _ticTacToeGame.getCell(1,1) + _ticTacToeGame.getCell(2,2) == 6 ){ cas = 101;letsWin = 1;}
            if (_ticTacToeGame.getCell(2,0) + _ticTacToeGame.getCell(1,1) + _ticTacToeGame.getCell(0,2) == 6 ){ cas = 102;letsWin = 1;}



            //  If the CPU is not going to win, we check if the user is going to win (by adding cells value. The user cell value is 1)

            if (letsWin == 0){
                // Lines
                for (int i=0; i<3; i++) {
                    if (_ticTacToeGame.getCell(i,0) + _ticTacToeGame.getCell(i,1) + _ticTacToeGame.getCell(i,2) == 2 ) { cas = i; blockPlayer = 1;}
                }

                // Columns
                for (int i=0; i<3; i++) {
                    if (_ticTacToeGame.getCell(0,i) + _ticTacToeGame.getCell(1,i) + _ticTacToeGame.getCell(2,i) == 2 ) {cas = 10+i;blockPlayer = 1;}
                }

                // Diagonals
                if (_ticTacToeGame.getCell(0,0) + _ticTacToeGame.getCell(1,1) + _ticTacToeGame.getCell(2,2) == 2 ){ cas = 101;blockPlayer = 1;}
                if (_ticTacToeGame.getCell(2,0) + _ticTacToeGame.getCell(1,1) + _ticTacToeGame.getCell(0,2) == 2 ){ cas = 102;blockPlayer = 1;}
            }



            // Now we now the situation, according to the cas value
            // We retrieve the coordinates of the cell we have to play
            if (cas == 0 | cas == 1 | cas == 2 ){
                if (_ticTacToeGame.getCell(cas,0) == 0){ line = cas; column = 0  ;    }
                if (_ticTacToeGame.getCell(cas,1) == 0){ line = cas; column = 1  ;    }
                if (_ticTacToeGame.getCell(cas,2) == 0){ line = cas; column = 2  ;    } }

            if (cas == 10 | cas == 11 | cas == 12 ){
                if (_ticTacToeGame.getCell(0,(cas-10)) == 0){ line = 0; column = (cas-10)  ;    }
                if (_ticTacToeGame.getCell(1,(cas-10)) == 0){ line = 1; column = (cas-10)  ;    }
                if (_ticTacToeGame.getCell(2,(cas-10)) == 0){ line = 2; column = (cas-10)  ;    } }

            if (cas == 101 ){
             if (_ticTacToeGame.getCell(0,0) == 0){ line = 0; column = 0  ;    }
             if (_ticTacToeGame.getCell(1,1) == 0){ line = 1; column = 1  ;    }
             if (_ticTacToeGame.getCell(2,2) == 0){ line = 2; column = 2  ;    }   }

            if (cas == 102 ){
             if (_ticTacToeGame.getCell(2,0) == 0){ line = 2; column = 0  ;    }
             if (_ticTacToeGame.getCell(1,1) == 0){ line = 1; column = 1  ;    }
             if (_ticTacToeGame.getCell(0,2) == 0){ line = 0; column = 2  ;    }   }

            // We want to play in the corners in priority
            if (line == 99 && column == 99 ) {
                if (_ticTacToeGame.getCell(0,0) == 1){ line = 2; column = 2  ; }
                if (_ticTacToeGame.getCell(2,0) == 1){ line = 0; column = 2  ; }
                if (_ticTacToeGame.getCell(0,2) == 1){ line = 2; column = 0  ; }
                if (_ticTacToeGame.getCell(2,2) == 1){ line = 0; column = 0  ; }

                // If no corner is available, we play somewhere else
                if (_ticTacToeGame.getCell(1,0) == 0){ line = 1; column = 0  ; }
                if (_ticTacToeGame.getCell(0,1) == 0){ line = 0; column = 1  ; }
                if (_ticTacToeGame.getCell(1,2) == 0){ line = 1; column = 2  ; }
                if (_ticTacToeGame.getCell(2,1) == 0){ line = 2; column = 1  ; }
                if (_ticTacToeGame.getCell(1,1) == 0){ line = 1; column = 1  ; }
            }

            // We ask the game engine to play the designated cell...
            TicTacToe.PLAY_RESULT result = _ticTacToeGame.playCell(line, column);

            // ...and then check if it was a valid play
            validPlay = (result == TicTacToe.PLAY_RESULT.OK);

        } while(!validPlay);


        // Once CPU has played, the grid is refreshed...
        drawGrid();

        // ... and the game state is checked to know is CPU has won or if there is a tie
        checkGameState();
    }



    // If the game is finished, this method displays the result and asks user what to do next
    private void checkGameState(){

        // If the game has ended
        if(_ticTacToeGame.getState() != TicTacToe.GAME_STATE.PLAYING){

            // All buttons of the grid are disabled
            lockButtons();

            // We build an alert dialog to announce victory/defeat/tie and to ask if user wants to play again or leave
            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this).setCancelable(false);

            // We define the title of the alert dialog (victory, defeat, tie) with ternary operations
            String title = (_ticTacToeGame.getState() == TicTacToe.GAME_STATE.A_WON) ? "Victory!" : (_ticTacToeGame.getState() == TicTacToe.GAME_STATE.B_WON) ? "Defeat." : "It's a tie";

            // We set the title and the message of the alert dialog
            builder.setMessage("Would you like to play again?")
                    .setTitle(title);


            // We set the positive button of the dialog. By clicking on "Play", user will launch a new game
            builder.setPositiveButton("Play", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newGame();
                }
            });

            // Otherwise, by clicking on "Leave", the activity is terminated, hence the home menu activity is dislayed again
            builder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            // We build the dialog and display it
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    // This method launches a new game instance (by initializing a new TicTacToe instance)
    private void newGame(){

        // If user won, its score is incremented
        _playerScore += (_ticTacToeGame.getState() == TicTacToe.GAME_STATE.A_WON) ? 1 : 0;

        // If CPU won, its score is incremented
        _cpuScore += (_ticTacToeGame.getState() == TicTacToe.GAME_STATE.B_WON) ? 1 : 0;


        // The scores labels are refreshed (their text is set to the current values of _playerScore and _cpuScore)
        refreshScores();

        // A new instance of the game engine is created
        _ticTacToeGame = new TicTacToe();

        // The grid is redrawn
        drawGrid();

        // And we look who plays first (same logic than in the onCreate function,
        // when launching the game for the first time when the activity is created)
        manageHand();
    }


    // Set the background color according to the Theme defined in the Settings instance
    private void setColorTesterColor(){

        // We retrieve the selected theme in the Settings instance
        Settings.THEMES currentTheme = Settings.getInstance().getTheme();
        int parsedColor = Color.parseColor("#ffffff");

        // Depending on this theme...
        switch (currentTheme) {
            case CHRISTMAS:
                // ... we set the color to apply to the color defined in app/res/values/colors.xml
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

        // We retrieve the background view...
        View colorTester = findViewById(R.id.colorTester);

        // ... and apply the color to it
        colorTester.setBackgroundColor(parsedColor);
    }


    // This method updates the text of the score labels
    private void refreshScores(){

        // We refresh the values displayed
        // We have to cast the scores to String format, with "" + score
        _playerScoreLabel.setText("" + _playerScore);
        _cpuScoreLabel.setText("" + _cpuScore);

        // We also need to give the labels text the correct color of the theme
        int labelsColor = getResources().getColor(R.color.ticTacToe);

        // That why we retrieve the theme from the Settings instance,...
        switch (Settings.getInstance().getTheme()){
            case CHRISTMAS:
                // ... retrieve the color from the colors.xml file...
                labelsColor = getResources().getColor(R.color.christmasText);
                break;
            case EASTER:
                labelsColor = getResources().getColor(R.color.easterText);
                break;
            case NIGHT:
                labelsColor = getResources().getColor(R.color.nightText);
                break;
            case BEACH:
                labelsColor = getResources().getColor(R.color.beachText);
                break;
        }

        // ... and apply the color to all the labels text
        _playerScoreLabel.setTextColor(labelsColor);
        _cpuScoreLabel.setTextColor(labelsColor);
        _playerNameLabel.setTextColor(labelsColor);
        _cpuNameLabel.setTextColor(labelsColor);
    }


    // This method draws the grid
    // It displays the correct picture for each cell, according to the state of the grid of the game engine
    // And it also disable all the cells button where the cells are already taken
    private void drawGrid(){

        // First, we have to get the correct pictures for empty cell, played by user and played by CPU
        int emptySymbol = R.drawable.empty;
        int crossSymbol = R.drawable.cross;
        int circleSymbol = R.drawable.circle;

        // We retrieve the theme from the Settings instance...
        switch (Settings.getInstance().getTheme()){
            case TICTACTOE:
                // ... and then set the pictures to draw to the correct ones, defined in app/res/drawable
                emptySymbol = R.drawable.empty;
                crossSymbol = R.drawable.cross;
                circleSymbol = R.drawable.circle;
                break;
            case CHRISTMAS:
                emptySymbol = R.drawable.christmas_empty;
                crossSymbol = R.drawable.christmas_cross;
                // Here is an easter egg. With christmas theme in easy mode, a troll replaces the circle symbol
                circleSymbol = (Settings.getInstance().getMode() == Settings.MODES.EASY) ? R.drawable.troll : R.drawable.christmas_circle;
                break;
            case NIGHT:
                emptySymbol = R.drawable.night_empty;
                circleSymbol= R.drawable.night_circle;
                crossSymbol = R.drawable.night_cross;
                break;
            case EASTER:
                emptySymbol = R.drawable.empty;
                circleSymbol = R.drawable.easter_circle;
                crossSymbol = R.drawable.easter_cross;
                break;
            case BEACH:
                emptySymbol = R.drawable.empty;
                circleSymbol = R.drawable.beach_circle;
                crossSymbol = R.drawable.beach_cross;
                break;
        }

        // Once the three kinds of picture have been defined, the grid can be drawn
        // For each line of the grid...
        for(int i=0; i<3; i++){

            // ... and for each cell in the line...
            for(int j=0; j<3; j++){

                // By default, we draw an empty cell
                _grid[i][j].setText("");
                _grid[i][j].setBackgroundResource(emptySymbol);

                // But if the cell is not empty...
                if(_ticTacToeGame.getCell(i,j) != 0){

                    // ... we disable its button (to make impossible to play it)
                    _grid[i][j].setEnabled(false);

                    // if the user played it...
                    if(_ticTacToeGame.getCell(i,j) == 1){

                        // ... the cross symbol is displayed
                        _grid[i][j].setBackgroundResource(crossSymbol);
                    }else{

                        // ... else the CPU played it, hence the circle symbol is displayed
                        _grid[i][j].setBackgroundResource(circleSymbol);
                    }
                }else{

                    // The cell is empty, hence its button is enabled. User can play it.
                    _grid[i][j].setEnabled(true);
                }
            }
        }
    }


    // At the beginning of a new game, CPU may play first
    // This method deals with it
    private void manageHand(){

        // If it is said in the settings that CPU must play first (HAND set to "LET")...
        if(Settings.getInstance().getHand() == Settings.HAND.LET){

            // ... cells buttons are disabled to prevent user to play
            lockButtons();

            // we tells the game engine that the current player is the CPU
            _ticTacToeGame.setCurrentPlayer(TicTacToe.PLAYERS.B);

            // we use a runnable to make the CPU play with some delay
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    makeCpuPlay();
                }
            };

            Handler h = new Handler();
            h.postDelayed(r, 1000);


        }

        // If the HAND is set to "FAIR" in the settings instance, then CPU has one chance out of two to play first
        else if(Settings.getInstance().getHand() == Settings.HAND.FAIR){

            // We "flip a coin"...
            Random myRandom = new Random();
            float myFloat = myRandom.nextFloat();

            if(myFloat < 0.5){

                // ... if the coin tells the CPU plays first, buttons are locked to prevent user from playing
                lockButtons();

                // we tells the game engine that CPU plays first
                _ticTacToeGame.setCurrentPlayer(TicTacToe.PLAYERS.B);


                // And make the CPU play with some delay
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        makeCpuPlay();
                    }
                };

                Handler h = new Handler();
                h.postDelayed(r, 1000);

            }
        }
    }
}
