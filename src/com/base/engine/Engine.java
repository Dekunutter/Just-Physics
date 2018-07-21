package com.base.engine;

import com.base.engine.loop.*;
import com.base.engine.physics.Integration;
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

    private LoopType gameLoopType;
    private Integration integrationType;

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
        start(gameToRun, LoopType.FIXED, Integration.EXPLICIT);
    }

    public void start(GameState gameToRun, LoopType gameLoopType) {
        start(gameToRun, gameLoopType, Integration.EXPLICIT);
    }

    public void start(GameState gameToRun, LoopType gameloopType, Integration integrationType) {
        this.gameToRun = gameToRun;
        this.gameLoopType = gameloopType;
        this.integrationType = integrationType;

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
        initUpdater();

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
        window = new Window(gameToRun.getTitle(), DisplaySettings.getDisplayDimensions(), DisplaySettings.getVsync());
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

    private void initUpdater() {
        updater = new Updater();
    }

    public LoopType getGameLoopType() {
        return gameLoopType;
    }

    private void startGameLoop() {
        switch(gameLoopType) {
            case FIXED:
                startFixedGameLoop();
                break;
            case SEMI_FIXED:
                startSemiFixedGameLoop();
                break;
            case FREED:
                startFreedGameLoop();
                break;
            case VARIABLE:
                startVariableGameLoop();
                break;
            default:
                startFixedGameLoop();
                break;
        }
    }

    private void startFixedGameLoop() {
        Time.init();

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            inputter.getInput(gameToRun);
            updater.update(gameToRun, integrationType);
            renderer.render(gameToRun);

            FrameCounter.getInstance().calculateFramerate();
        }
    }

    private void startSemiFixedGameLoop() {
        Time.init();

        long updateTime = 0;

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            updateTime += Time.getFrameTime();

            inputter.getInput(gameToRun);

            while(updateTime > 0.0f) {
                updater.update(gameToRun, integrationType);
                updateTime -= Time.getDelta();
                inputter.reset();
            }
            renderer.render(gameToRun);

            FrameCounter.getInstance().calculateFramerate();
        }
    }

    private void startFreedGameLoop() {
        Time.init();

        long updateTime = 0;

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            updateTime += Time.getFrameTime();

            inputter.getInput(gameToRun);

            //TODO: Swap time calculations to milliseconds and then these doesn't need to compensate from nanoseconds anymorer? What accuracy do I lose doing that though?
            while(updateTime >= LOOP_STEP) {
                updater.update(gameToRun, integrationType);
                updateTime -= LOOP_STEP;
                inputter.reset();
            }
            renderer.render(gameToRun);

            FrameCounter.getInstance().calculateFramerate();
        }
    }

    private void startVariableGameLoop() {
        Time.init();

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            inputter.getInput(gameToRun);
            updater.update(gameToRun, integrationType);
            renderer.render(gameToRun);

            FrameCounter.getInstance().calculateFramerate();
        }
    }
}
