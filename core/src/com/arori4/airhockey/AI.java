package com.arori4.airhockey;

import java.util.Random;

/**
 * Created by Christopher Cabreros on 22-Apr-16.
 * AI for puck behavior
 */
public class AI {

    private float difficulty;
    private Paddle paddle;
    private boolean isTop;

    public static final int AI_GLOBAL_BASE_SPEED = 8;
    public static final int AI_COUNTDOWN_INTERVAL = 60;
    private int countdown;
    private float threshold_height;

    //strategy and variables
    private Random rand;
    private int strategy;
    public static final int STRATEGY_GUARD = 1;
    public static final float STRATEGY_GUARD_THRESHOLD = 0.8f;
    public static final int STRATEGY_GUARD_FRONT = 4;
    public static final float STRATEGY_GUARD_FRONT_THRESHOLD = 0.55f;
    public static final int STRATEGY_PASSIVE = 2;
    public static final float STRATEGY_PASSIVE_THRESHOLD = 0.3f;
    public static final int STRATEGY_SLOW = 3;
    private float attackVelocity;


    public AI(Paddle paddle, boolean isTop) {
        setDifficulty(difficulty);
        this.paddle = paddle;
        this.isTop = isTop;

        countdown = 0;
        strategy = 1;
        attackVelocity = 2;
        rand = new Random();

        if (isTop){
            threshold_height = GameScreen.GOAL_AREA_HEIGHT_P2;
        } else{
            threshold_height = GameScreen.GOAL_AREA_HEIGHT_P1;
        }
    }

    /**
     * Updates the AI with the given puck
     * @param puck - puck of the game
     */
    public void update(Puck puck){
        //create variables
        float moveX = paddle.getxPosition();
        float moveY = paddle.getyPosition();

        //manage countdown intervals.
        //we do not reset countdown here, only when puck hits
        if (countdown % AI_COUNTDOWN_INTERVAL == 0){
            changeAIStrategies();
        }//end special changes

        //make countdown go down
        countdown--;

        //paddle movement
        //handle if top
        if (isTop){ //PLAYER 2
            //if lower than half court, revert to strategy
            if (puck.getyPosition() < GameScreen.HALF_COURT){
                if (strategy == STRATEGY_GUARD) {
                    moveStrategyGuard(GameScreen.GOAL_AREA_HEIGHT_P2, puck);
                } else if (strategy == STRATEGY_GUARD_FRONT){
                    moveStrategyGuardFront(GameScreen.GOAL_AREA_HEIGHT_P2, puck);
                } else{ //slow is not needed, become passive
                    paddle.update(moveX, moveY, false);
                }
            }
            //if above half court but below paddle height limit, track the puck
            else if (puck.getyPosition() > GameScreen.HALF_COURT &&
                    puck.getyPosition() < Globals.GAME_HEIGHT - GameScreen.PADDLE_HEIGHT_LIMIT){
                moveStrategyTrackPuck(puck);
            }
            //if in the hit area
            else {
                //hit the puck if the countdown is less than 0
                if (countdown < 0) {
                    moveStrategyHitPuck(2.5f * difficulty, puck);
                }
                //otherwise revert to strategy
                else{
                    if (strategy == STRATEGY_GUARD) {
                        moveStrategyGuard(GameScreen.GOAL_AREA_HEIGHT_P2, puck);
                    } else if (strategy == STRATEGY_GUARD_FRONT){
                        moveStrategyGuardFront(GameScreen.GOAL_AREA_HEIGHT_P2, puck);
                    } else if (strategy == STRATEGY_PASSIVE){
                        paddle.update(moveX, moveY, false);
                    }
                }
            }
        }
        //handle if bottom
        else{ //PLAYER 1

        }

    }

    /**
     * Moves towards the puck with the given speed and adjusts for difficulty
     * @param currentPosition - current position in any dimension
     * @param finalPosition - target position in any dimension
     * @param speed - speed at which ai can move
     * @return - new x position of the paddle based on AI
     */
    private float moveTowards(float currentPosition, float finalPosition, float speed){
        if (Math.abs(currentPosition - finalPosition) < speed * difficulty){
            return finalPosition;
        } else{
            if (currentPosition - finalPosition < 0){ //current position left of final position
                return currentPosition + speed * difficulty;
            } else{
                return currentPosition - speed * difficulty;
            }
        }
    }

