package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 21-Apr-16.
 * Defines a button for the GUI.
 * Buttons should have a secondary texture for being pressed.
 */
public class Button extends TextBox implements Pressable{

    //defaults
    private static final int DEFAULT_CLICK_DELAY = 10; //measured in seconds/60

    private String mActionCommand;
    private Texture mPressedTexture;
    private boolean mIsPressed;

    private ActionListener mListener;
    private int clickCountdown;

    /**
     * Creates a button with text and font
     * @param text - text to display
     * @param font - font to display
     */
    public Button(String text, BitmapFont font){
        super(text, font);
    }


    /**
     * Draws the button.
     * Changes texture if pressed
     * @param context - SpriteBatch to draw with
     */
    public void draw(SpriteBatch context, float parentX, float parentY){
        super.draw(context, parentX, parentY);

        //draw the new image if pressed
        if (mIsPressed){
            if (mPressedTexture != null){
                context.draw(mPressedTexture, getX() + parentX, getY() + parentY);
            } else{
                System.err.println("ERROR: Button with action command " + mActionCommand + " does " +
                        "not have a background texture.");
            }
        }
    }

    /**
     * Updates the button.
     * Must always be called.
     */
    public void update(){
        super.update();

        //lower click countdown if pressed.
        //this simulates a slight delay which is needed
        if (mIsPressed){
            clickCountdown--;
        }
        if (clickCountdown <= 0) {
            clickCountdown = DEFAULT_CLICK_DELAY;
            mIsPressed = false;

            //Call the listener's on item clicked command if available
            if (mListener != null) {
                mListener.onItemClicked(mActionCommand);
            } else{ //do nothing if there is no listener
                System.err.println("ERROR: Button with action command " + mActionCommand + " does " +
                 "not have a listener.");
            }
        }
    }


    /**
     * Checks whether the button has been pressed
     * @param xClick - x location of press
     * @param yClick - y location of press
     */
    public void isPressed(float xClick, float yClick){
        mIsPressed = xClick > getX() && xClick < getX() + getTexture().getWidth() &&
                yClick > getY() && yClick < getY() + getTexture().getHeight();
    }


    public String getActionCommand() {
        return mActionCommand;
    }

    public void setActionCommand(String actionCommand) {
        mActionCommand = actionCommand;
    }

    public Texture getPressedTexture() {
        return mPressedTexture;
    }

    public void setPressedTexture(Texture pressedTexture) {
        mPressedTexture = pressedTexture;
    }

    public void setListener(ActionListener listener) {
        mListener = listener;
    }
}//end class Button
