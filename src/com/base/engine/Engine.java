package com.base.engine;

import com.base.engine.loop.*;
import com.base.engine.render.DisplaySettings;

import java.net.URL;

public class Engine implements Runnable {
    private static final float LOOP_STEP = 16666666.6667f;
    private static final long FRAME_COUNTER = 1000000000;

    private static Engine engine;

    public GameState gameToRun;
    private Inputter inputter;
    private Renderer renderer;
    private Updater updater;

    public int framesPassed;
    public boolean hasQuit;

    public static Window window;
    private final Thread gameLoopThread;

    private final Type gameLoopType = Type.FIXED;

    public static Engine getInstance() {
        if(engine == null) {
            engine = new Engine();
        }
        return engine;
    }

    private Engine() {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
    }

    public void start(GameState gameToRun) {
        this.gameToRun = gameToRun;

        String osName = System.getProperty("os.name");
        if(osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        framesPassed = 30;
        hasQuit = false;

        checkVideoSettings();

        initDisplay();
        initOpenGL();
        initOpenAL();
        initKeyboard();

        startGameLoop();
    }

    private void checkVideoSettings() {
        URL url = Launcher.class.getClassLoader().getResource("settings/settings.txt");
        if(url == null) {
            System.err.println("Video settings were not found");
            DisplaySettings.loadDefaults();
        } else {
            DisplaySettings.loadFromFile(url);
        }
    }

    private void initDisplay() {
        window = new Window(Constants.GAME_TITLE, DisplaySettings.getDisplayDimensions(), DisplaySettings.getVsync());
        window.init();
    }

    private void initOpenGL() {
        renderer = new Renderer();
    }

    private void initOpenAL() {
        //TODO: Soundmanager
    }

    private void initKeyboard() {
        inputter = new Inputter();
    }

    public Type getGameLoopType() {
        return gameLoopType;
    }

    private void startGameLoop() {
        Time.init();

        int frames = 0;
        long lastTime = System.nanoTime();
        long totalTime = 0;
        long updateTime = 0;

        Updater updater = new Updater();

        while(!window.shouldClose() && !hasQuit) {
            long now = System.nanoTime();
            long passed = now - lastTime;
            lastTime = now;
            totalTime += passed;
            updateTime += passed;

            inputter.getInput(gameToRun);

            while(updateTime >= LOOP_STEP) {
                framesPassed = frames;
                updater.update(gameToRun);
                updateTime -= LOOP_STEP;
                inputter.reset();
            }
            renderer.render(gameToRun);

            if(totalTime >= FRAME_COUNTER) {
                framesPassed = frames;
                System.out.println("FPS: " + frames + " " + Time.getDelta());
                totalTime = 0;
                frames = 0;
            }
            frames++;

            Time.update();
        }
    }
}
