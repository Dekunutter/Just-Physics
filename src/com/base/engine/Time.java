package com.base.engine;

public class Time {
    private static long startTime, currentTime, lastTime;
    private static float previousDelta;

    public static long getTime() {
        return System.nanoTime();
    }

    public static float getDelta() {
        switch(Engine.getInstance().getGameLoopType()) {
            case FIXED:
                //TODO: Research the desire to have a timestep closer to representing the actually time between update calls in the game loop. This is the engine loopstep converted to seconds for real-time step calculations
                return 0.01666666667f;
                //return 0.66f;
            case VARIABLE:
                //TODO: Return actual variable time step calculated from the game loop earlier and update previous delta
                return 0.66f;
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
    }

    public static void init() {
        startTime = getTime();

        currentTime = getTime();
        lastTime = getTime();
    }

    public static long timeSinceLaunch() {
        return currentTime - startTime;
    }
}
