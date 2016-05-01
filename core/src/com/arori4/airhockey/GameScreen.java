package com.arori4.airhockey;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Cabreros on 29-Apr-16.
 * Defines the game screen, where the game is played.
 */
public class GameScreen implements Screen {

    //Engine objects
    private AirHockeyGame mGame;
    private AssetManager mManager;
    private OrthographicCamera mCamera;
    private int mCurrentDelay;
    private Vector3 touchPos;

    //Game objects and fields
    //Scoring
    private int player1Score;
    private int player2Score;
    private boolean player1LastHit;
    private boolean player1ScoredGoal;
    //Puck
    private Puck puck;
    private List<PuckTrail> list_puckTrails = new ArrayList<PuckTrail>();
    //Paddles
    private Paddle paddle1;
    private Paddle paddle2;
    //ai
    public AI player1AI;
    public AI player2AI;

    //Game assets
    //Images
    private Texture puckTexture;
    private Texture puckTrailTexture;
    private Texture tableTexture;
    private Texture goalTexture;
    //Sounds
    private Sound countdownNumberSound;
    private Sound countdownGoSound;
    private Sound goalSound;
    private Sound ownGoalSound;
    private Sound puckPaddleCollideSound;
    private Sound puckTableCollideSound;
    //Fonts
    private BitmapFont scoreFont;
    private BitmapFont popupTextFont;
    private BitmapFont bigPopupTextFont;

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

    //Constants
    //Game constants
    public static final int MAX_GOALS = 7;
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
    public static final int NUM_PUCK_TRAILS_X = 30;
    public static final float FRICTION = 0.85f;
    //Goal Constants
    public static final int GOALP1 = 1; //p1 gets puck into top goal
    public static final int GOALP2 = 2;
    //Score constants
    public static final int SCORE_X = 5;
    public static final int SCORE_P1Y = 50;
    public static final int SCORE_P2Y = Globals.GAME_HEIGHT - SCORE_P1Y + 20;
    public static final int SCORE_FONT_SIZE = 30;
    public static final int POPUP_TEXT_FONT_SIZE = 250;
    public static final int BIG_POPUP_TEXT_FONT_SIZE = 400;
    //Color constants
    public static final Color PUCK_COLOR = Color.GOLD;
    public static final Color PLAYER1_COLOR = Color.RED;
    public static final Color PLAYER2_COLOR = Color.BLUE;

    //Countdown assets
    public static final int COUNTDOWN_START = 140;
    public static final int COUNTDOWN_INTERVAL = 40;
    public static final int GOAL_COUNTDOWN_START = 60;
    public static final int GAME_FINAL_DURATION = 120;

    //States
    private int state;
    public static final int STATE_COUNTDOWN = 0;
    public static final int STATE_PLAY = 1;
    public static final int STATE_GOAL = 2;
    public static final int STATE_WIN = 3;

    public GameScreen(final AirHockeyGame game, final AssetManager manager){
        //assign variables
        mGame = game;
        mManager = manager;

        //set camera
        mCamera = new OrthographicCamera();
        mCamera.setToOrtho(false, Globals.GAME_WIDTH, Globals.GAME_HEIGHT);
        touchPos = new Vector3();

        //assign assets
        //all images
        puckTexture = mManager.get("puck.png", Texture.class);
        puckTrailTexture = mManager.get("puck_trail.png", Texture.class);
        tableTexture = mManager.get("table.png", Texture.class);
        goalTexture = mManager.get("goal.png", Texture.class);
        //all sounds
        countdownNumberSound = mManager.get("sounds/440_short.wav", Sound.class);
        countdownGoSound = mManager.get("sounds/880_short.wav", Sound.class);
        goalSound = mManager.get("sounds/goal.wav", Sound.class);
        ownGoalSound = mManager.get("sounds/ownGoal.wav", Sound.class);
        puckPaddleCollideSound = mManager.get("sounds/puckPaddleCollide.wav", Sound.class);
        puckTableCollideSound = mManager.get("sounds/puckTableCollide.wav", Sound.class);

        //scores
        player1Score = 0;
        player2Score = 0;

        setupFonts();
        setupAssets();
    }


