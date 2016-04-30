package com.arori4.airhockey;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Defines the game engine for the AirHockeyGame
 * Currently implements update loop in render loop
 */
public final class AirHockeyGame extends Game{

	///Game large components
	private GameScreen mGameScreen;
	private MainMenuScreen mMainMenuScreen;

	//Game Engine essentials
	public SpriteBatch batch;
	public ShapeRenderer mShapeRenderer;
	private AssetManager assetManager;
	private OrthographicCamera mCamera;

	//Settings change variables
	public boolean showTrails;
	public Color trailColor;
	public boolean isMultiplayer;

	//Loading fonts
	private BitmapFont mLoadingFont;

	//States
	private int state;
	public static final int STATE_LOADING = 0;

	//Constants
	public static final String GAME_NAME = "EXTREME AIR\n   HOCKEY";
	public static final String DIFFICULTY_TEXT = "DIFFICULTY";
	public static final String SETTINGS_TEXT = "Settings";
	public static final int MAX_GOALS = 7;
	//Loading constants
	public static final int LOADING_WIDTH = 150;
	public static final int LOADING_HEIGHT = 800;
	public static final int LOADING_X = Globals.GAME_WIDTH / 2 - LOADING_WIDTH / 2;
	public static final int LOADING_Y = Globals.GAME_HEIGHT / 2 - LOADING_HEIGHT / 2;
	public static final int LOADING_FONT_SIZE = 30;


	@Override
	/**
	 * Called once when the application is created
	 */
	public void create () {
		//create the asset manager for optimizations
		assetManager = new AssetManager();

		//create camera for the loading screen
		mCamera = new OrthographicCamera();
		mCamera.setToOrtho(false, Globals.GAME_WIDTH, Globals.GAME_HEIGHT);

		//Drawing Assets
		batch = new SpriteBatch();

		//loading
		setState(STATE_LOADING);
		loadFiles();
	}

	private void setupFonts(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/arialuni.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter =
				new FreeTypeFontGenerator.FreeTypeFontParameter();

		//set up game font
		parameter.size = LOADING_FONT_SIZE;
		parameter.color = com.badlogic.gdx.graphics.Color.BLACK;
		mLoadingFont = generator.generateFont(parameter);
	}


	@Override
	/**
	 * Called by the game loop logic every time rendering should be performed.
	 * Game logic can also be done here.
	 */
	public void render () {
		//call super class
		super.render();

		if (state == STATE_LOADING) {
			//reset
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //OpenGl will clear the screen

			//display loading information
			float progress = assetManager.getProgress();
			//display progress
			mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			mShapeRenderer.setProjectionMatrix(mCamera.combined);
			mShapeRenderer.setColor(Color.GREEN);
			mShapeRenderer.rect(LOADING_X, LOADING_Y, LOADING_WIDTH, LOADING_HEIGHT * progress);
			mShapeRenderer.end();
			//write messages
			batch.begin();
			mLoadingFont.draw(batch, "Loading Assets...", 50, 50);
			batch.end();

			//update
			assetManager.update();
			if (Math.abs(progress - 1) < 0.001) {
				mMainMenuScreen = new MainMenuScreen(this, assetManager);

				//weird stuff
				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
						Gdx.files.internal("fonts/arialuni.ttf"));
				FreeTypeFontGenerator.FreeTypeFontParameter parameter =
						new FreeTypeFontGenerator.FreeTypeFontParameter();

				//set up game font
				parameter.size = 30;
				parameter.color = com.badlogic.gdx.graphics.Color.BLACK;
				BitmapFont scoreFont = generator.generateFont(parameter);

				//set up popup text font
				parameter.size = 250;
				parameter.color = Color.WHITE;
				BitmapFont popupTextFont = generator.generateFont(parameter);

				mGameScreen = new GameScreen(this, assetManager, scoreFont, popupTextFont);

				//remove the generator as we don't need it anymore
				generator.dispose();


				//set main menu now
				setMainMenu();
			}

			//debug
			if (Debug.LOADING) {
				System.out.println("STATE: " + state + " PROGRESS: " + progress);
			}
		}
	}


	public void setMainMenu(){
		this.setScreen(mMainMenuScreen);
	}

	public void setGame(float difficulty){
		if (!isMultiplayer){
			mGameScreen.player2AI.setDifficulty(difficulty);
		}

		//start game
		this.setScreen(mGameScreen);
	}


	@Override
	/**
	 * Called every time the game screen is resized and the game is not paused.
	 * Called once also when the game is created.
	 * Parameters are the new width and height of the screen
	 */
	public void resize(int width, int height) {
		super.resize(width, height);
	}


	@Override
	/**
	 * Pauses game state
	 */
	public void pause() {
		super.pause();
	}


	@Override
	/**
	 * Called after paused.
	 * Only for android
	 */
	public void resume() {
		super.resume();
	}


	@Override
	/**
	 * Called when the application is destroyed. It is preceded by a call to pause.
	 */
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		batch.dispose();
	}

	/**
	 * Helper method to load all the files
	 * This will keep the create method tiny
	 */
	private void loadFiles(){
		//all images
		assetManager.load("puck.png", Texture.class);
		assetManager.load("puck_trail.png", Texture.class);
		assetManager.load("table.png", Texture.class);
		assetManager.load("goal.png", Texture.class);
		assetManager.load("template.png", Texture.class);
		//menu button images
		assetManager.load("menuBackground.png", Texture.class);
		assetManager.load("menuButton.png", Texture.class);
		assetManager.load("menuButtonPressed.png", Texture.class);
		assetManager.load("triangle.png", Texture.class);

		//load sounds
		assetManager.load("sounds/440_short.wav", Sound.class);
		assetManager.load("sounds/880_short.wav", Sound.class);
		assetManager.load("sounds/button.wav", Sound.class);
		assetManager.load("sounds/goal.wav", Sound.class);
		assetManager.load("sounds/ownGoal.wav", Sound.class);
		assetManager.load("sounds/puckPaddleCollide.wav", Sound.class);
		assetManager.load("sounds/puckTableCollide.wav", Sound.class);
	}


	/**
	 * Sets the state to the new state
	 * @param newState - state to set
     */
	private void setState(int newState){
		if (newState == STATE_LOADING){
			state = newState;
		} else{
			System.out.println("ERROR: State " + newState + " is not a valid state in " +
			"file AirHockeyGame.java");
		}
	}


}//end class AirHockeyGame