package com.arori4.airhockey;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher Cabreros on 29-Apr-16.
 * Defines all containers. A container is a GUIComponent that contains other components.
 */
public abstract class GUIContainer extends GUIComponent implements Pressable{

    //list
    private List<GUIComponent> arrComponents;
    private ActionListener mListener;

    /**
     * Constructor
     */
    public GUIContainer(){
        arrComponents = new ArrayList<GUIComponent>();
    }


    @Override
    /**
     * Draws the container and all contained components.
     */
    public void draw(SpriteBatch context, float parentX, float parentY) {
        super.draw(context, parentX, parentY);

        //draw every single component
        int numComponents = arrComponents.size();
        for (int index = 0; index < numComponents; index++){
            arrComponents.get(index).draw(context, getX() + parentX, getY() + parentY);
        }
    }

    @Override
    /**
     * Updates the container and all contained components
     */
    public void update() {
        //update every component
        int numComponents = arrComponents.size();
        for(int index = 0; index < numComponents; index++){
            arrComponents.get(index).update();
        }
    }


    /**
     * Checks whether the container has been pressed
     * @param xClick - x location of press
     * @param yClick - y location of press
     */
    public void isPressed(float xClick, float yClick){
        //delegate to every component that is a button
        int numComponents = arrComponents.size();
        for (int index = 0; index < numComponents; index++){
            GUIComponent nextComponent = arrComponents.get(index);

            //delegate to any pressable. adjust for location of the container
            if (nextComponent instanceof Pressable){
                ((Pressable) nextComponent).isPressed(xClick + getX(), yClick + getY());
            }
        }
    }


    /**
     * Adds a component into the container.
     * Components will retain their x and y coordinates, but will be drawn relative to the
     * origin of the container added into.
     * @param component - component to add
     */
    public void addComponent(GUIComponent component) {
        //null check
        if (component != null) {
            arrComponents.add(component);
        }
    }


    public void setListener(ActionListener listener){
        mListener = listener;
    }


}//end class GUIContainer
