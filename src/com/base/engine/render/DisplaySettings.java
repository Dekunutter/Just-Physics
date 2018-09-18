package com.base.engine.render;

import com.base.engine.FileLoader;
import com.base.engine.OperatingSystem;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWVidMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;

public class DisplaySettings {
    private static String SETTINGS_FILE = "settings.dat";

    private static String KEY_WINDOW_WIDTH = "window width";
    private static String KEY_WINDOW_HEIGHT = "window height";
    private static String KEY_FULLSCREEN = "fullscreen";
    private static String KEY_VSYNC = "vsync";
    private static String KEY_CAP_FRAMERATE = "framerate cap";
    private static String KEY_TARGET_FRAMERATE = "framerate target";

    private static int DEFAULT_WINDOW_WIDTH = 1280;
    private static int DEFAULT_WINDOW_HEIGHT = 900;
    private static boolean DEFAULT_FULLSCREEN = false;
    private static boolean DEFAULT_VSYNC = true;
    private static boolean DEFAULT_CAP_FRAMERATE = true;
    private static int DEFAULT_TARGET_FRAMERATE = 60;

    private static int displayWidth, displayHeight;
    private static boolean isFullScreen, isVsyncEnabled, isFramerateCapped;
    private static int targetFramerate;

    public static void loadFromFile(OperatingSystem os, String gameTitle) {
        try {
            String filePath;
            switch (os) {
                case WINDOWS:
                    filePath = System.getenv("APPDATA") + File.separator + gameTitle + File.separator + SETTINGS_FILE;
                    readFromSettingsFile(filePath);
                    break;
                case MAC:
                    //NOTE: Untested. Coded on a Windows machine
                    filePath = File.separator + "var" + File.separator + "lib" + File.separator + gameTitle + File.separator + SETTINGS_FILE;
                    readFromSettingsFile(filePath);
                    break;
                default:
                    loadDefaults();
                    break;
            }
        } catch(IOException ex) {
            System.err.print("Video settings were not found. Loading defaults. ");
            ex.printStackTrace();
            loadDefaults();
        }
    }

    public static void loadDefaults() {
        displayWidth = DEFAULT_WINDOW_WIDTH;
        displayHeight = DEFAULT_WINDOW_HEIGHT;
        isFullScreen = DEFAULT_FULLSCREEN;
        isVsyncEnabled = DEFAULT_VSYNC;
        isFramerateCapped = DEFAULT_CAP_FRAMERATE;
        targetFramerate = DEFAULT_TARGET_FRAMERATE;
    }

    private static void readFromSettingsFile(String filePath) throws IOException {
        boolean isNew = FileLoader.createFileIfMissing(filePath);
        if(isNew) {
            populateWithDefaults(filePath);
        }

        displayWidth = readSetting(filePath, KEY_WINDOW_WIDTH, DEFAULT_WINDOW_WIDTH);
        displayHeight = readSetting(filePath, KEY_WINDOW_HEIGHT, DEFAULT_WINDOW_HEIGHT);
        isFullScreen = readSetting(filePath, KEY_FULLSCREEN, DEFAULT_FULLSCREEN);
        isVsyncEnabled = readSetting(filePath, KEY_VSYNC, DEFAULT_VSYNC);
        isFramerateCapped = readSetting(filePath, KEY_CAP_FRAMERATE, DEFAULT_CAP_FRAMERATE);
        targetFramerate = readSetting(filePath, KEY_TARGET_FRAMERATE, DEFAULT_TARGET_FRAMERATE);
    }

    private static void populateWithDefaults(String filePath) throws IOException {
        writeSetting(filePath, KEY_WINDOW_WIDTH , String.valueOf(DEFAULT_WINDOW_WIDTH));
        writeSetting(filePath, KEY_WINDOW_HEIGHT, String.valueOf(DEFAULT_WINDOW_HEIGHT));
        writeSetting(filePath, KEY_FULLSCREEN, String.valueOf(DEFAULT_FULLSCREEN));
        writeSetting(filePath, KEY_VSYNC, String.valueOf(DEFAULT_VSYNC));
        writeSetting(filePath, KEY_CAP_FRAMERATE, String.valueOf(DEFAULT_CAP_FRAMERATE));
        writeSetting(filePath, KEY_TARGET_FRAMERATE, String.valueOf(DEFAULT_TARGET_FRAMERATE));
    }

    private static int readSetting(String filePath, String key, int defaultValue) throws IOException {
        int result;
        String defaultString = String.valueOf(defaultValue);
        String readValue = readSetting(filePath, key, String.valueOf(defaultValue));

        try {
            result = Integer.parseInt(readValue);
        } catch(NumberFormatException ex) {
            System.err.println("Invalid settings value for " + key + " found. Setting to default value");
            overwriteSetting(filePath, key, defaultString);
            result = defaultValue;
        }

        return result;
    }

    private static boolean readSetting(String filePath, String key, boolean defaultValue) throws IOException {
        boolean result;
        String defaultString = String.valueOf(defaultValue);
        String readValue = readSetting(filePath, key, defaultString);

        if(!readValue.equalsIgnoreCase("true") && !readValue.equalsIgnoreCase("false")) {
            System.err.println("Invalid settings value for " + key + " found. Setting to default value");
            overwriteSetting(filePath, key, defaultString);
            result = defaultValue;
        } else {
            result = Boolean.parseBoolean(readValue);
        }

        return result;
    }

    private static String readSetting(String filePath, String key, String defaultValue) throws IOException {
        BufferedReader br = FileLoader.openFileForReading(filePath);
        String settingValue = "";
        boolean settingFound = false;

        for(String line = br.readLine(); line != null; line = br.readLine()) {
            if(line.startsWith(key)) {
                settingValue = line.substring(line.indexOf("=") + 1);
                settingFound = true;
                break;
            }
        }
        br.close();

        if(!settingValue.isEmpty()) {
            return settingValue;
        } else {
            if(!settingFound) {
                writeSetting(filePath, key, defaultValue, true);
            } else {
                overwriteSetting(filePath, key, defaultValue);
            }
            return defaultValue;
        }
    }

    private static void writeSetting(String filePath, String key, String defaultValue) throws IOException {
        writeSetting(filePath, key, defaultValue, true);
    }

    private static void writeSetting(String filePath, String key, String defaultValue, boolean appendMode) throws IOException {
        BufferedWriter bw = FileLoader.openFileForWriting(filePath, appendMode);
        bw.write(key + "=" + defaultValue + System.getProperty("line.separator"));
        bw.close();
    }

    private static void overwriteSetting(String filePath, String key, String defaultValue) throws IOException {
        BufferedReader br = FileLoader.openFileForReading(filePath);
        List<String> lines = new ArrayList<>();
        for(String line = br.readLine(); line != null; line = br.readLine()) {
            if(line.startsWith(key)) {
                lines.add(key + "=" + defaultValue + System.getProperty("line.separator"));
            } else {
                lines.add(line + System.getProperty("line.separator"));
            }
        }
        br.close();

        BufferedWriter bw = FileLoader.openFileForWriting(filePath, false);
        for(int i = 0; i < lines.size(); i++) {
            bw.write(lines.get(i));
        }
        bw.close();

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
