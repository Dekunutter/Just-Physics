package com.base.engine;

public enum State {
    INTRO, GAME;

    public State getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
