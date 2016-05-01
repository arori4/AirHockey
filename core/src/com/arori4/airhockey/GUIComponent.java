package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 26-Apr-16.
 * Defines a GUIComponent that can
 */
public abstract class GUIComponent {

    //defaults
    private static final int DEFAULT_BORDER_SIZE = 2;
    public static Texture DEFAULT_TEXTURE;

    //texture
    private Texture mTexture;
    //color
    private Color mForegroundColor;
    private Color mBorderColor;

    //measurements
    private float mX;
    private float mY;
    private float mWidth;
    private float mHeight;
    private boolean mFlippedX;
    private boolean mFlippedY;

    //border
    private float mBorderSize;


    /**
     * Default constructor
     */
    public GUIComponent(){
        //colors should never manifset in non-container components
        mForegroundColor = Color.WHITE;
        mBorderColor = Color.BLACK;

        //default values
        mX = 0;
        mY = 0;
        mWidth = 50;
        mHeight = 50;
        mBorderSize = DEFAULT_BORDER_SIZE;
        mFlippedX = false;
        mFlippedY = false;

        //check for default texture
        if (DEFAULT_TEXTURE == null){
            throw new NullPointerException("GUIComponent.DEFAULT_TEXTURE has not been " +
                    "initialized yet.");
        }
    }


    /**
     * Draws the GUIComponent with its own specified color.
     * Does not preserve color information. You are responsible for changing this before drawing
     * other components
     * @param context - SpriteBatch that will contain drawing information
     * @param parentX - x location of parent
     * @param parentY - y location of parent
     */
    public void draw(SpriteBatch context, float parentX, float parentY){
        //All components are responsible for changing their own color

        //Create coordinates for drawing based on reversals
        float xPosition;
        float yPosition;
        float width;
        float height;
        if (mFlippedX){
            width = -mWidth;
            xPosition = mX + mWidth;
        } else{
            width = mWidth;
            xPosition = mX;
        }

        if (mFlippedY){
            height = -mHeight;
            yPosition = mY + mHeight;
        } else{
            height = mHeight;
            yPosition = mY;
        }

        //draw border if it exists
        if (mBorderSize != 0) {
            context.setColor(mBorderColor);
            context.draw(DEFAULT_TEXTURE,
                    xPosition + parentX - mBorderSize / 2.0f, yPosition + parentY - mBorderSize / 2.0f,
                    width + mBorderSize, height + mBorderSize);
        }

        //draw foreground, based on the color and whether the texture exists or not
        context.setColor(mForegroundColor);
        if (mTexture != null){
            context.draw(mTexture,
                    xPosition + parentX, yPosition + parentY,
                    width, height);
        } else{
            context.draw(DEFAULT_TEXTURE,
                    xPosition + parentX, yPosition + parentY,
                    width, height);
        }
    }


    /**
     * Updates the component within the update loop
     */
    public abstract void update();


    public Texture getTexture() {
        return mTexture;
    }


    /**
     * Sets the texture to the specified texture.
     * Also sets the color to draw the texture without any disortions
     * @param texture - texture to draw for the button
     */
    public void setTexture(Texture texture) {
        mTexture = texture;
        setForegroundColor(Color.WHITE);
    }

    public Color getForegroundColor() {
        return mForegroundColor;
    }

    public void setForegroundColor(Color backgroundColor) {
        mForegroundColor = backgroundColor;
    }

    public Color getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(Color borderColor) {
        mBorderColor = borderColor;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        if (width < 0) {
            mWidth = -width;
            mFlippedX = true;
        } else {
            mWidth = width;
            mFlippedX = false;
        }
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        if (height < 0) {
            mHeight = -height;
            mFlippedY = true;
        } else {
            mHeight = height;
            mFlippedY = false;
        }
    }

    public float getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(float borderSize) {
        mBorderSize = borderSize;
    }
}
