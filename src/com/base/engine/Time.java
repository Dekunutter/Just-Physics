package com.base.engine;

public class Time {
    private static long startTime, currentTime, lastTime;
    private static float frameTime, previousDelta;

    public static long getTime() {
        return System.nanoTime();
    }

    public static float getDelta() {
        switch(Engine.getInstance().getGameLoopType()) {
            case FIXED:
                //TODO: Research the desire to have a timestep closer to representing the actually time between update calls in the game loop. This is the engine loopstep converted to seconds for real-time step calculations
                return 0.01666666667f;
                //return 0.66f;
            case SEMI_FIXED:
                return Math.min(frameTime, 0.01666666667f);
            case FREED:
                return 0.01666666667f;
            case VARIABLE:
                return frameTime;
            default:
                return 0.66f;
        }
    }

    public static float getPreviousDelta() {
        switch(Engine.getInstance().getGameLoopType()) {
            case VARIABLE:
                return previousDelta;
            default:
                return getDelta();
        }
    }

    public static void update() {
        lastTime = currentTime;
        currentTime = getTime();

        frameTime = currentTime - lastTime;
    }

    public static void init() {
        startTime = getTime();

        currentTime = getTime();
        lastTime = getTime();
    }

    public static float getFrameTime() {
        return frameTime;
    }

    public static long timeSinceLaunch() {
        return currentTime - startTime;
    }
}