    /**
     * Creates all the assets for the game
     */
    private void setupAssets(){
        //puck
        puck = new Puck(puckTexture);
        puck.setColor(PUCK_COLOR);
        resetPuck();
        //puck trails
        for (int index = 0; index < NUM_PUCK_TRAILS_X; index++){
            PuckTrail newPuckTrail = new PuckTrail(puckTrailTexture,
                    Puck.PUCK_RADIUS * 2, Puck.PUCK_RADIUS * 2,
                    PuckTrail.PUCK_TRAIL_LENGTH - index);
            newPuckTrail.setColor(PUCK_COLOR);
            list_puckTrails.add(newPuckTrail);
        }

        //paddle 1
        paddle1 = new Paddle(puckTexture, Globals.GAME_WIDTH/2 - Paddle.PADDLE_RADIUS/2, 80);
        paddle1.setBounds(0, Globals.GAME_WIDTH, PADDLE_HEIGHT_LIMIT, 0);
        paddle1.setColor(PLAYER1_COLOR);
        //paddle 2
        paddle2 = new Paddle(puckTexture, Globals.GAME_WIDTH/2 - Paddle.PADDLE_RADIUS/2, Globals.GAME_HEIGHT - 80);
        paddle2.setBounds(0, Globals.GAME_WIDTH, Globals.GAME_HEIGHT, Globals.GAME_HEIGHT - PADDLE_HEIGHT_LIMIT);
        paddle2.setColor(PLAYER2_COLOR);

        //countdown objects
        countdown1 = new PopupText("1", bigPopupTextFont);
        countdown1.setTextColor(Color.BLACK);

        countdown2 = new PopupText("2", bigPopupTextFont);
        countdown2.setTextColor(Color.BLACK);

        countdown3 = new PopupText("3", bigPopupTextFont);
        countdown3.setTextColor(Color.BLACK);

        countdownGo = new PopupText("GO!", bigPopupTextFont);
        countdownGo.setTextColor(Color.BLACK);

        goalMessage = new PopupText("GOAL", popupTextFont);

        ownGoalMessage = new PopupText("OWN GOAL LOSER", popupTextFont);

        //final messages
        gameFinalMessage = new PopupText("GAME OVER", popupTextFont);

        gameFinalP1SucksMessage = new PopupText("PLAYER 1 SUCKS", popupTextFont);
        gameFinalP1SucksMessage.setTextColor(PLAYER2_COLOR);

        gameFinalP2SucksMessage = new PopupText("PLAYER 2 SUCKS", popupTextFont);
        gameFinalP2SucksMessage.setTextColor(PLAYER1_COLOR);

        //ai
        player1AI = new AI(paddle1, false);
        player2AI = new AI(paddle2, true);
    }


    /**
     * Sets up the fonts
     */
    private void setupFonts(){
        //weird stuff
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/arialuni.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        //set up game font
        parameter.size = SCORE_FONT_SIZE;
        parameter.color = com.badlogic.gdx.graphics.Color.BLACK;
        scoreFont = generator.generateFont(parameter);

        //set up popup text font
        parameter.size = POPUP_TEXT_FONT_SIZE;
        parameter.color = Color.WHITE;
        popupTextFont = generator.generateFont(parameter);

        //set up big popup text font
        parameter.size = BIG_POPUP_TEXT_FONT_SIZE;
        parameter.color = Color.WHITE;
        bigPopupTextFont = generator.generateFont(parameter);

        //remove the generator as we don't need it anymore
        generator.dispose();
    }


    @Override
    /**
     * Called when the game first starts
     * Sets the game to countdown
     */
    public void show() {
        setState(STATE_COUNTDOWN);
    }


