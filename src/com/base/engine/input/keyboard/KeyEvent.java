package com.base.engine.input.keyboard;

public class KeyEvent {
    private final int key, action;
    private boolean isFresh;

    public KeyEvent(int key, int action) {
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
