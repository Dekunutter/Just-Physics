package com.base.engine.input.keyboard;

import com.base.engine.Window;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class Keyboard {
    private static KeyboardHandler keyCallback;

    public static void init(Window window) {
        keyCallback = new KeyboardHandler();
        glfwSetKeyCallback(window.getHandle(), keyCallback);
    }

    public static boolean isKeyDown(int keycode) {
        return keyCallback.isKeyDown(keycode);
    }

    public static boolean isKeyDown(ArrayList<Integer> keycodes) {
        return keyCallback.isKeyDown(keycodes);
    }

    public static boolean isKeyPressed(int keycode) {
        return keyCallback.isKeyPressed(keycode);
    }

    public static boolean isKeyPressed(ArrayList<Integer> keycodes) {
        return keyCallback.isKeyPressed(keycodes);
    }

    public static void updateKeyEvents() {
        keyCallback.updateKeyEvents();
    }
}
