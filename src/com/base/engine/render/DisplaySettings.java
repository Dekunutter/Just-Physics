package com.base.engine.render;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWVidMode;

import java.net.URL;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;

public class DisplaySettings {
    private static int DEFAULT_WINDOW_WIDTH = 1280;
    private static int DEFAULT_WINDOW_HEIGHT = 900;
    private static boolean DEFAULT_FULLSCREEN = false;
    private static boolean DEFAULT_VSYNC = true;

    private static int displayWidth, displayHeight;
    private static boolean isFullScreen, isVsyncEnabled;

    public static void loadFromFile(URL filepath) {
        loadDefaults();
    }

    public static void loadDefaults() {
        displayWidth = DEFAULT_WINDOW_WIDTH;
        displayHeight = DEFAULT_WINDOW_HEIGHT;
        isFullScreen = DEFAULT_FULLSCREEN;
        isVsyncEnabled = DEFAULT_VSYNC;
    }

    public static Vector2i getDisplayDimensions() {
        return new Vector2i(displayWidth, displayHeight);
    }

    public static GLFWVidMode getMonitorResolution() {
        return glfwGetVideoMode(glfwGetPrimaryMonitor());
    }

    public static boolean getVsync() {
        return isVsyncEnabled;
    }
}
