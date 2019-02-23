package com.base.engine.input.mouse;

import com.base.engine.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {
    private static Vector2f displayVector;

    private static MouseKeyHandler buttonCallback;
    private static MousePositionHandler positionCallback;
    private static MouseEnterHandler enterCallback;
    private static MouseScrollHandler scrollCallback;

    public static void init(Window window) {
        displayVector = new Vector2f();

        buttonCallback = new MouseKeyHandler();
        positionCallback = new MousePositionHandler();
        enterCallback = new MouseEnterHandler();
        scrollCallback = new MouseScrollHandler();

        glfwSetMouseButtonCallback(window.getHandle(), buttonCallback);
        glfwSetCursorPosCallback(window.getHandle(), positionCallback);
        glfwSetCursorEnterCallback(window.getHandle(), enterCallback);
        glfwSetScrollCallback(window.getHandle(), scrollCallback);

        glfwSetInputMode(window.getHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public static Vector2f getDisplayVector() {
        return displayVector;
    }

    public static void getInput(Window window) {
        displayVector = new Vector2f();

        double deltaX = positionCallback.getPosition().x - positionCallback.getPreviousPosition().x;
        double deltaY = positionCallback.getPosition().y - positionCallback.getPreviousPosition().y;
        boolean rotateX = deltaX != 0;
        boolean rotateY = deltaY != 0;
        if(rotateX) {
            displayVector.y = (float) deltaX;
        }
        if(rotateY) {
            displayVector.x = (float) deltaY;
        }
    }

    public static void update() {
        scrollCallback.resetScrollState();
        positionCallback.setPreviousPosition();
    }

    public static Vector2d getPositionOnScreen() {
        return positionCallback.getPosition();
    }

    public static boolean isButtonHeld(int buttonCode) {
        return buttonCallback.isButtonHeld(buttonCode);
    }

    public static boolean isButtonPressed(int buttonCode) {
        return buttonCallback.isButtonPressed(buttonCode);
    }

    public static boolean isScrolling() {
        return scrollCallback.getScrollDirection() != 0;
    }

    public static double getScroll() {
        return scrollCallback.getScrollDirection();
    }

    public static void freeze() {
        positionCallback.freeze();
    }

    public static void unfreeze() {
        positionCallback.unfreeze();
    }
}
