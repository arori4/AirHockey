package com.arori4.airhockey;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the game engine for the AirHockeyGame
 * Currently implements update loop in render loop
 */
public final class AirHockeyGame extends Game
								implements ActionListener, Runnable{

	//Game Engine essentials
	public SpriteBatch batch;
	private AssetManager assetManager;

	//Game specific variables
	private static boolean isMultiplayer;
	private Vector3 touchPos;
	private boolean player1LastHit;
	private int player1Score;
	private int player2Score;
	private boolean player1Goal; //player 1 scored
	//Possible optimizations by declaring as instance variables
	private float input1X, input1Y, input2X, input2Y;
	private boolean input1Pressed, input2Pressed;
	//Settings change variables
	private boolean showTrails;
	private Color trailColor;

	//Constants
	public static final String GAME_NAME = "EXTREME AIR\n   HOCKEY";
	public static final String DIFFICULTY_TEXT = "DIFFICULTY";
	public static final String SETTINGS_TEXT = "Settings";
	public static final int MAX_GOALS = 7;
	//Strings
	public static final String STRING_PLAYER_1_SUCKS = "PLAYER 1 SUCKS";
	public static final String STRING_PLAYER_2_SUCKS = "PLAYER 2 SUCKS";
	public static final String STRING_GOAL = "GOAL";
	//Loading constants
	public static final int LOADING_WIDTH = 150;
	public static final int LOADING_HEIGHT = 800;
	public static final int LOADING_X = Globals.GAME_WIDTH / 2 - LOADING_WIDTH / 2;
	public static final int LOADING_Y = Globals.GAME_HEIGHT / 2 - LOADING_HEIGHT / 2;
	//Table measurement constants
	public static final int PADDLE_HEIGHT_LIMIT = Globals.GAME_HEIGHT/4;
	public static final int GOAL_WIDTH = 300;
	public static final int GOAL_HEIGHT = 150;
	public static final int GOAL_LEFT_BOUNDS = Globals.GAME_WIDTH/2 - GOAL_WIDTH/2;
	public static final int GOAL_RIGHT_BOUNDS = Globals.GAME_WIDTH/2 + GOAL_WIDTH/2;
	//AI Table measurement constants
	public static final int GOAL_AREA_CENTER = Globals.GAME_WIDTH/2;
	public static final int GOAL_AREA_HEIGHT_P1 = GOAL_HEIGHT/2 + 20;
	public static final int GOAL_AREA_HEIGHT_P2 = Globals.GAME_HEIGHT - GOAL_AREA_HEIGHT_P1;
	public static final int HALF_COURT = Globals.GAME_HEIGHT/2;
	//Puck constants
	public static final int INITIAL_PUCK_SPEED = 10;
	public static final int NUM_PUCK_TRAILS = 30;
	public static final float FRICTION = 0.85f;
	//Goal Constants
	public static final int GOALP1 = 1; //p1 gets puck into top goal
	public static final int GOALP2 = 2;
	//Score constants
	public static final int SCORE_X = 5;
	public static final int SCORE_P1Y = 50;
	public static final int SCORE_P2Y = Globals.GAME_HEIGHT - SCORE_P1Y + 20;
	public static final int SCORE_FONT_SIZE = 30;
	//Color constants
	public static final Color PUCK_COLOR = Color.GOLD;
	public static final Color PLAYER1_COLOR = Color.RED;
	public static final Color PLAYER2_COLOR = Color.BLUE;


	//Game assets
	//Images
	private Texture puckImage;
	private Texture puckTrailImage;
	private Texture tableImage;
	private Texture goalImage;
	private Texture defaultComponent;

	//Sounds
	private Sound countdownNumberSound;
	private Sound countdownGoSound;
	private Sound buttonPressSound;
	private Sound goalSound;
	private Sound ownGoalSound;
	private Sound puckPaddleCollideSound;
	private Sound puckTableCollideSound;

	//Fonts
	private BitmapFont scoreFont;

	//Game objects
	private Puck puck;
	private List<PuckTrail> list_puckTrails = new ArrayList<PuckTrail>();
	//Paddles
	private Paddle paddle1;
	private Paddle paddle2;
	//ai
	private AI player1AI;
	private AI player2AI;

	//GUI Objects
	//Popup Texts
	private PopupText countdown1;
	private PopupText countdown2;
	private PopupText countdown3;
	private PopupText countdownGo;
	private PopupText goalMessage;
	private PopupText ownGoalMessage;
	private PopupText stateGoalMessage;
	private PopupText gameFinalMessage;
	private PopupText gameFinalP1SucksMessage;
	private PopupText gameFinalP2SucksMessage;

	//States
	private int state;
	public static final int STATE_COUNTDOWN = 0;
	public static final int STATE_PLAY = 1;
	public static final int STATE_GOAL = 2;
	public static final int STATE_MAIN_MENU = 3;
	public static final int STATE_MENU_AI_DIFFICULTY = 4;
	public static final int STATE_WIN = 5;
	public static final int STATE_SETTINGS = 6;
	public static final int STATE_LOADING = 7;
	public static final int STATE_INITIALIZING = 8;

	//Countdown assets
	private int countdown;
	public static final int COUNTDOWN_START = 140;
	public static final int COUNTDOWN_INTERVAL = 40;
	public static final int GOAL_COUNTDOWN_START = 60;
	public static final int CLICK_DELAY = 45;
	public static final int GAME_FINAL_DURATION = 120;


	@Override
	/**
	 * Called once when the application is created
	 */
	public void create () {
		//create the asset manager for optimizations
		assetManager = new AssetManager();

		//Drawing Assets
		batch = new SpriteBatch();
		touchPos = new Vector3();

		//scores
		player1Score = 0;
		player2Score = 0;

		//setup fonts
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/arialuni.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter =
				new FreeTypeFontGenerator.FreeTypeFontParameter();

		//set up game font
		parameter.size = SCORE_FONT_SIZE;
		parameter.color = com.badlogic.gdx.graphics.Color.BLACK;
		parameter.borderColor = com.badlogic.gdx.graphics.Color.GOLD;
		scoreFont = generator.generateFont(parameter);

		//loading
		setState(STATE_LOADING);
		loadFiles();
	}


	@Override
	/**
	 * Called by the game loop logic every time rendering should be performed.
	 * Game logic can also be done here.
	 */
	public void render () {
		//reset
		Gdx.gl.glClearColor(1, 1, 1, 1); //white
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //OpenGl will clear the screen

		/**
		 * LOADING
		 * combines both update and render portions because
		 * spriteBatch and shapeRenderer hate each other
		 */
		if (state == STATE_LOADING || state == STATE_INITIALIZING){
			//display loading information
			float progress = assetManager.getProgress();

			//display progress
			//TODO: transform into a regular GUIComponent
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.rect(LOADING_X, LOADING_Y, LOADING_WIDTH, LOADING_HEIGHT * progress);
			shapeRenderer.end();

			//write messages
			batch.begin();
			if (state == STATE_LOADING){
				scoreFont.draw(batch, "Loading Assets...", 50, 50);
			} else if (state == STATE_INITIALIZING){
				scoreFont.draw(batch, "Initializing Assets...", 50, 50);
			}
			batch.end();

			//update
			if (state == STATE_LOADING){
				assetManager.update();
				if (Math.abs(progress - 1) < 0.001) {
					setState(STATE_INITIALIZING);
				}
			} else if (state == STATE_INITIALIZING){
				assignFiles();
				createGameAssets();
				setState(STATE_MAIN_MENU);
			}

			//debug
			if (Debug.LOADING){
				System.out.println("STATE: " + state + " PROGRESS: " + progress);
			}

			//end this immediately
			return;
		}

		/**
		 * Render portion of loop
		 * There might be a few parts that are updates
		 */
		//update the camera
		camera.update();
		//begin renders
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.setColor(1, 1, 1, 1);

		//main menu
		if (state == STATE_MAIN_MENU){
			//render buttons and background image
			batch.draw(menuBackgroundImage, 0, 0);
			menuPlayButton_1Player.draw(batch);
			menuPlayButton_2Player.draw(batch);
			menuSettingsButton.draw(batch);

			//render title text
			menuTitleFont.draw(batch, GAME_NAME,
					Globals.GAME_WIDTH/2 - titleTextLayout.width/2, TITLE_TEXT_Y);
		}

		//difficulty menu
		else if (state == STATE_MENU_AI_DIFFICULTY){
			//render buttons and background image
			batch.draw(menuBackgroundImage, 0, 0);
			menuDifficultyEasyButton.draw(batch);
			menuDifficultyMediumButton.draw(batch);
			menuDifficultyHardButton.draw(batch);

			//render title text
			menuTitleFont.draw(batch, DIFFICULTY_TEXT,
					Globals.GAME_WIDTH/2 - difficultyTextLayout.width/2, DIFFICULTY_TEXT_Y);
		}

		//settings menu
		else if (state == STATE_SETTINGS){
			//render buttons and background image
			batch.draw(menuBackgroundImage, 0, 0);

			//render buttons
			settingsColorButton.draw(batch);
			settingsColorLeftButton.draw(batch);
			settingsColorRightButton.draw(batch);
			settingsTrailsButton.draw(batch);
			settingsTrailsLeftButton.draw(batch);
			settingsTrailsRightButton.draw(batch);
			settingsBackButton.draw(batch);

			//render title text
			menuTitleFont.draw(batch, SETTINGS_TEXT, Globals.GAME_WIDTH/2 - settingsTextLayout.width/2,
					DIFFICULTY_TEXT_Y);
		}

		//regular game
		else if (state == STATE_PLAY ||
				state == STATE_GOAL ||
				state == STATE_COUNTDOWN ||
				state == STATE_WIN) {
			//render game components
			batch.draw(tableImage, 0, 0);//draw table first before anything
			batch.draw(goalImage, GOAL_LEFT_BOUNDS, 0,
					GOAL_WIDTH, GOAL_HEIGHT);//draw bottom goal image
			batch.draw(goalImage, GOAL_LEFT_BOUNDS, Globals.GAME_HEIGHT, //must be game height, reversing y direction draw
					GOAL_WIDTH, -GOAL_HEIGHT);//draw top goal image in reverse
			for (int index = 0; index < NUM_PUCK_TRAILS; index++){
				list_puckTrails.get(index).draw(batch);
			}
			paddle1.draw(batch);
			paddle2.draw(batch);
			puck.draw(batch);

			//render scores
			scoreFont.draw(batch, ("P1 Score: " + player1Score), SCORE_X, SCORE_P1Y);
			scoreFont.draw(batch, ("P2 Score: " + player2Score), SCORE_X, SCORE_P2Y);

			//game state countdown
			if (state == STATE_COUNTDOWN) {
				if (countdown == COUNTDOWN_START){
					countdownNumberSound.play();
				} else if (countdown > COUNTDOWN_START - COUNTDOWN_INTERVAL) {//first interval for 3
					countdown3.draw(batch, Position.CENTER);
				} else if (countdown == COUNTDOWN_START - COUNTDOWN_INTERVAL){
					countdownNumberSound.play();
				} else if (countdown > COUNTDOWN_START - COUNTDOWN_INTERVAL * 2 && //second interval for 2
						countdown < COUNTDOWN_START - COUNTDOWN_INTERVAL) {
					countdown2.draw(batch, Position.CENTER);
				} else if (countdown == COUNTDOWN_START - COUNTDOWN_INTERVAL * 2){
					countdownNumberSound.play();
				} else if (countdown > COUNTDOWN_START - COUNTDOWN_INTERVAL * 3 && //third interval for 1
						countdown < COUNTDOWN_START - COUNTDOWN_INTERVAL * 2) {
					countdown1.draw(batch, Position.CENTER);
				} else if (countdown == COUNTDOWN_START - COUNTDOWN_INTERVAL * 3){
					countdownGoSound.play();
				} else {
					countdownGo.draw(batch, Position.CENTER); //rest for the GO! sign
				}
			}

			if (state == STATE_GOAL){
				//draw the state goal message
				stateGoalMessage.draw(batch, Position.CENTER);
			}

			if (state == STATE_WIN){
				//draw the messages based on time
				if (countdown >= GAME_FINAL_DURATION / 2){
					gameFinalMessage.draw(batch, Position.CENTER);
				} else if (countdown > 0){
					if (player1Score >= MAX_GOALS){
						gameFinalP2SucksMessage.draw(batch, Position.CENTER);
					} else if (player2Score >= MAX_GOALS){
						gameFinalP1SucksMessage.draw(batch, Position.CENTER);
					} else{
						System.err.println("HOW THE FUCK DID SOMEONE WIN WITH LESS GOALS!??!?");
					}
				} else{
					setState(STATE_MAIN_MENU);
				}
				countdown--;
			}
		}//end cases regular game

		//end all renders
		batch.end();

		/*
		 * Update loop happens after render
		 * This is a temporary fix for an actual looop
		 * TODO: add in an actual update loop
		 */


		//state play
		if (state == STATE_PLAY || state == STATE_COUNTDOWN || state == STATE_GOAL) {
			//handle input to move the paddles
			input1X = paddle1.getxPosition();
			input1Y = paddle1.getyPosition();
			input1Pressed = false;
			input2X = paddle2.getxPosition();
			input2Y = paddle2.getyPosition();
			input2Pressed = false;
			//iterate through the inputs
			for (int index = 0; index < 2; index++) {
				if (Gdx.input.isTouched(index)) {//process input only if it is touched
					touchPos.set(Gdx.input.getX(index), Gdx.input.getY(index), 0);
					camera.unproject(touchPos);

					//separate into either player 1 or player 2
					if (touchPos.y < PADDLE_HEIGHT_LIMIT) {//player 1, bottom
						input1X = touchPos.x;
						input1Y = touchPos.y;
						input1Pressed = true;
					} else if (touchPos.y > Globals.GAME_HEIGHT - PADDLE_HEIGHT_LIMIT) {//player 2, top
						input2X = touchPos.x;
						input2Y = touchPos.y;
						input2Pressed = true;
					}
				}
			} //this is outside the state_play  state so you can move the paddles

			//paddle 1 must always update
			paddle1.update(input1X, input1Y, input1Pressed);

			//nullify movement of paddles depending on multiplayer settings
			if (!isMultiplayer){
				//TODO: update p1 and p2 depending on multiplayer settings
				player2AI.update(puck);
			}
			else{//update paddle 2 with input only if multiplayer
				paddle2.update(input2X, input2Y, input2Pressed);
			}

			//state countdown
			if (state == STATE_COUNTDOWN){
				if (countdown < 0) {
					state = STATE_PLAY;
				}
				countdown--;//decrement countdown after every interval
			}

			//ONLY UPDATE DURING PLAY
			if (state == STATE_PLAY) {
				//update the puck's movement
				puck.update();

				//puck collision with outer walls
				//left/right ball collision
				float wallXPosition = handleBounds(0, Globals.GAME_WIDTH, puck.getxPosition(), Puck.PUCK_RADIUS);
				//change x velocity if there is a possible change
				if (Math.abs(wallXPosition - puck.getxPosition()) > 0.0001) {
					puck.setxVelocity(-puck.getxVelocity() * FRICTION);
					puckTableCollideSound.play();
				}

				//top/bottom ball collision
				float wallYPosition = handleBounds(0, Globals.GAME_HEIGHT, puck.getyPosition(), Puck.PUCK_RADIUS);
				//make sure ball is not in the middle third
				if (puck.getxPosition() > GOAL_LEFT_BOUNDS && puck.getxPosition() < GOAL_RIGHT_BOUNDS) {
					wallYPosition = puck.getyPosition(); //set ball back to regular position if in goal area
				}
				//change x velocity if there is a possible change
				if (Math.abs(wallYPosition - puck.getyPosition()) > 0.0001) {
					puck.setyVelocity(-puck.getyVelocity() * FRICTION);
					puckTableCollideSound.play();
				}

				//update the puck again
				puck.setxPosition(wallXPosition);
				puck.setyPosition(wallYPosition);

				//handle collisions with the paddles
				if (puck.collidesWithPaddle(paddle1)) {
					handlePaddlePuckCollision(puck, paddle1);
					player1LastHit = true;
				} else if (puck.collidesWithPaddle(paddle2)) {
					handlePaddlePuckCollision(puck, paddle2);
					player1LastHit = false;
					//multiplayer handling
					if (isMultiplayer){
						player2AI.resetCountdown();
					}
				}

				//handle scoring. if scored, reset the puck and make the state count down again
				int whoseGoal = isInGoal();
				if (whoseGoal == GOALP1) {
					player1Score++;
					resetPuck();
					player1Goal = false;
					setState(STATE_GOAL);
				}
				if (whoseGoal == GOALP2) {
					player2Score++;
					resetPuck();
					player1Goal = true;
					setState(STATE_GOAL);
				}

			}//end state PLAY_GAME

			//evaluate win conditions
			if (state == STATE_GOAL){
				countdown--;

				//go to state countdown if done
				if (countdown < 0){
					//check for a win condition
					if (player1Score >= MAX_GOALS || player2Score >= MAX_GOALS){
						setState(STATE_WIN);
					}
					else{
						setState(STATE_COUNTDOWN);
					}
				}
			}

			//update the puckTrails
			//must happen after the puck is fully updated
			//but must also happen regardless wither playing game or goal scored
			for (int index = 0; index < NUM_PUCK_TRAILS; index++){
				PuckTrail curr = list_puckTrails.get(index);
				curr.update();
				if (curr.isRedraw()){
					curr.setxPosition(puck.getxPosition());
					curr.setyPosition(puck.getyPosition());
				}
			}

		}//end state PLAY_GAME, COUNTDOWN, GOAL
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
	 * Assigns loaded files into the game assets
	 */
	private void assignFiles(){
		//all images
		puckImage = assetManager.get("puck.png", Texture.class);
		puckTrailImage = assetManager.get("puck_trail.png", Texture.class);
		tableImage = assetManager.get("table.png", Texture.class);
		goalImage = assetManager.get("goal.png", Texture.class);
		defaultComponent = assetManager.get("template.png", Texture.class);

		//get sounds
		countdownNumberSound = assetManager.get("sounds/440_short.wav", Sound.class);
		countdownGoSound = assetManager.get("sounds/880_short.wav", Sound.class);
		buttonPressSound = assetManager.get("sounds/button.wav", Sound.class);
		goalSound = assetManager.get("sounds/goal.wav", Sound.class);
		ownGoalSound = assetManager.get("sounds/ownGoal.wav", Sound.class);
		puckPaddleCollideSound = assetManager.get("sounds/puckPaddleCollide.wav", Sound.class);
		puckTableCollideSound = assetManager.get("sounds/puckTableCollide.wav", Sound.class);
	}


	/**
	 * Creates all the game assets
	 */
	private void createGameAssets(){
		//puck
		puck = new Puck(puckImage);
		puck.setColor(PUCK_COLOR);
		resetPuck();
		//puck trails
		for (int index = 0; index < NUM_PUCK_TRAILS; index++){
			PuckTrail newPuckTrail = new PuckTrail(puckTrailImage,
					Puck.PUCK_RADIUS * 2, Puck.PUCK_RADIUS * 2,
					PuckTrail.PUCK_TRAIL_LENGTH - index);
			newPuckTrail.setColor(PUCK_COLOR);
			list_puckTrails.add(newPuckTrail);
		}
		showTrails = true;

		//paddle 1
		paddle1 = new Paddle(puckImage, Globals.GAME_WIDTH/2 - Paddle.PADDLE_RADIUS/2, 80);
		paddle1.setBounds(0, Globals.GAME_WIDTH, PADDLE_HEIGHT_LIMIT, 0);
		paddle1.setColor(PLAYER1_COLOR);
		//paddle 2
		paddle2 = new Paddle(puckImage, Globals.GAME_WIDTH/2 - Paddle.PADDLE_RADIUS/2, Globals.GAME_HEIGHT - 80);
		paddle2.setBounds(0, Globals.GAME_WIDTH, Globals.GAME_HEIGHT, Globals.GAME_HEIGHT - PADDLE_HEIGHT_LIMIT);
		paddle2.setColor(PLAYER2_COLOR);

		//countdown objects
		countdown1 = new PopupText(countdown1Image);
		countdown2 = new PopupText(countdown2Image);
		countdown3 = new PopupText(countdown3Image);
		countdownGo = new PopupText(countdownGoImage);
		goalMessage = new PopupText(goalMessageImage);
		ownGoalMessage = new PopupText(ownGoalMessageImage);
		//final messages
		gameFinalMessage = new PopupText(gameFinalImage);
		gameFinalP1SucksMessage = new PopupText(gameFinalP1SucksImage);
		gameFinalP1SucksMessage.setForegroundColor(PLAYER2_COLOR);
		gameFinalP2SucksMessage = new PopupText(gameFinalP2SucksImage);
		gameFinalP2SucksMessage.setForegroundColor(PLAYER1_COLOR);



		//ai
		player1AI = new AI(0.75f, paddle1, false);
		player2AI = new AI(0.75f, paddle2, true);
	}

	/**
	 * Handles puck collision
	 * PRECONDITION: puck and paddle already collide
	 * @param puck - puck to check
	 * @param paddle - paddle to check
     */
	private void handlePaddlePuckCollision(Puck puck, Paddle paddle){
		//retrieve collision points
		//TODO: Use the collision points to add some effect
		float collisionPointX = ( (paddle.getxPosition() * Puck.PUCK_RADIUS) +
									(puck.getxPosition() * Paddle.PADDLE_RADIUS) /
								(Puck.PUCK_RADIUS + Paddle.PADDLE_RADIUS));
		float collisionPointY = ( (paddle.getyPosition() * Puck.PUCK_RADIUS) +
									(puck.getyPosition() * Paddle.PADDLE_RADIUS) /
								(Puck.PUCK_RADIUS + Paddle.PADDLE_RADIUS));

		//calculate new x and y velocities of the puck
		float puckxVelocity = (puck.getxVelocity() * (Puck.PUCK_MASS - Paddle.PADDLE_MASS) +
				(2 * Paddle.PADDLE_MASS * paddle.getxVelocity())) / (Puck.PUCK_MASS + Paddle.PADDLE_MASS);
		float puckyVelocity = (puck.getyVelocity() * (Puck.PUCK_MASS - Paddle.PADDLE_MASS) +
				(2 * Paddle.PADDLE_MASS * paddle.getyVelocity())) / (Puck.PUCK_MASS + Paddle.PADDLE_MASS);

		//set the new x and y velocities of the puck. Adjust for friction effects to slow down puck
		puck.setxVelocity(puckxVelocity * FRICTION);
		puck.setyVelocity(puckyVelocity * FRICTION);

		//if puck and paddle are in the same location, force the puck out with random values
		if (Math.abs(paddle.getxPosition() - puck.getxPosition()) < 30 &&
				Math.abs(paddle.getyPosition() - puck.getyPosition()) < 30){
			puck.setxVelocity( (float)((Math.random() - 0.5) * 30) );
			puck.setyVelocity( (float)((Math.random() - 0.5) * 30) );
		}
		//if not, play the sound
		else{
			puckPaddleCollideSound.play();
		}

		//if we are in multiplayer, then make the ai go back
		if (!isMultiplayer){
			if (paddle == paddle2){
				player2AI.resetCountdown();
			}
			else{
				player1AI.resetCountdown();
			}
		}

		//move the paddle if it's not selected
		if (!paddle.isHeld()){
			//calculate new x and y velocities of the puck. multiply by a negative b/c math is weird
			float paddlexVelocty = -(paddle.getxVelocity() * (Paddle.PADDLE_MASS - Puck.PUCK_MASS) +
					(2 * Puck.PUCK_MASS * puck.getxVelocity())) / (Puck.PUCK_MASS + Paddle.PADDLE_MASS);
			float paddleyVelocity = -(paddle.getyVelocity() * (Paddle.PADDLE_MASS - Puck.PUCK_MASS) +
					(2 * Puck.PUCK_MASS * puck.getyVelocity())) / (Puck.PUCK_MASS + Paddle.PADDLE_MASS);

			//set the new x and y velocities of the puck. Adjust for friction effects to slow down puck
			paddle.setxVelocity(paddlexVelocty * FRICTION);
			paddle.setyVelocity(paddleyVelocity * FRICTION);
		}

		//set the paddle to not collide again for a period of time
		paddle.resetCollision();
	}


	/**
	 * Sets the state based on what is passed in
	 * @param gameState - new game state
     */
	private void setState(int gameState){
		if (gameState == STATE_PLAY){
			//set the state
			state = STATE_PLAY;
		}
		else if (gameState == STATE_COUNTDOWN){
			//set state
			state = STATE_COUNTDOWN;
			//reset the countdown timer
			countdown = COUNTDOWN_START;
		}
		else if (gameState == STATE_GOAL){
			//set state
			state = STATE_GOAL;
			//set countdown
			countdown = GOAL_COUNTDOWN_START;

			//display correct message color
			if (player1Goal){
				if (player1LastHit){
					//own goal
					stateGoalMessage = ownGoalMessage;
					stateGoalMessage.setForegroundColor(PLAYER1_COLOR);
					ownGoalSound.play();
				} else{
					//not own goal
					stateGoalMessage = goalMessage;
					stateGoalMessage.setForegroundColor(PLAYER2_COLOR);
					goalSound.play();
				}
			} else{
				if (!player1LastHit){
					//own goal
					stateGoalMessage = ownGoalMessage;
					stateGoalMessage.setForegroundColor(PLAYER2_COLOR);
					ownGoalSound.play();
				} else{
					//not own goal
					stateGoalMessage = goalMessage;
					stateGoalMessage.setForegroundColor(PLAYER1_COLOR);
					goalSound.play();
				}
			}
		}
		else if (gameState == STATE_MAIN_MENU){
			//set state
			state = STATE_MAIN_MENU;
			countdown = CLICK_DELAY;
			//reset scores
			player1Score = 0;
			player2Score = 0;
		}
		else if (gameState == STATE_MENU_AI_DIFFICULTY){
			//set state
			state = STATE_MENU_AI_DIFFICULTY;
			countdown = CLICK_DELAY;
		}
		else if (gameState == STATE_WIN){
			//set state
			state = STATE_WIN;
			countdown = GAME_FINAL_DURATION;

			//set color
			if (player1Score >= MAX_GOALS){
				gameFinalMessage.setForegroundColor(PLAYER1_COLOR);
			}
			else if (player2Score >= MAX_GOALS){
				gameFinalMessage.setForegroundColor(PLAYER2_COLOR);
			}
		}
		else if (gameState == STATE_SETTINGS){
			//set state
			state = STATE_SETTINGS;
		}
		else if (gameState == STATE_LOADING){
			//set state
			state = STATE_LOADING;
		}
		else if (gameState == STATE_INITIALIZING){
			//set state
			state = STATE_INITIALIZING;
		}
		else{
			System.err.println("ERROR: GAME STATE " + gameState + " is an invalid state");
		}
	}


	/**
	 * Checks if in goal, and who's goal
	 * @return - 0 if no goal, 1 if P1 scores into the top goal, 2 if P2 scores into the bottom goal
     */
	private int isInGoal(){
		if (puck.getyPosition() > Globals.GAME_HEIGHT){
			return GOALP1;
		} else if (puck.getyPosition() < 0){
			return GOALP2;
		} else{
			return 0;
		}
	}


	/**
	 * Resets the puck location to the center and gives it a random velocity.
	 */
	private void resetPuck(){
		puck.setxPosition(Globals.GAME_WIDTH/2);
		puck.setyPosition(Globals.GAME_HEIGHT/2);
		puck.setxVelocity( ((float)Math.random() - 0.5f) * INITIAL_PUCK_SPEED);
		puck.setyVelocity( ((float)Math.random() - 0.5f) * INITIAL_PUCK_SPEED);

		//make sure that y velocity is enough
		while (Math.abs(puck.getyVelocity()) < 4) {
			puck.setyVelocity(((float) Math.random() - 0.5f) * INITIAL_PUCK_SPEED);
		}
	}


	/**
	 * Handles the bounds for a ball in one dimension
	 * Run it a second time for the other dimension
	 * @param lowBound - lower bound for movement
	 * @param highBound - upper bound for movement
	 * @param position - current position, x or y
	 * @param radius - radius of the object
	 * @return - the new position, adjusted for bounds
	 */
	private float handleBounds(int lowBound, int highBound, float position, float radius){
		if (position - radius/2 < lowBound ){
			return lowBound + radius/2;
		} else if (position + radius/2 > highBound){
			return highBound - radius/2;
		} else{
			return position;
		}
	}


	@Override
	public void onItemClicked(String actionCommand) {



	}

	@Override
	public void run() {

	}
}//end class AirHockeyGame