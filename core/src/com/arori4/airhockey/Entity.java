package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Christopher Cabreros on 19-Apr-16.
 * Defines anything that is affected by physics within the game
 */
public abstract class Entity {

    private float xPosition;
    private float yPosition;

    private float xVelocity;
    private float yVelocity;

    private float width;
    private float height;

    private int leftBound;
    private int rightBound;
    private int topBound;
    private int bottomBound;

    private Color color;
    private Color previousColor;

    public static final Color DEFAULT_COLOR = Color.GRAY;

    /**
     * Creates an entity with a width and height
     * @param width - width of the entity
     * @param height - height of the entity
     */
    public Entity(float width, float height){
        this.width = width;
        this.height = height;

        this.xVelocity = 0;
        this.yVelocity = 0;

        //set color to default color
        color = DEFAULT_COLOR;
    }

    /**
     * Draw method in abstract class is only for debug purposes.
     */
    public void draw(SpriteBatch context){
        if (AirHockeyGame.DEBUG){

        }
    }

    /**
     * Handles the bounds for a ball in one dimension
     * Run it a second time for the other dimension
     * @param lowBound - lower bound for movement
     * @param highBound - upper bound for movement
     * @param position - current position, x or y
     * @param radius - radius of the object
     * @return - the new position, adjusted for bounds
     */
    protected float handleBounds(int lowBound, int highBound, float position, float radius){
        if (position - radius/2 < lowBound ){
            return lowBound + radius/2;
        }
        else if (position + radius/2 > highBound){
            return highBound - radius/2;
        }
        else{
            return position;
        }
    }


    public void setBounds(int left, int right, int top, int down){
        leftBound = left;
        rightBound = right;
        topBound = top;
        bottomBound = down;
    }

    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(int leftBound) {
        this.leftBound = leftBound;
    }

    public int getRightBound() {
        return rightBound;
    }

    public void setRightBound(int rightBound) {
        this.rightBound = rightBound;
    }

    public int getTopBound() {
        return topBound;
    }

    public void setTopBound(int topBound) {
        this.topBound = topBound;
    }

    public int getBottomBound() {
        return bottomBound;
    }

    public void setBottomBound(int bottomBound) {
        this.bottomBound = bottomBound;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getPreviousColor() {
        return previousColor;
    }

    public void setPreviousColor(Color previousColor) {
        this.previousColor = previousColor;
    }
}//end class Entity
