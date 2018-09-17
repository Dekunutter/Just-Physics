package com.base.engine.input.mouse;

import com.base.engine.input.InputEvent;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class MouseKeyHandler extends GLFWMouseButtonCallback {
    //private boolean leftPressed, leftWasPressed, rightPressed, rightWasPressed;
    public Map<Integer, InputEvent> events = new HashMap<>();

    @Override
    public void invoke(long window, int button, int action, int mods) {
        /*leftPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
        rightPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;

        if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
            leftWasPressed = false;
        }
        if(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) {
            rightWasPressed = false;
        }*/

        events.put(button, new InputEvent(button, action));
    }

    /*public boolean isLeftHeld() {
        return leftPressed;
    }

    public boolean isRightHeld() {
        return rightPressed;
    }

    public boolean isLeftPressed() {
        if(leftPressed && !leftWasPressed) {
            leftWasPressed = true;
            return true;
        }
        return false;
    }

    public boolean isRightPressed() {
        if(rightPressed && !rightWasPressed) {
            rightWasPressed = true;
            return true;
        }
        return false;
    }*/

    public boolean isButtonHeld(int buttonCode) {
        InputEvent event = events.get(buttonCode);
        return event != null && event.getAction() > GLFW_RELEASE;

        /*if(buttonCode == GLFW_MOUSE_BUTTON_1) {
            return isLeftHeld();
        } else if(buttonCode == GLFW_MOUSE_BUTTON_2) {
            return isRightHeld();
        }
        return false;*/
    }

    public boolean isButtonPressed(int buttonCode) {
        InputEvent event = events.get(buttonCode);
        return event != null && event.getAction() == GLFW_PRESS && event.isFresh();

       /* if(buttonCode == GLFW_MOUSE_BUTTON_1) {
            return isLeftPressed();
        } else if(buttonCode == GLFW_MOUSE_BUTTON_2) {
            return isRightPressed();
        }
        return false;*/
    }
}
