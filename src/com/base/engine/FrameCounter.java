package com.base.engine;

public class FrameCounter {
    //TODO: Reuse nanosecond static constant in Time class
    private static final long FRAME_COUNTER = 1000000000;

    private static FrameCounter instance = null;

    private int frames, framesPassed;
    private long totalTime, lastTime;

    private FrameCounter() {
        frames = 0;
        framesPassed = 0;
        totalTime = 0;
        lastTime = System.nanoTime();
    }

    public static FrameCounter getInstance() {
        if(instance == null) {
            instance = new FrameCounter();
        }
        return instance;
    }

    //TODO: Reuse time variables from Time class instead of duplicating them here, if possible
    public void calculateFramerate() {
        long now = System.nanoTime();
        long passed = now - lastTime;
        lastTime = now;
        totalTime += passed;

        if(totalTime >= FRAME_COUNTER) {
            framesPassed = frames;
            System.out.println("FPS: " + frames + " " + Time.getDelta());
            totalTime = 0;
            frames = 0;
        }
        frames++;
    }
}