package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Christopher Cabreros on 21-Apr-16.
 * Defines a button for the GUI
 */
public class Button {

    private Texture texture;
    private String text;
    private BitmapFont font;
    private GlyphLayout layout;

    private int xLocation;
    private int yLocation;

    private int width;
    private int height;

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
        texture = val;
        this.text = text;
        this.font = font;
        layout = new GlyphLayout();
        layout.setText(font, text);

        //initialize location
        xLocation = 0;
        yLocation = 0;
        //initialize size
        width = texture.getWidth();
        height = texture.getHeight();
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
            xLocation = AirHockeyGame.GAME_WIDTH / 2 - width / 2;
        }

        if (yRel == Position.CENTER) {
            yLocation = AirHockeyGame.GAME_WIDTH / 2 + height / 2;
        }
    }

    /**
     * Draws the button
     * @param context - SpriteBatch to draw with
     */
    public void draw(SpriteBatch context){
        //draw the button graphic
        context.draw(texture, getxLocation(), getyLocation(), width, height);
        //draw the font in the centered location
        float textxLocation = getxLocation() + width/2 - layout.width/2;
        float textyLocation = getyLocation() + height/2 + layout.height/2;
        font.draw(context, text, textxLocation, textyLocation);
    }


    /**
     * Checks whether the button has been pressed
     * @param xClick - x location of press
     * @param yClick - y location of press
     * @return - true if within button
     */
    public boolean isPressed(float xClick, float yClick){
        return xClick > xLocation && xClick < xLocation + texture.getWidth() &&
                yClick > yLocation && yClick < yLocation + texture.getHeight();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }
}//end class Button
