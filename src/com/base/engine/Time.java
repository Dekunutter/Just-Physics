package com.base.engine;

public class Time {
    private static long currentTime, lastTime;

    public static long getTime() {
        return System.nanoTime();
    }

    public static float getDelta() {
        switch(Engine.getGameLoopType()) {
            case FIXED:
                return 0.66f;
            case LOOSE:
                return 0.66f;
            default:
                return 0.66f;
        }
    }

    public static void update() {
        lastTime = currentTime;
        currentTime = getTime();
    }

    public static void init() {
        currentTime = getTime();
        lastTime = getTime();
    }
}
