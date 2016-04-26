package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 21-Apr-16.
 * Defines a button for the GUI
 */
public class Button extends GUIComponent{

    private String text;
    private BitmapFont font;
    private GlyphLayout layout;

    /**
     * Creates a button with the texture and text
     * @param val - texture
     * @param text - text to display
     * @param font - font to display
     */
    public Button(Texture val, String text, BitmapFont font){
        if (val == null){
            System.err.println("ERROR: Button texture is null");
            throw new NullPointerException("Button texture is null");
        }
        if (font == null){
            System.err.println("ERROR: Button font is null");
            throw new NullPointerException("Button " + text + " font is null");
        }

        setTexture(val);
        this.text = text;
        this.font = font;
        layout = new GlyphLayout();
        layout.setText(font, text);

        //initialize size
        setWidth(getTexture().getWidth());
        setHeight(getTexture().getHeight());
    }


    /**
     * Used to set relative location. Use Position class values.
     * Dependent on texture size
     * Use 0 to indicate no change
     * @param xRel - x relative location to entire screen
     * @param yRel - y relative location to entire screen
     */
    public void setRelativeLocation(int xRel, int yRel) {
        if (xRel == Position.CENTER) {
            setX(Globals.GAME_WIDTH / 2 - getWidth() / 2);
        }

        if (yRel == Position.CENTER) {
            setY(Globals.GAME_WIDTH / 2 + getHeight() / 2);
        }
    }

    /**
     * Draws the button
     * @param context - SpriteBatch to draw with
     */
    public void draw(SpriteBatch context){
        //draw the button graphic
        context.draw(getTexture(), getX(), getY(), getWidth(), getHeight());
        //draw the font in the centered location
        font.draw(context, text, getX() + getWidth()/2 - layout.width/2, getY() + getHeight()/2 + layout.height/2);
    }


    /**
     * Checks whether the button has been pressed
     * @param xClick - x location of press
     * @param yClick - y location of press
     * @return - true if within button
     */
    public boolean isPressed(float xClick, float yClick){
        return xClick > getX() && xClick < getX() + getTexture().getWidth() &&
                yClick > getY() && yClick < getY() + getTexture().getHeight();
    }

}//end class Button
