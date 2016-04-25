package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;


/**
 * Created by Christopher Cabreros on 19-Apr-16.
 */
public class Puck extends Entity {

    private Circle geometry;
    private Texture puckTexture;

    public static final float PUCK_RADIUS = 50;
    public static final float PUCK_MASS = 1;


    public Puck(Texture texture){
        super(PUCK_RADIUS*2, PUCK_RADIUS*2);

        //initialize collision handling geometry
        geometry = new Circle();
        geometry.setRadius(PUCK_RADIUS/2);
        //set position in superclass
        setxPosition(geometry.x);
        setyPosition(geometry.y);
        //set constants
        puckTexture = texture;
    }

    /**
     * Draws the puck on the screen
     * @param context - sprite batch that will draw this
     */
    public void draw(SpriteBatch context){
        super.draw(context);

        //set the new color
        setPreviousColor(context.getColor());
        context.setColor(getColor());

        //we will draw based on geometry location, however we can easily do it for others
        context.draw(puckTexture,
                     getxPosition() - PUCK_RADIUS/2, getyPosition() - PUCK_RADIUS/2,
                   PUCK_RADIUS, PUCK_RADIUS);

        //restore original color
        context.setColor(getPreviousColor());
    }

    /**
     * Updates the position of the puck
     * Also updates the geometry location variables
     */
    public void update(){
        setxPosition(getxPosition() + getxVelocity());
        setyPosition(getyPosition() + getyVelocity());
        geometry.setX(getxPosition());
        geometry.setY(getyPosition());
    }

    /**
     * Checks whether the puck collides with the paddle
     * @param paddle - paddle to check collision with
     * @return - true if paddle collides with puck, false otherwise
     */
    public boolean collidesWithPaddle(Paddle paddle){
        //delegate to geometric collision
        //also check whether paddle has collided earlier
        return geometry.overlaps(paddle.getGeometry()) && paddle.canCollide();
    }

}//end class Puck
