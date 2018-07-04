package com.base.engine.input.mouse;

import org.lwjgl.glfw.GLFWScrollCallback;

public class MouseScrollHandler extends GLFWScrollCallback {
    private double scrollX, scrollY;

    public MouseScrollHandler() {
        scrollX = 0;
        scrollY = 0;
    }

    @Override
    public void invoke(long window, double offsetX, double offsetY) {
        scrollX = offsetX;
        scrollY = offsetY;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollDirection() {
        return scrollY;
    }

    public void resetScrollState() {
        scrollY = 0;
    }
}
