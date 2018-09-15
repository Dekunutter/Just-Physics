package com.base.engine;

public class FrameCounter {
    private static FrameCounter instance = null;

    private int frames, framesPassed;
    private long totalTime;

    private FrameCounter() {
        frames = 0;
        framesPassed = 0;
        totalTime = 0;
    }

    public static FrameCounter getInstance() {
        if(instance == null) {
            instance = new FrameCounter();
        }
        return instance;
    }

    public void calculateFramerate() {
        totalTime += Time.getFrameTime();

        if(totalTime >= Time.NANOSECONDS_TO_SECONDS) {
            framesPassed = frames;
            System.out.println("FPS: " + frames + " " + Time.getDelta());
            totalTime = 0;
            frames = 0;
        }
        frames++;
    }
}
