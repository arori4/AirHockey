package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 22-Apr-16.
 * Defines the PuckTrail. The puck trail is a group of entities drawn as the puck moves around.
 */
public class PuckTrail extends Entity {

    private Texture texture;
    private float count;
    private boolean redraw;
    public static final int PUCK_TRAIL_LENGTH = (int)(GameScreen.NUM_PUCK_TRAILS_X * 1.5);


    /**
     * Creates an entity with a width and height
     *
     * @param width  - width of the entity
     * @param height - height of the entity
     */
    public PuckTrail(Texture texture, float width, float height, int count) {
        super(width, height);
        this.texture = texture;

        this.count = count;
        redraw = true;
    }


    /**
     * Updates the puck trail
     */
    public void update(){
        //reset the puck trail when it is
        if (count < PUCK_TRAIL_LENGTH / 1.5){
            count = PUCK_TRAIL_LENGTH;
            redraw = true;
        }
        else{
            redraw = false;
            count--;
        }
    }


    /**
     * Draws the PuckTrail object
     * @param context - sprite batch to draw in
     */
    public void draw(SpriteBatch context){
        super.draw(context);

        //set the color to be fading based on the count of the puck
        setPreviousColor(context.getColor());
        context.setColor(getColor().r, getColor().g, getColor().b,
                getColor().a * (count / PUCK_TRAIL_LENGTH));

        //set the radius to be a fraction based on puck trail length
        float radius = Puck.PUCK_RADIUS * count / PUCK_TRAIL_LENGTH;

        //we will draw based on geometry location, however we can easily do it for others
        context.draw(texture,
                getxPosition() - radius/2, getyPosition() - radius/2,
                radius, radius);

        //reset the color to original color
        context.setColor(getPreviousColor());
    }


    public boolean isRedraw() {
        return redraw;
    }

    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }


}//end class PuckTrail
