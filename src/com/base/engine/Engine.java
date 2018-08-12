package com.base.engine;

import com.base.engine.loop.*;
import com.base.engine.physics.Integration;
import com.base.engine.render.DisplaySettings;

import java.net.URL;

public class Engine implements Runnable {
    private static Engine engine;

    public GameState gameToRun;
    private Inputter inputter;
    private Renderer renderer;
    private Updater updater;
    private Interpolater interpolater;

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

        cleanUp();
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
        interpolater = new Interpolater();
    }

    public LoopType getGameLoopType() {
        return gameLoopType;
    }

    private void startGameLoop() {
        try {
            gameToRun.start();
        } catch(Exception ex) {
            //TODO: Should I close the game if there is a setup exception caught here? Like a shader failed to link?
            System.err.println("Game initialization error: " + ex.getLocalizedMessage());
        }

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
            case INTERPOLATED:
                startInterpolatedGameLoop();
            default:
                startFixedGameLoop();
                break;
        }
    }

    //The simplest game loop, least computationally expensive
    //Ideal if you can ensure your physics and display refresh rate match up
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

    //A more computationally expensive game loop that keeps the variable time delta in check
    //Calls a variable number of physics calculations per step to keep it in sync with the framerate
    //Does not play well with spiral of death situations where the physics can't keep up with the framerate and causes more physics steps to be run to try and keep up, exaggerating the problem
    private void startSemiFixedGameLoop() {
        Time.init();

        long updateTime = 0;

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            updateTime += Time.getFrameTime();

            inputter.getInput(gameToRun);

            while(updateTime > 0.0f) {
                updater.update(gameToRun, integrationType);
                updateTime -= Time.convertSecondsToNanoseconds(Time.getDelta());
                inputter.reset();
            }
            renderer.render(gameToRun);
            FrameCounter.getInstance().calculateFramerate();
        }
    }

    //Variation on the semi-fixed game loop that seperates the physics from the framerate entirely using a fixed time delta
    //Instead of using the leftover update time to run the physics with a variable time delta, we keep the leftovers and pass them to the next frame, keeing the physics consistent
    //this carry-over of leftover time can cause visual stutter of the physics simulations at certain framerates
    private void startFreedGameLoop() {
        Time.init();

        long updateTime = 0;

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            updateTime += Time.getFrameTime();

            inputter.getInput(gameToRun);

            while(updateTime >= Time.convertSecondsToNanoseconds(Time.getDelta())) {
                updater.update(gameToRun, integrationType);
                updateTime -= Time.convertSecondsToNanoseconds(Time.getDelta());
                inputter.reset();
            }
            renderer.render(gameToRun);

            FrameCounter.getInstance().calculateFramerate();
        }
    }

    //Computationally cheap game loop that uses a calculated delta time to compensate for fluctuations in framerate
    //A good solution for varying framerates until frames go very low or high. Then the physics will start to act up using the extreme values of the calculated time delta
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

    //Variation of the freed physics game loop that uses object states to create interpolated states for the renderer to use
    //Fixes the visual stutter of physics by using the interpolated state in the renderer instead of the object's true state
    private void startInterpolatedGameLoop() {
        Time.init();

        long updateTime = 0;

        while(!window.shouldClose() && !hasQuit) {
            Time.update();

            updateTime += Time.getFrameTime();

            inputter.getInput(gameToRun);

            while(updateTime >= Time.convertSecondsToNanoseconds(Time.getDelta())) {
                updater.update(gameToRun, integrationType);
                updateTime -= Time.convertSecondsToNanoseconds(Time.getDelta());
                inputter.reset();
            }
            float alpha = updateTime / Time.convertSecondsToNanoseconds(Time.getDelta());
            interpolater.interpolate(gameToRun, alpha);

            renderer.render(gameToRun);
            if(!DisplaySettings.getVsync()) {
                renderer.sync();
            }

            FrameCounter.getInstance().calculateFramerate();
        }
    }

    private void cleanUp() {
        gameToRun.cleanUp();
    }
}
