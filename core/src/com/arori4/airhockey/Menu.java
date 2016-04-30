package com.arori4.airhockey;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 29-Apr-16.
 * A menu is a special instance of a GUIContainer. This container is always drawn at 0, 0 and
 * is the size of the screen.
 */
public class Menu extends GUIContainer {


    /**
     * Constructor
     */
    public Menu(){
        super();

        //set location explicitly to the 'origin'
        setX(0);
        setY(0);

        //set width and height to the game size
        setWidth(Globals.GAME_WIDTH);
        setHeight(Globals.GAME_HEIGHT);
    }


    @Override
    @Deprecated
    /**
     * Deprecated version of draw
     */
    public void draw(SpriteBatch context, float parentX, float parentY) {
        super.draw(context, 0, 0);
    }


    /**
     * Draws the menu at the origin.
     * This is the preferred usage of menu
     * @param context - SpriteBatch to draw
     */
    public void draw(SpriteBatch context){
        draw(context, 0, 0);
    }

}//end class Menu
