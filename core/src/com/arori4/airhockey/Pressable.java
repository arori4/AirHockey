package com.arori4.airhockey;

/**
 * Created by Christopher Cabreros on 29-Apr-16.
 * Implement for any pressable. A pressable is a GUIComponent that can respond to click mouse
 * events.
 * TODO: possibly extend pressable to any mouse action (emulate swing MouseListener)
 */
public interface Pressable {

    void isPressed(float xClick, float yClick);

    void setListener(ActionListener listener);

}
