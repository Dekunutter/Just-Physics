package com.base.engine.input.mouse;

import com.base.engine.input.InputEvent;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class MouseKeyHandler extends GLFWMouseButtonCallback {
    public Map<Integer, InputEvent> events = new HashMap<>();

    @Override
    public void invoke(long window, int button, int action, int mods) {
        events.put(button, new InputEvent(button, action));
    }

    public boolean isButtonHeld(int buttonCode) {
        InputEvent event = events.get(buttonCode);
        return event != null && event.getAction() > GLFW_RELEASE;
    }

    public boolean isButtonPressed(int buttonCode) {
        InputEvent event = events.get(buttonCode);
        return event != null && event.getAction() == GLFW_PRESS && event.isFresh();
    }
}
