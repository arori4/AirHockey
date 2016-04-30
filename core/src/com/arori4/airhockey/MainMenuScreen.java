package com.arori4.airhockey;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Christopher Cabreros on 29-Apr-16.
 * Defines the main menu screen, including the main menu, settings, and difficulty.
 */
public class MainMenuScreen implements Screen, ActionListener {

    //Engine objects
    private final AirHockeyGame mGame;
    private OrthographicCamera mCamera;
    private final AssetManager mManager;
    private Menu mCurrentMenu;
    private int mCurrentDelay;
    private Vector3 touchPos;

    //Assets
    //menus
    private Menu mMenuMain;
    private Menu mMenuSettings;
    private Menu mMenuDifficulty;
    
    //Fonts
    private BitmapFont menuFont;
    private BitmapFont menuTitleFont;
    //Textures
    private Texture menuBackgroundTexture;
    private Texture menuButtonTexture;
    private Texture menuButtonPressedTexture;
    private Texture settingsButtonTexture;

    //Constants
    private static final int DEFAULT_DELAY = 30;
    //Menu Constants
    public static final int LARGE_BUTTON_FONT_SIZE = 100;
    public static final int LARGE_BUTTON_FONT_BORDER_SIZE = 3;
    public static final int LARGE_BUTTON_WIDTH = 500;
    public static final int LARGE_BUTTON_HEIGHT = 300;
    public static final int LARGE_BUTTON_CENTER = Globals.GAME_WIDTH / 2 - LARGE_BUTTON_WIDTH / 2;

    public static final int MENU_1_PLAYER_BUTTON_Y = Globals.GAME_HEIGHT / 2;
    public static final int MENU_2_PLAYER_BUTTON_Y =
            Globals.GAME_HEIGHT / 2 - LARGE_BUTTON_HEIGHT - 20;
    public static final int MENU_SETTINGS_Y = 20;

    //Difficulty Menu Constants
    public static final int DIFFICULTY_HARD_BUTTON_WIDTH = 650;
    public static final int DIFFICULTY_MEDIUM_BUTTON_WIDTH = 500;
    public static final int DIFFICULTY_EASY_BUTTON_WIDTH = 400;
    public static final int DIFFICULTY_HARD_BUTTON_Y = 20;
    public static final int DIFFICULTY_MEDIUM_BUTTON_Y =
            DIFFICULTY_HARD_BUTTON_Y + LARGE_BUTTON_HEIGHT + 20;
    public static final int DIFFICULTY_EASY_BUTTON_Y =
            DIFFICULTY_MEDIUM_BUTTON_Y + LARGE_BUTTON_HEIGHT + 20;

    //Title Constants
    public static final int TITLE_TEXT_SIZE = 120;
    public static final int TITLE_TEXT_BORDER_WIDTH = 7;
    public static final int TITLE_TEXT_Y = Globals.GAME_HEIGHT - 80;
    public static final int DIFFICULTY_TEXT_Y = Globals.GAME_HEIGHT - 70;
    //Settings constants
    public static final int SETTINGS_CENTER_BOX_WIDTH = 450;
    public static final int SETTINGS_CENTER_BOX_HEIGHT = 200;
    public static final int SETTINGS_SIDE_DIMENSION = 100;
    public static final int SETTINGS_COLOR_Y = 800;
    public static final int SETTINGS_COLOR_EDIT_Y =
            SETTINGS_COLOR_Y + SETTINGS_CENTER_BOX_HEIGHT / 2 - SETTINGS_SIDE_DIMENSION / 2;
    public static final int SETTINGS_TRAILS_Y = 500;
    public static final int SETTINGS_TRAILS_EDIT_Y =
            SETTINGS_TRAILS_Y + SETTINGS_CENTER_BOX_HEIGHT / 2 - SETTINGS_SIDE_DIMENSION / 2;
    public static final int SETTINGS_BACK_Y = 200;
    public static final int SETTINGS_LEFT_X = 50;
    public static final int SETTINGS_RIGHT_X =
            Globals.GAME_HEIGHT - SETTINGS_LEFT_X - SETTINGS_SIDE_DIMENSION;

