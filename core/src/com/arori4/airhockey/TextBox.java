package com.arori4.airhockey;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Christopher Cabreros on 26-Apr-16.
 * Defines a text box. A text box is a GUI object that displays text with the font specified.
 * By default, the text box will resize to the width for the text to be one line.
 * It is recommended to set a target width manually.
 * TODO: The implementation of this class needs a lot of review
 */
public class TextBox extends GUIComponent {

    //defaults
    private int DEFAULT_ALIGNMENT = Position.CENTER;
    private Color DEFAULT_BOX_COLOR = Color.CLEAR;
    private Color DEFAULT_TEXT_COLOR = Color.WHITE;
    private int DEFAULT_PADDING = 10;

    //members
    private String mText;
    private BitmapFont mFont;
    private GlyphLayout mLayout;
    private int mTextAlignment;


    /**
     * Creates a text box.
     * Sets it transparent and aligned to the size of the text
     * @param text - text to display
     * @param font - font for the text box
     */
    public TextBox(String text, BitmapFont font){
        super();

        //set variables. setFont delegates to a nullptr check
        //set a default text color
        mLayout = new GlyphLayout(); //for safety. must be done before we set the font
        mFont = font; //do not do call to other method right now TODO: fix this
        font.setColor(DEFAULT_TEXT_COLOR);
        mText = text;

        //set default text alignment
        mTextAlignment = DEFAULT_ALIGNMENT;

        //create the custom layout
        calibrateText(true);

        //make the text box transparent
        setForegroundColor(DEFAULT_BOX_COLOR);
        setBorderColor(DEFAULT_BOX_COLOR);
    }


    @Override
    /**
     * Draws the TextBox
     */
    public void draw(SpriteBatch context, float parentX, float parentY) {
        //delegate to super class for the colors
        super.draw(context, parentX, parentY);

        //draw based on configuration
        if (mTextAlignment == Position.CENTER) {
            mFont.draw(context, mText,
                    getX() + parentX,
                    getY() + parentY + mLayout.height / 2 + getHeight() / 2,
                    getWidth(), 1, true);
        } else{
            //error message
            System.err.println("ERROR: Text Alignment " + mTextAlignment + " for button " +
            mText + " is an invalid alignment.");
            System.err.println("Drawing in center by default.");

            //draw at center
            mFont.draw(context, mText,
                    getX() + parentX + getWidth() / 2.0f - mLayout.width / 2.0f,
                    getY() + parentY + getHeight() / 2.0f + mLayout.height / 2.0f);
        }

    }

    @Override
    public void update() {

    }


    /**
     * Resize the button and refreshes the layout, if the font or text has changed.
     * @param resize - whether to resize the text box or not
     */
    private void calibrateText(boolean resize){
        mLayout.reset();
        mLayout.setText(mFont, mText);

        if (resize){
            //do a fake draw to get the glyph
            SpriteBatch fakeBatch =  new SpriteBatch();
            fakeBatch.begin();
            mLayout = mFont.draw(fakeBatch, mText,
                    0, 0,
                    getWidth(), 1, true);
            fakeBatch.end();

            //resize the text box to a new size
            setHeight(mLayout.height + DEFAULT_PADDING);
        }
    }


    public String getText() {
        return mText;
    }

    public void setWidth(float width){
        super.setWidth(width);
        //must also resize
        calibrateText(true);
    }

    public void setText(String text) {
        mText = text;
        calibrateText(false);
    }

    public BitmapFont getFont() {
        return mFont;
    }


    /**
     * Sets the font, and recalibrates the text
     * Button will not be resized
     * @param font - font to change
     */
    public void setFont(BitmapFont font) {
        if (font == null){
            System.err.println("ERROR: Button font is null");
            throw new NullPointerException("Button " + mText + " font is null");
        }
        mFont = font;
        calibrateText(false);
    }

    public int getTextAlignment() {
        return mTextAlignment;
    }

    public void setTextAlignment(int textAlignment) {
        mTextAlignment = textAlignment;
    }

    public Color getTextColor() {
        return mFont.getColor();
    }

    public void setTextColor(Color textColor) {
        mFont.setColor(textColor);
    }
}

