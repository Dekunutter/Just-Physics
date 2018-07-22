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
    private static boolean DEFAULT_CAP_FRAMERATE = true;
    private static int DEFAULT_TARGET_FRAMERATE = 60;

    private static int displayWidth, displayHeight;
    private static boolean isFullScreen, isVsyncEnabled, isFramerateCapped;
    private static int targetFramerate;

    public static void loadFromFile(URL filepath) {
        //TODO: Load settings from file
        loadDefaults();
    }

    public static void loadDefaults() {
        displayWidth = DEFAULT_WINDOW_WIDTH;
        displayHeight = DEFAULT_WINDOW_HEIGHT;
        isFullScreen = DEFAULT_FULLSCREEN;
        isVsyncEnabled = DEFAULT_VSYNC;
        isFramerateCapped = DEFAULT_CAP_FRAMERATE;
        targetFramerate = DEFAULT_TARGET_FRAMERATE;
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

    public static boolean isFramerateCapped() {
        return isFramerateCapped;
    }

    public static int getTargetFramerate() {
        return targetFramerate;
    }
}
