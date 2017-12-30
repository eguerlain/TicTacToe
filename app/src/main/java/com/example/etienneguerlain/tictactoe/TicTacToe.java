package com.example.etienneguerlain.tictactoe;


// This class (referred as "game engine") deals with the Tic Tac Toe game logic
public class TicTacToe {


    // Enumeration of the possible game states
    public enum GAME_STATE {
        PLAYING,    // Game is not finished, someone has to play
        A_WON,      // Player A won
        B_WON,      // Player B won
        TIE         // Grid is full and nobody won
    };


    // Enumeration of the possible results when playing (asking this game engine to play a cell by
    // giving coordinates)

    // This enumeration is not used in the application, since user can't play outside of bounds,
    // and occupied cells can't be played, since the corresponding button is disabled

    // However, if this game engine is used in CLI, it might be useful to deal with those exceptions
    public enum PLAY_RESULT {
        OK, // The "move" was valid (inside the grid boundaries and on an empty cell)
        TAKEN_CELL, // Impossible to play at given coordinates: cell is occupied. Player has to play again
        OUT_OF_BOUNDS   // The provided coordinated don't match with a cell of the grid. Player has to play again
    };


    public enum PLAYERS{
        A, B
    }




    private GAME_STATE _state;

    private PLAYERS _currentPlayer;

    private int[][] _grid;


    // Initializes the game engine
    public TicTacToe(){

        // Grid is empty at first
        _grid = new int[][]{{0,0,0},{0,0,0},{0,0,0}};

        // Player A is the first to play
        _currentPlayer = PLAYERS.A;

        // Of course, the game state is "Playing"
        _state = GAME_STATE.PLAYING;
    }



    // Method that intent to play at the given coordinates (line and column numbers)
    public PLAY_RESULT playCell(int line, int column){

        // Check if the "move" occurs inside the grid boundaries
        // if not, the "OUT_OF_BOUNDS" game state is returned
        if(!playIsInBounds(line, column)){
            return PLAY_RESULT.OUT_OF_BOUNDS;

        }else{

            if(_grid[line][column] == 0){   // if the cell is empty...

                // We look with a ternary operator who is the current player
                // We write inside the cell a 1 for player A and a 3 for player B
                _grid[line][column] = (_currentPlayer == PLAYERS.A) ? 1 : 3;

                // We toggle the current player
                _currentPlayer = (_currentPlayer == PLAYERS.A) ? PLAYERS.B : PLAYERS.A;

                // We check the game state by checking the grid
                checkGrid();

                // The "move" was performed, hence "OK" is the returned game state
                return PLAY_RESULT.OK;


            }else { // The cell is already taken
                return PLAY_RESULT.TAKEN_CELL;
            }
        }
    }



    // Simply checks if the provided coordinates refer to a cell in the grid
    private Boolean playIsInBounds(int line, int column){

        // If line number is between 0 and 2, and column number is between 0 and 2
        // then the "move" is inside grid boundaries
        return (line > -1 && line < 3 && column > -1 && column < 3);
    }


    // Checks the game state by checking the grid
    private void checkGrid(){

        // Each one of the following methods looks if a player won, by aligning marks on a line,
        // a column or a diagonal
        checkLines();
        checkColumns();
        checkDiagonals();

        // if no player won, maybe the grid is full, and therefore the game state needs to be set to "TIE"
        if(_state == GAME_STATE.PLAYING){
            checkGridIsFull();
        }

    }


    // Iterates over the 3 lines of the grid
    private void checkLines(){
        for(int i = 0; i<3; i++){
            checkLine(i);
        }
    }


    // Checks if a player aligned three marks on the given line
    private void checkLine(int line){

        // If there is no empty cell on the line...
        if(_grid[line][0] != 0 && _grid[line][1] != 0 && _grid[line][2] != 0) {

            // ... and if all the marks on this line are the same...
            if (_grid[line][0] == _grid[line][1] && _grid[line][1] == _grid[line][2]) {

                // ... game state is set to the winning player, retrieved from a ternary operation
                // if the mark is 1, then it is the player A that won, else, it is the player B
                _state = (_grid[line][0] == 1) ? GAME_STATE.A_WON : GAME_STATE.B_WON;
            }
        }
    }


    // Iterates over the 3 columns of the grid
    private void checkColumns(){
        for(int i=0; i<3; i++){
            checkColumn(i);
        }
    }


    // Same logic as in checkLine, but for a given column
    private void checkColumn(int column){
        if(_grid[0][column] != 0 && _grid[1][column] != 0 && _grid[2][column] != 0) {
            if (_grid[0][column] == _grid[1][column] && _grid[1][column] == _grid[2][column]) {
                _state = (_grid[0][column] == 1) ? GAME_STATE.A_WON : GAME_STATE.B_WON;
            }
        }
    }



    // Checks if a player managed to align three marks on a diagonal
    private void checkDiagonals(){

        // If there is no empty cell on the North-West to South-Est diagonal...
        if(_grid[0][0] != 0 && _grid[1][1] != 0 && _grid[2][2] != 0) {

            // ... and if all three marks are the same...
            if (_grid[0][0] == _grid[1][1] && _grid[1][1] == _grid[2][2]) {

                // ... game state is set to the player that wrote those marks
                _state = (_grid[0][0] == 1) ? GAME_STATE.A_WON : GAME_STATE.B_WON;
            }
        }

        // Same logic, but for the North-East to South-West diagonal
        if(_grid[0][2] != 0 && _grid[1][1] != 0 && _grid[2][0] != 0) {
            if (_grid[0][2] == _grid[1][1] && _grid[1][1] == _grid[2][0]) {
                _state = (_grid[0][2] == 1) ? GAME_STATE.A_WON : GAME_STATE.B_WON;
            }
        }
    }



    // Checks if the grid is full
    public void checkGridIsFull(){

        // For each line of the grid...
        for(int i = 0; i<3; i++){

            // ... and for each cell of this line...
            for(int j = 0; j<3; j++){

                // ... if the cell is empty, we return (the grid is not full)
                if(_grid[i][j] == 0){
                    return;
                }
            }
        }

        // If we ran through all the cells without finding one empty cell, then the game state has to be set to "TIE"
        _state = GAME_STATE.TIE;
    }


    // Getters and setters
    public int[][]getGrid(){
        return _grid.clone();
    }


    public int getCell(int line, int column){
        return _grid[line][column];
    }



    public PLAYERS getCurrentPlayer(){
        return _currentPlayer;
    }

    // Useful to set who plays first
    public void setCurrentPlayer(PLAYERS player){
        _currentPlayer = player;
    }



    public GAME_STATE getState() {
        return _state;
    }


}
