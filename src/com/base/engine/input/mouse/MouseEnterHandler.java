package com.base.engine.input.mouse;

import org.lwjgl.glfw.GLFWCursorEnterCallback;

public class MouseEnterHandler extends GLFWCursorEnterCallback {
    private boolean inWindow;

    @Override
    public void invoke(long window, boolean entered) {
        inWindow = entered;
    }

    public boolean isInWindow() {
        return inWindow;
    }
}
