package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

/**
 * Created by Christopher Cabreros on 20-Apr-16.
 */
public class Paddle extends Entity{

    private Circle geometry;
    private Texture paddleTexture;
    private boolean isHeld;

    public static final float PADDLE_RADIUS = 125;
    public static final float PADDLE_MASS = 20;

    public static final int COLLIDE_TIME = 10;
    public static final float FRICTION = 0.90f;
    private int collide;

    /**
     * Creates the paddle
     * @param texture - texture to have the paddle use
     */
    public Paddle(Texture texture, float xLocation, float yLocation){
        super(PADDLE_RADIUS*2, PADDLE_RADIUS*2);

        if (texture == null){ //nullpointer check
            System.err.println("ERROR: PopupText texture is null");
            throw new NullPointerException("PopupText texture is null");
        }

        //initialize collision handling geometry
        geometry = new Circle();
        geometry.setRadius(PADDLE_RADIUS/2);
        geometry.x = xLocation;
        geometry.y = yLocation;
        //set position in superclass
        setxPosition(geometry.x);
        setyPosition(geometry.y);

        //set constants
        paddleTexture = texture;
        collide = -1;
        isHeld = false; //set to true initially
    }


    /**
     * Draws the puck on the screen
     * @param context - sprite batch that will draw this
     */
    public void draw(SpriteBatch context){
        super.draw(context);

        //change color of puck when held
        //TODO: this is a bug. change implementation to multiplicative
        setPreviousColor(context.getColor());
        if (isHeld){
            context.setColor(getColor());
        } else{ //draw slightly darker when not selected
            context.setColor(getColor().r * 0.7f, getColor().g * 0.7f, getColor().b * 0.7f,
                    getColor().a * 0.8f);
        }

        //we will draw based on geometry location
        context.draw(paddleTexture,
                getxPosition() - PADDLE_RADIUS/2, getyPosition() - PADDLE_RADIUS/2,
                PADDLE_RADIUS, PADDLE_RADIUS);

        //set the color back
        context.setColor(getPreviousColor());
    }


    /**
     * Sets the new x position and y position of the paddle.
     * Handling of collision with the game board should be handled elsewhere
     * @param newX - new x position based on user input
     * @param newY - new y position based on user input
     */
    public void update(float newX, float newY, boolean pressed){
        //subtract collision counter
        collide--;

        //handle being held or not
        if (!isHeld && pressed){
            if (geometry.contains(newX, newY)){
                isHeld = true;
            }
        }
        else if (!pressed){ //TODO: Evaluate if this is the right condition
            isHeld = false;
            //move with x and y velocity
            setNewPositions(getxPosition() + getxVelocity(), getyPosition() + getyVelocity());
            //do a little bit of friction
            setxVelocity(getxVelocity() * FRICTION);
            setyVelocity(getyVelocity() * FRICTION);
        }
        //only do if held and pressed
        else if (isHeld) { //TODO: evaluate whether we need to have newer x calculated up here
            setxVelocity(newX - getxPosition());
            setyVelocity(newY - getyPosition());

            //generate and set new positions
            setNewPositions(newX, newY);
        }
    }

    public Circle getGeometry() {
        return geometry;
    }

    public void setGeometry(Circle geometry) {
        this.geometry = geometry;
    }

    public void resetCollision(){
        collide = COLLIDE_TIME;
    }

    /**
     * Checks whether the paddle can collide again, based on timing
     * @return - true if paddle can collide
     */
    public boolean canCollide(){
        return collide < 0;
    }

    public boolean isHeld() {
        return isHeld;
    }

    public void setHeld(boolean held) {
        isHeld = held;
    }

    /**
     * Helper method for update
     */
    private void setNewPositions(float newX, float newY){
        //calcualte based on object bounds
        float newerX = handleBounds(getLeftBound(), getRightBound(), newX, PADDLE_RADIUS);
        float newerY = handleBounds(getBottomBound(), getTopBound(), newY, PADDLE_RADIUS);

        //set new positions
        setxPosition(newerX);
        setyPosition(newerY);
        geometry.x = newerX;
        geometry.y = newerY;
    }
}//end class Paddle