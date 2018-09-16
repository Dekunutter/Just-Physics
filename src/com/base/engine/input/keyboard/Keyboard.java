package com.base.engine.input.keyboard;

import com.base.engine.Window;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class Keyboard {
    private static KeyboardHandler keyCallback;

    public static void init(Window window) {
        keyCallback = new KeyboardHandler();
        glfwSetKeyCallback(window.getHandle(), keyCallback);
    }

    public static boolean isKeyHeld(int keycode) {
        return keyCallback.isKeyHeld(keycode);
    }

    public static boolean isKeyPressed(int keycode) {
        return keyCallback.isKeyPressed(keycode);
    }

    public static void updateKeyEvents() {
        keyCallback.updateKeyEvents();
    }
}