    //AI Values
    public static final float AI_EASY = 0.45f;
    public static final float AI_MEDIUM = 0.70f;
    public static final float AI_HARD = 0.90f;

    /**
     * Constructor
     * @param game - game to implement in
     * @param manager - asset manager that contains necessary assets
     */
    public MainMenuScreen(final AirHockeyGame game, final AssetManager manager){
        //assign game and manager
        mGame = game;
        mManager = manager;

        //set camera
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, Globals.GAME_WIDTH, Globals.GAME_HEIGHT);

        //menu button images
        menuBackgroundTexture = mManager.get("menuBackground.png", Texture.class);
        menuButtonTexture = mManager.get("menuButton.png", Texture.class);
        settingsButtonTexture = mManager.get("triangle.png", Texture.class);
        menuButtonPressedTexture = mManager.get("menuButtonPressed.png", Texture.class);

        //create all the assets
        setupFonts();
        setupMainMenu();
        setupDifficultyMenu();
        setupSettingsMenu();

        //set up default delay
        mCurrentDelay = DEFAULT_DELAY;

        //start the main screen
        mCurrentMenu = mMenuMain;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        //clear screen
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update and start spriteBatch
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);
        mGame.batch.begin();

        //render the current menu, if it exists
        if (mCurrentMenu != null){
            mCurrentMenu.draw(mGame.batch);
        } else{
            System.err.println("ERROR: mCurrentMenu not assigned");
        }

        //call update loop
        update(delta);
    }


    /**
     * Separate update loop
     * @param delta - time distance. not used
     */
    public void update(float delta){
        //update the current menu
        mCurrentMenu.update();
        //subtract currentDelay
        mCurrentDelay--;

        //check any touch events
        if (Gdx.input.isTouched() && mCurrentDelay < 0) {//process input only if it is touched, and if countdown is lower
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            mCurrentMenu.isPressed(touchPos.x, touchPos.y);
        }
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        menuFont.dispose();
        menuTitleFont.dispose();
    }


    /**
     * Helper method to set up fonts
     */
    private void setupFonts(){
        //setup fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/arialuni.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        //set up menu font
        parameter.size = LARGE_BUTTON_FONT_SIZE;
        parameter.color = com.badlogic.gdx.graphics.Color.WHITE;
        parameter.borderColor = com.badlogic.gdx.graphics.Color.BLACK;
        parameter.borderWidth = LARGE_BUTTON_FONT_BORDER_SIZE;
        menuFont = generator.generateFont(parameter);

        //set the generator for a new font
        generator.dispose();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/impact.ttf"));

        //set up menu title font
        parameter.size = TITLE_TEXT_SIZE;
        parameter.color = Color.SCARLET;
        parameter.borderColor = Color.WHITE;
        parameter.borderWidth = TITLE_TEXT_BORDER_WIDTH;
        menuTitleFont = generator.generateFont(parameter);

        //remove the generator as we don't need it anymore
        generator.dispose();
    }


    /**
     * Sets up the main menu
     */
    private void setupMainMenu(){
        //create main menu
        mMenuMain = new Menu();
        mMenuMain.setTexture(menuBackgroundTexture);

        //Title Text
        TextBox titleText = new TextBox("EXTREME AIR HOCKEY", menuTitleFont);
        titleText.setX(LARGE_BUTTON_CENTER);
        titleText.setY(TITLE_TEXT_Y);
        titleText.setWidth(LARGE_BUTTON_WIDTH);
        titleText.setHeight(LARGE_BUTTON_HEIGHT);
        mMenuMain.addComponent(titleText);

        //1 player button
        Button p1Button = new Button("1 Player", menuFont);
        p1Button.setTexture(menuButtonTexture);
        p1Button.setPressedTexture(menuButtonPressedTexture);
        p1Button.setX(LARGE_BUTTON_CENTER);
        p1Button.setY(MENU_1_PLAYER_BUTTON_Y);
        p1Button.setWidth(LARGE_BUTTON_WIDTH);
        p1Button.setHeight(LARGE_BUTTON_HEIGHT);
        p1Button.setActionCommand("1 Player");
        mMenuMain.addComponent(p1Button);

        //2 player button
        Button p2Button = new Button("2 Player", menuFont);
        p2Button.setTexture(menuButtonTexture);
        p2Button.setPressedTexture(menuButtonPressedTexture);
        p2Button.setX(LARGE_BUTTON_CENTER);
        p2Button.setY(MENU_2_PLAYER_BUTTON_Y);
        p2Button.setWidth(LARGE_BUTTON_WIDTH);
        p2Button.setHeight(LARGE_BUTTON_HEIGHT);
        p2Button.setActionCommand("2 Player");
        mMenuMain.addComponent(p2Button);

        //settings button
        Button settingsButton = new Button("Settings", menuFont);
        settingsButton.setTexture(menuButtonTexture);
        settingsButton.setPressedTexture(menuButtonPressedTexture);
        settingsButton.setX(LARGE_BUTTON_CENTER);
        settingsButton.setY(MENU_SETTINGS_Y);
        settingsButton.setWidth(LARGE_BUTTON_WIDTH);
        settingsButton.setHeight(LARGE_BUTTON_HEIGHT);
        settingsButton.setActionCommand("Go To Settings");
        mMenuMain.addComponent(settingsButton);
    }


    /**
     * Sets up the difficulty menu
     */
    private void setupDifficultyMenu(){
        //create main menu
        mMenuDifficulty = new Menu();
        mMenuDifficulty.setTexture(menuBackgroundTexture);

        //Title Text
        TextBox titleText = new TextBox("DIFFICULTY", menuTitleFont);
        titleText.setWidth(LARGE_BUTTON_WIDTH);
        titleText.setHeight(LARGE_BUTTON_HEIGHT);
        titleText.setX(LARGE_BUTTON_CENTER);
        titleText.setY(DIFFICULTY_TEXT_Y);
        mMenuDifficulty.addComponent(titleText);

        //Wimp button
        Button easyButton = new Button("Wimp", menuFont);
        easyButton.setTexture(menuButtonTexture);
        easyButton.setPressedTexture(menuButtonPressedTexture);
        easyButton.setWidth(DIFFICULTY_EASY_BUTTON_WIDTH);
        easyButton.setHeight(LARGE_BUTTON_HEIGHT);
        easyButton.setX(Globals.GAME_WIDTH / 2 - easyButton.getWidth() / 2);
        easyButton.setY(DIFFICULTY_EASY_BUTTON_Y);
        easyButton.setActionCommand("Easy Difficulty");
        mMenuDifficulty.addComponent(easyButton);

        //Medium button
        Button mediumButton = new Button("Fickle", menuFont);
        mediumButton.setTexture(menuButtonTexture);
        mediumButton.setPressedTexture(menuButtonPressedTexture);
        mediumButton.setWidth(DIFFICULTY_MEDIUM_BUTTON_WIDTH);
        mediumButton.setHeight(LARGE_BUTTON_HEIGHT);
        mediumButton.setX(Globals.GAME_WIDTH / 2 - mediumButton.getWidth() / 2);
        mediumButton.setY(DIFFICULTY_MEDIUM_BUTTON_Y);
        mediumButton.setActionCommand("Medium Difficulty");
        mMenuDifficulty.addComponent(mediumButton);

        //Hard button
        Button hardButton = new Button("HARDCORE", menuFont);
        hardButton.setTexture(menuButtonTexture);
        hardButton.setPressedTexture(menuButtonPressedTexture);
        hardButton.setWidth(DIFFICULTY_HARD_BUTTON_WIDTH);
        hardButton.setHeight(LARGE_BUTTON_HEIGHT);
        hardButton.setX(Globals.GAME_WIDTH / 2 - hardButton.getWidth() / 2);
        hardButton.setY(DIFFICULTY_HARD_BUTTON_Y);
        hardButton.setActionCommand("Hard Difficulty");
        mMenuDifficulty.addComponent(hardButton);
    }


    /**
     * Sets up the settings menu
     */
    private void setupSettingsMenu(){
        //create menu
        mMenuSettings = new Menu();
        mMenuSettings.setTexture(menuBackgroundTexture);

        //color buttons
        TextBox colorTextBox = new TextBox("Color", menuFont);
        colorTextBox.setWidth(SETTINGS_CENTER_BOX_WIDTH);
        colorTextBox.setHeight(SETTINGS_CENTER_BOX_HEIGHT);
        colorTextBox.setX(Globals.GAME_WIDTH /2 - colorTextBox.getWidth() / 2);
        colorTextBox.setY(SETTINGS_COLOR_Y);
        mMenuSettings.addComponent(colorTextBox);

        Button colorLeft = new Button("", menuFont);
        colorLeft.setTexture(settingsButtonTexture);
        colorLeft.setWidth(SETTINGS_SIDE_DIMENSION);
        colorLeft.setHeight(SETTINGS_SIDE_DIMENSION);
        colorLeft.setX(SETTINGS_LEFT_X);
        colorLeft.setY(SETTINGS_COLOR_EDIT_Y);
        colorLeft.setActionCommand("Color Left");
        mMenuSettings.addComponent(colorLeft);

        Button colorRight = new Button("", menuFont);
        colorRight.setTexture(settingsButtonTexture);
        colorRight.setWidth(SETTINGS_SIDE_DIMENSION);
        colorRight.setHeight(SETTINGS_SIDE_DIMENSION);
        colorRight.setX(SETTINGS_RIGHT_X);
        colorRight.setY(SETTINGS_COLOR_EDIT_Y);
        colorRight.setActionCommand("Color Right");
        mMenuSettings.addComponent(colorRight);

        //trails
        TextBox trailsTextBox = new Button("Trails", menuFont);
        trailsTextBox.setWidth(SETTINGS_CENTER_BOX_WIDTH);
        trailsTextBox.setHeight(SETTINGS_CENTER_BOX_HEIGHT);
        trailsTextBox.setX(Globals.GAME_WIDTH /2 - trailsTextBox.getWidth() / 2);
        trailsTextBox.setY(SETTINGS_TRAILS_Y);
        mMenuSettings.addComponent(trailsTextBox);

        Button trailsLeft = new Button("", menuFont);
        trailsLeft.setTexture(settingsButtonTexture);
        trailsLeft.setWidth(SETTINGS_SIDE_DIMENSION);
        trailsLeft.setHeight(SETTINGS_SIDE_DIMENSION);
        trailsLeft.setX(SETTINGS_LEFT_X);
        trailsLeft.setY(SETTINGS_TRAILS_EDIT_Y);
        trailsLeft.setActionCommand("Trails Left");
        mMenuSettings.addComponent(trailsLeft);

        Button trailsRight = new Button("", menuFont);
        trailsRight.setTexture(settingsButtonTexture);
        trailsRight.setWidth(SETTINGS_SIDE_DIMENSION);
        trailsRight.setHeight(SETTINGS_SIDE_DIMENSION);
        trailsRight.setX(SETTINGS_RIGHT_X);
        trailsRight.setY(SETTINGS_TRAILS_EDIT_Y);
        trailsRight.setActionCommand("Trails Right");
        mMenuSettings.addComponent(trailsRight);

        //back button
        Button backButton = new Button("Back", menuFont);
        backButton.setTexture(menuButtonTexture);
        backButton.setPressedTexture(menuButtonPressedTexture);
        backButton.setWidth(SETTINGS_CENTER_BOX_WIDTH);
        backButton.setHeight(SETTINGS_CENTER_BOX_HEIGHT);
        backButton.setX(Globals.GAME_WIDTH / 2 - backButton.getWidth() / 2);
        backButton.setY(SETTINGS_BACK_Y);
        backButton.setActionCommand("Settings Back");
        mMenuSettings.addComponent(backButton);
    }


    @Override
    public void onItemClicked(String actionCommand) {
        if (actionCommand == null){

        } else if (actionCommand.equals("1 Player")){

        } else if (actionCommand.equals("2 Player")){
            mCurrentMenu = mMenuDifficulty;
        } else if (actionCommand.equals("Go To Settings")){
            mCurrentMenu = mMenuSettings;
        } else if (actionCommand.equals("Easy Difficulty")){

        } else if (actionCommand.equals("Medium Difficulty")){

        } else if (actionCommand.equals("Hard Difficulty")){

        } else if (actionCommand.equals("Settings Back")){
            mCurrentMenu = mMenuMain;
        }

        //reset delay
        mCurrentDelay = DEFAULT_DELAY;
    }


}//end class MainMenuScreen
