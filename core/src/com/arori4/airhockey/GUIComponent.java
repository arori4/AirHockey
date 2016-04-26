package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Christopher Cabreros on 26-Apr-16.
 * Defines a GUIComponent that can
 */
public abstract class GUIComponent {

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


    /**
     * Default constructor
     */
    public GUIComponent(){
        mForegroundColor = Color.WHITE;
        mBorderColor = Color.BLACK;

        mX = 0;
        mY = 0;
        mWidth = 50;
        mHeight = 50;
    }


    public Texture getTexture() {
        return mTexture;
    }

    public void setTexture(Texture texture) {
        mTexture = texture;
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
        mWidth = width;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        mHeight = height;
    }
}
