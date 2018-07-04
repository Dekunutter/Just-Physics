package com.base.engine;

import com.base.engine.loop.Inputter;
import com.base.engine.loop.Renderer;
import com.base.engine.loop.Type;
import com.base.engine.loop.Updater;
import com.base.engine.render.DisplaySettings;

import java.net.URL;

public class Engine implements Runnable {
    private static final float LOOP_STEP = 16666666.6667f;
    private static final long FRAME_COUNTER = 1000000000;

    public static State state;
    private static Inputter inputter;
    private static Renderer renderer;

    public static int framesPassed;
    public static boolean hasQuit;

    public static Window window;
    private final Thread gameLoopThread;

    private static final Type gameLoopType = Type.FIXED;

    public Engine() {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if(osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        //This is the game state that we wish to start up in
        state = State.GAME;
        framesPassed = 30;
        hasQuit = false;

        checkVideoSettings();

        initDisplay();
        initOpenGL();
        initOpenAL();
        initKeyboard();

        startGameLoop();
    }

    private static void checkVideoSettings() {
        URL url = Launcher.class.getClassLoader().getResource("settings/settings.txt");
        if(url == null) {
            System.err.println("Video settings were not found");
            DisplaySettings.loadDefaults();
        } else {
            DisplaySettings.loadFromFile(url);
        }
    }

    private static void initDisplay() {
        window = new Window(Constants.GAME_TITLE, DisplaySettings.getDisplayDimensions(), DisplaySettings.getVsync());
        window.init();
    }

    private static void initOpenGL() {
        renderer = new Renderer();
    }

    private static void initOpenAL() {
        //TODO: Soundmanager
    }

    private static void initKeyboard() {
        inputter = new Inputter();
    }

    public static Type getGameLoopType() {
        return gameLoopType;
    }

    private static void startGameLoop() {
        Time.init();

        int frames = 0;
        long lastTime = System.nanoTime();
        long totalTime = 0;
        long updateTime = 0;

        Updater.updater = new Updater();

        while(!window.shouldClose() && !hasQuit) {
            long now = System.nanoTime();
            long passed = now - lastTime;
            lastTime = now;
            totalTime += passed;
            updateTime += passed;

            inputter.getInput();

            while(updateTime >= LOOP_STEP) {
                framesPassed = frames;
                Updater.updater.update();
                updateTime -= LOOP_STEP;
                inputter.reset();
            }
            renderer.render();

            if(totalTime >= FRAME_COUNTER) {
                framesPassed = frames;
                System.out.println("FPS: " + frames + " " + Time.getDelta());
                totalTime = 0;
                frames = 0;
            }
            frames++;
        }
    }
}
