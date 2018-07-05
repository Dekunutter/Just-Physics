package com.base.engine;

import com.base.engine.loop.GameLoop;

public abstract class GameState implements GameLoop {
    protected String gameTitle = "My Game";

    @Override
    public void getInput() {

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {

    }

    public String getTitle() {
        return gameTitle;
    }
}
