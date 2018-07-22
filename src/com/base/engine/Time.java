package com.base.engine;

import com.base.engine.loop.LoopType;

public class Time {
    public static int NANOSECONDS_TO_SECONDS = 1000000000;

    private static long startTime, currentTime, lastTime;
    private static float frameTime, previousDelta;

    public static long getTime() {
        return System.nanoTime();
    }

    public static float getDelta() {
        switch(Engine.getInstance().getGameLoopType()) {
            case FIXED:
                return 0.01666666667f;
                //return 0.66f;
            case SEMI_FIXED:
                return Math.min(Time.convertNanosecondsToSeconds(frameTime), 0.01666666667f);
            case FREED:
                return 0.01666666667f;
            case VARIABLE:
                return Time.convertNanosecondsToSeconds(frameTime);
            case INTERPOLATED:
                return 0.01666666667f;
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

        //cap physics at 4 loops per second on interpolation loop types
        if(Engine.getInstance().getGameLoopType() == LoopType.INTERPOLATED) {
            if(frameTime > Time.convertSecondsToNanoseconds(0.25f)) {
                frameTime = Time.convertSecondsToNanoseconds(0.25f);
            }
        }
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

    public static long getCurrentTime() {
        return currentTime;
    }

    public static float convertNanosecondsToSeconds(float nanoTime) {
        return nanoTime / NANOSECONDS_TO_SECONDS;
    }

    public static float convertSecondsToNanoseconds(float time) {
        return time * NANOSECONDS_TO_SECONDS;
    }
}
