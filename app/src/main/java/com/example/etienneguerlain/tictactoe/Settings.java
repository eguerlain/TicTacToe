package com.example.etienneguerlain.tictactoe;

// This class handles the management of the app settings
// It is accessible from any activity via a singleton pattern
public class Settings {


    // Enumeration of themes user can choose from
    public enum THEMES {
        TICTACTOE, CHRISTMAS, EASTER, BEACH, NIGHT
    }

    // Difficulty levels
    public enum MODES{
        EASY,   // CPU plays randomly on the grid
        NORMAL, // CPU has a one in two chance of playing wisely, and plays randomly otherwise
        IMPOSSIBLE  // CPU always plays wisely (Impossible to beat)
    }

    // Tells who plays first
    public enum HAND{
        LET,    // CPU plays first
        FAIR,   // 50/50
        START   // User plays first
    }

    private THEMES _theme;
    private MODES _mode;
    private HAND _hand;


    // Singleton pattern, allowing access from any activity to a unique instance of Settings
    public static final Settings instanceOfSettings = new Settings();

    public static Settings getInstance(){
        return instanceOfSettings;
    }


    // Private constructor of Settings class, initializing it with default values
    private Settings(){
        _theme = THEMES.TICTACTOE;
        _mode = MODES.NORMAL;
        _hand = HAND.FAIR;
    }

    // Getters and setters
    public THEMES getTheme() {
        return _theme;
    }

    public void setTheme(THEMES newTheme){
        _theme = newTheme;
    }

    public MODES getMode(){
        return _mode;
    }

    public void setMode(MODES newMode){
        _mode = newMode;
    }

    public HAND getHand(){
        return _hand;
    }

    public void setHand(HAND newHand){
        _hand = newHand;
    }
}