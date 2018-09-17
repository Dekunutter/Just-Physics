package com.base.engine.input;

public class InputEvent {
    private final int key, action;
    private boolean isFresh;

    public InputEvent(int key, int action) {
        this.key = key;
        this.action = action;
        isFresh = true;
    }

    public int getKey() {
        return key;
    }

    public int getAction() {
        return action;
    }

    public boolean isFresh() {
        return isFresh;
    }

    public void stagnate() {
        isFresh = false;
    }
}
