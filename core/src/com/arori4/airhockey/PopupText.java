package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 20-Apr-16.
 * Defines a popup text
 */
public class PopupText {

    private Texture texture;
    private static final int MAX_FADE  = 50;
    private float current_fade = 0;

    private Color color;

    /**
     * Creates a popup text with the texture specified
     * @param val - texture to apply to popup text
     */
    public PopupText(Texture val){
        if (val == null){
            System.err.println("ERROR: PopupText texture is null");
            throw new NullPointerException("PopupText texture is null");
        }
        //set default values
        texture = val;
        color = new Color(1, 1, 1, 1);
    }

    /**
     * Draws and updates the popup text
     * @param context - SpriteBatch to draw into
     * @param location - location index to draw text
     */
    public void draw(SpriteBatch context, int location){
        if (location == Position.CENTER){
            //have the spritebatch draw with transparency
            Color oldColor = context.getColor();
            context.setColor(color.r, color.g, color.b, current_fade/MAX_FADE);

            context.draw(texture, AirHockeyGame.GAME_WIDTH/2 - texture.getWidth()/2,
                AirHockeyGame.GAME_HEIGHT/2 - texture.getHeight()/2);

            //reset color back
            context.setColor(oldColor);
        }

        //fade
        if (current_fade > 0){
            current_fade--;
        }
    }

    /**
     * Resets the popup text
     */
    public void reset(){
        current_fade = MAX_FADE;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}//end class PopupText
