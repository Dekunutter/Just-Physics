package com.base.engine.input.keyboard;

import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyboardHandler extends GLFWKeyCallback {
    public Map<Integer, KeyEvent> events = new HashMap<>();

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        events.put(key, new KeyEvent(key, action));
    }

    public boolean isKeyHeld(int keycode) {
        KeyEvent event = events.get(keycode);
        return event != null && event.getAction() > GLFW_RELEASE;
    }

    public boolean isKeyPressed(int keycode) {
        KeyEvent event = events.get(keycode);
        return event != null && event.getAction() == GLFW_PRESS && event.isFresh();
    }

    public void updateKeyEvents() {
        for(Map.Entry pair : events.entrySet()) {
            ((KeyEvent)pair.getValue()).stagnate();
        }
    }
}
