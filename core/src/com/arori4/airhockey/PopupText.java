package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 20-Apr-16.
 * Defines a popup text. A popup text will appear in the middle of the screen suddenly and then
 * fade away.
 * TODO: implement time for the text to stay opaque 100% for a while
 */
public class PopupText extends TextBox{

    private static final int DEFAULT_MAX_FADE  = 50;
    private static final int DEFAULT_HEIGHT = 300;

    private float mMaxFadeValue;
    private float mCurrentFadeValue;
    private boolean mDraw;


    /**
     * Creates a Popup Text.
     * @param text -  text to draw
     * @param font - font that is used to draw
     */
    public PopupText(String text, BitmapFont font) {
        super(text, font);

        //force the width to be the game width
        setWidth(Globals.GAME_WIDTH);
        setHeight(DEFAULT_HEIGHT);

        //set default delay
        mCurrentFadeValue = DEFAULT_MAX_FADE;
        mMaxFadeValue = DEFAULT_MAX_FADE;

        //place the popup text in the middle, after all relevant adjustments have been made
        setX(Globals.GAME_WIDTH / 2.0f - getWidth() / 2.0f);
        setY(Globals.GAME_HEIGHT / 2.0f - getHeight() / 2.0f);

        //set background color to transparent
        setForegroundColor(Color.CLEAR);
        setBorderColor(Color.CLEAR);
    }


    /**
     * Draws the popup text
     * @param context - batch to draw in
     * @param parentX - parent X, invalid because this draws at the center
     * @param parentY - parent Y, invalid because this draws at the center
     */
    @Override
    @Deprecated
    public void draw(SpriteBatch context, float parentX, float parentY) {
        //invalidate the two inputs
        parentX = parentY = 0;

        //only draw when permitted by the game engine
        if (mDraw) {
            //have the spriteBatch draw with transparency
            setTextColor(new Color(getTextColor().r, getTextColor().g,
                    getTextColor().b, mCurrentFadeValue / mMaxFadeValue));

            //now delegate to super class with new parent values of 0
            super.draw(context, parentX, parentY);
        }
    }


    /**
     * Draws the popup text. Preferred method to use.
     * @param context - sprite batch to draw to
     */
    public void draw(SpriteBatch context){
        draw(context, 0, 0);
    }


    @Override
    /**
     * Updates the popup text.
     */
    public void update() {
        //fade
        if (mDraw) {
            mCurrentFadeValue--;
        } else { //when done, no longer fade
            mCurrentFadeValue = mMaxFadeValue;
            mDraw = false;
        }
    }


    /**
     * Starts the popup text.
     */
    public void start(){
        mDraw = true;
        mCurrentFadeValue = mMaxFadeValue;
    }


    public float getMaxFadeValue() {
        return mMaxFadeValue;
    }

    public void setMaxFadeValue(float maxFadeValue) {
        mMaxFadeValue = maxFadeValue;
    }
}//end class PopupText