    @Override
    public void render(float delta) {
        //clear screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update and start spriteBatch
        mCamera.update();
        mGame.batch.setProjectionMatrix(mCamera.combined);
        mGame.batch.begin();

        //reset the color to white
        mGame.batch.setColor(Color.WHITE);

        //render game components
        mGame.batch.draw(tableTexture, 0, 0);//draw table first before anything
        mGame.batch.draw(goalTexture, GOAL_LEFT_BOUNDS, 0,
                GOAL_WIDTH, GOAL_HEIGHT);//draw bottom goal image
        mGame.batch.draw(goalTexture, GOAL_LEFT_BOUNDS, Globals.GAME_HEIGHT, //must be game height, reversing y direction draw
                GOAL_WIDTH, -GOAL_HEIGHT);//draw top goal image in reverse
        for (int index = 0; index < NUM_PUCK_TRAILS_X; index++) {
            list_puckTrails.get(index).draw(mGame.batch);
        }
        paddle1.draw(mGame.batch);
        paddle2.draw(mGame.batch);
        puck.draw(mGame.batch);

        //render scores
        scoreFont.draw(mGame.batch, ("P1 Score: " + player1Score), SCORE_X, SCORE_P1Y);
        scoreFont.draw(mGame.batch, ("P2 Score: " + player2Score), SCORE_X, SCORE_P2Y);

        //game state countdown
        if (state == STATE_COUNTDOWN) {
            if (mCurrentDelay >= COUNTDOWN_START - COUNTDOWN_INTERVAL) {
                countdown3.draw(mGame.batch);
            }
            else if (mCurrentDelay >= COUNTDOWN_START - COUNTDOWN_INTERVAL * 2 &&
                    mCurrentDelay < COUNTDOWN_START - COUNTDOWN_INTERVAL) {
                countdown2.draw(mGame.batch);
            }
            else if (mCurrentDelay >= COUNTDOWN_START - COUNTDOWN_INTERVAL * 3 &&
                    mCurrentDelay < COUNTDOWN_START - COUNTDOWN_INTERVAL * 2) {
                countdown1.draw(mGame.batch);
            }
            else {
                countdownGo.draw(mGame.batch); //rest for the GO! sign
            }
        }

        else if (state == STATE_GOAL) {
            //draw the state goal message
            stateGoalMessage.draw(mGame.batch);
        }

        else if (state == STATE_WIN) {
            //draw the messages based on time
            if (mCurrentDelay >= GAME_FINAL_DURATION / 2) {
                gameFinalMessage.draw(mGame.batch);
            } else if (mCurrentDelay > 0) {
                if (player1Score >= MAX_GOALS) {
                    gameFinalP2SucksMessage.draw(mGame.batch);
                } else if (player2Score >= MAX_GOALS) {
                    gameFinalP1SucksMessage.draw(mGame.batch);
                } else {
                    System.err.println("HOW THE FUCK DID SOMEONE WIN WITH LESS GOALS!??!?");
                }
            } else {
                mGame.setMainMenu();
            }
        }
    

        //end all renders
        mGame.batch.end();

        //call update
        update(delta);
    }