    /**
     * Sets the difficulty.
     * Maximum difficulty is a 1
     * @param difficulty - difficulty setting, 0.1 < x < 1 where 1 is hardest
     */
    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
        if (difficulty > 1f){
            this.difficulty = 1f;
        }
        if (difficulty < 0.1){
            this.difficulty = 0.1f;
        }
    }

    /**
     * Resets the countdown when puck is hit
     */
    public void resetCountdown(){
        countdown = (int)(AI_COUNTDOWN_INTERVAL * (1.5f - difficulty));
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public float getDifficulty() {
        return difficulty;
    }


    /**
     * Moves the AI paddle to the goal area
     * @param puck - puck to move
     * TODO: have the puck move to the edge of the goal ... maybe a better defensive strategy
     */
    private void moveStrategyGuard(float yTarget, Puck puck){
        float moveX = moveTowards(paddle.getxPosition(), GameScreen.GOAL_AREA_CENTER,
                AI_GLOBAL_BASE_SPEED);
        float moveY = moveTowards(paddle.getyPosition(), yTarget,
                AI_GLOBAL_BASE_SPEED);

        //update the paddle
        paddle.update(moveX, moveY, true);
    }

    /**
     * Changes the AI strategies, including behavior, velocity, etc.
     */
    private void changeAIStrategies(){
        //generate new threshold
        float variance = (float)(Math.random() - 0.5) * 60;
        if (isTop){
            threshold_height = GameScreen.GOAL_AREA_HEIGHT_P2 + variance;
        } else{
            threshold_height = GameScreen.GOAL_AREA_HEIGHT_P1 + variance;
        }

        //generate new strategy
        float strategyChange = rand.nextFloat();
        if (Debug.AI){
            System.out.print(strategyChange + " ");
        }
        if (strategyChange > STRATEGY_GUARD_THRESHOLD * (1 - difficulty)){
            strategy = STRATEGY_GUARD;
            if (Debug.AI){
                System.out.println("GUARD | THRESHOLD " + STRATEGY_GUARD_THRESHOLD * (1 - difficulty));
            }
        } else if (strategyChange > STRATEGY_GUARD_FRONT_THRESHOLD * (1 - difficulty)){
            strategy = STRATEGY_GUARD_FRONT;
            if (Debug.AI){
                System.out.println("GUARD FRONT | THRESHOLD " + STRATEGY_GUARD_FRONT_THRESHOLD * (1 - difficulty));
            }
        } else if (strategyChange > STRATEGY_PASSIVE_THRESHOLD * (1 - difficulty)){
            strategy = STRATEGY_PASSIVE;
            if (Debug.AI){
                System.out.println("PASSIVE | THRESHOLD " + STRATEGY_PASSIVE_THRESHOLD * (1 - difficulty));
            }
        } else{
            strategy = STRATEGY_SLOW;
            if (Debug.AI){
                System.out.println("SLOW");
            }
        }

        //generate new attack value
        attackVelocity = (rand.nextFloat() + 0.5f) * (1 + 3 * difficulty);
    }

    /**
     * Moves the AI to the front of the goal area
     * @param puck - puck
     */
    private void moveStrategyGuardFront(float yTarget, Puck puck){
        float adjustMoveY = (GameScreen.HALF_COURT - yTarget) / 3.5f;
        moveStrategyGuard(yTarget + adjustMoveY, puck);
    }

    /**
     * Tracks the puck. Moes slightly faster
     * @param puck
     */
    private void moveStrategyTrackPuck(Puck puck){
        float moveX = moveTowards(paddle.getxPosition(), puck.getxPosition(),
                AI_GLOBAL_BASE_SPEED * 1.3f);
        float moveY = moveTowards(paddle.getyPosition(), threshold_height,
                AI_GLOBAL_BASE_SPEED * 1.3f);

        //update the paddle
        paddle.update(moveX, moveY, true);
    }

    private void moveStrategyHitPuck(float multiplier, Puck puck){
        float moveX = moveTowards(paddle.getxPosition(), puck.getxPosition(), AI_GLOBAL_BASE_SPEED * multiplier);
        float moveY = moveTowards(paddle.getyPosition(), puck.getyPosition(), AI_GLOBAL_BASE_SPEED * multiplier);

        //update the paddle
        paddle.update(moveX, moveY, true);
    }

}//end class AI
