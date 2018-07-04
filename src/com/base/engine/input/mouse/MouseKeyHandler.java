package com.base.engine.input.mouse;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MouseKeyHandler extends GLFWMouseButtonCallback {
    private boolean leftPressed, leftWasPressed, rightPressed, rightWasPressed;

    @Override
    public void invoke(long window, int button, int action, int mods) {
        leftPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
        rightPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;

        if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
            leftWasPressed = false;
        }
        if(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) {
            rightWasPressed = false;
        }
    }

    public boolean isLeftDown() {
        return leftPressed;
    }

    public boolean isRightDown() {
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
    }
}