    /**
     * Updates the game
     * @param delta - change in time
     */
    public void update(float delta){
        //handle input to move the paddles
        float input1X = paddle1.getxPosition();
        float input1Y = paddle1.getyPosition();
        boolean input1Pressed = false;
        float input2X = paddle2.getxPosition();
        float input2Y = paddle2.getyPosition();
        boolean input2Pressed = false;
        //iterate through the inputs
        for (int index = 0; index < 2; index++) {
            if (Gdx.input.isTouched(index)) {//process input only if it is touched
                touchPos.set(Gdx.input.getX(index), Gdx.input.getY(index), 0);
                mCamera.unproject(touchPos);

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
        if (!mGame.isMultiplayer){
            //TODO: update p1 and p2 depending on multiplayer settings
            player2AI.update(puck);
        }
        else{//update paddle 2 with input only if multiplayer
            paddle2.update(input2X, input2Y, input2Pressed);
        }

        //update the puckTrails
        for (int index = 0; index < NUM_PUCK_TRAILS_X; index++){
            PuckTrail curr = list_puckTrails.get(index);
            curr.update();

            //redraw puck if needed
            if (curr.isRedraw()){
                curr.setxPosition(puck.getxPosition());
                curr.setyPosition(puck.getyPosition());
            }
        }

        //state countdown
        if (state == STATE_COUNTDOWN){
            if (mCurrentDelay == COUNTDOWN_START) {
                countdownNumberSound.play();
                countdown3.start();
                if (Debug.ENGINE){
                    System.err.println("Countdown 3");
                }
            } else if (mCurrentDelay > COUNTDOWN_START - COUNTDOWN_INTERVAL) {//first interval for 3
                countdown3.update();
            }

            else if (mCurrentDelay == COUNTDOWN_START - COUNTDOWN_INTERVAL) {
                countdownNumberSound.play();
                countdown2.start();
                if (Debug.ENGINE){
                    System.err.println("Countdown 2");
                }
            } else if (mCurrentDelay > COUNTDOWN_START - COUNTDOWN_INTERVAL * 2 && //second interval for 2
                    mCurrentDelay < COUNTDOWN_START - COUNTDOWN_INTERVAL) {
                countdown2.update();
            }

            else if (mCurrentDelay == COUNTDOWN_START - COUNTDOWN_INTERVAL * 2) {
                countdownNumberSound.play();
                countdown1.start();
                if (Debug.ENGINE){
                    System.err.println("Countdown 1");
                }
            } else if (mCurrentDelay > COUNTDOWN_START - COUNTDOWN_INTERVAL * 3 && //third interval for 1
                    mCurrentDelay < COUNTDOWN_START - COUNTDOWN_INTERVAL * 2) {
                countdown1.update();
            }

            else if (mCurrentDelay == COUNTDOWN_START - COUNTDOWN_INTERVAL * 3) {
                countdownGoSound.play();
                countdownGo.start();
                if (Debug.ENGINE){
                    System.err.println("Countdown Go!");
                }
            } else {
                countdownGo.update(); //rest for the GO! sign
            }

            if (mCurrentDelay < 0) {
                setState(STATE_PLAY);
            }
        }

        else if (state == STATE_PLAY) {
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
                if (mGame.isMultiplayer){
                    player2AI.resetCountdown();
                }
            }

            //handle scoring. if scored, reset the puck and make the state count down again
            int whoseGoal = isInGoal();
            if (whoseGoal == GOALP1) {
                player1Score++;
                resetPuck();
                player1ScoredGoal = false;
                setState(STATE_GOAL);
            }
            if (whoseGoal == GOALP2) {
                player2Score++;
                resetPuck();
                player1ScoredGoal = true;
                setState(STATE_GOAL);
            }

        }//end state PLAY_GAME

        //evaluate win conditions
        else if (state == STATE_GOAL){
            //update the goal message to fade
            stateGoalMessage.update();

            //go to state countdown if done
            if (mCurrentDelay < 0){
                //check for a win condition
                if (player1Score >= MAX_GOALS || player2Score >= MAX_GOALS){
                    setState(STATE_WIN);
                }
                else{
                    setState(STATE_COUNTDOWN);
                }
            }
        }

        //remove countdown universally after everything
        mCurrentDelay--;

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
        scoreFont.dispose();
        popupTextFont.dispose();
        bigPopupTextFont.dispose();
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
        if (!mGame.isMultiplayer){
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
        } else if (gameState == STATE_COUNTDOWN){
            //set state
            state = STATE_COUNTDOWN;
            //reset the countdown timer
            mCurrentDelay = COUNTDOWN_START;
        } else if (gameState == STATE_GOAL){
            //set state
            state = STATE_GOAL;
            //set countdown
            mCurrentDelay = GOAL_COUNTDOWN_START;

            //display correct message color
            if (player1ScoredGoal){
                if (player1LastHit){
                    //own goal
                    stateGoalMessage = ownGoalMessage;
                    stateGoalMessage.setTextColor(PLAYER1_COLOR);
                    stateGoalMessage.start();
                    ownGoalSound.play();
                } else{
                    //not own goal
                    stateGoalMessage = goalMessage;
                    stateGoalMessage.setTextColor(PLAYER2_COLOR);
                    stateGoalMessage.start();
                    goalSound.play();
                }
            } else{
                if (!player1LastHit){
                    //own goal
                    stateGoalMessage = ownGoalMessage;
                    stateGoalMessage.setTextColor(PLAYER2_COLOR);
                    stateGoalMessage.start();
                    ownGoalSound.play();
                } else{
                    //not own goal
                    stateGoalMessage = goalMessage;
                    stateGoalMessage.setTextColor(PLAYER1_COLOR);
                    stateGoalMessage.start();
                    goalSound.play();
                }
            }
        } else if (gameState == STATE_WIN){
            //set state
            state = STATE_WIN;
            mCurrentDelay = GAME_FINAL_DURATION;

            //set color
            if (player1Score >= MAX_GOALS){
                gameFinalMessage.setTextColor(PLAYER1_COLOR);
            }
            else if (player2Score >= MAX_GOALS){
                gameFinalMessage.setTextColor(PLAYER2_COLOR);
            }
        } else{
            System.err.println("ERROR: GAME STATE " + gameState + " is an invalid state");
        }
    }

}//end class GameScreen
