package com.base.engine;

import com.base.engine.loop.GameLoop;
import com.base.engine.physics.Integration;

public abstract class GameState implements GameLoop {
    protected String gameTitle = "My Game";

    public abstract void start();

    @Override
    public void getInput() {

    }

    @Override
    public void update(Integration integrationType) {
        //TODO: Pass integration type into each physics body that needs to update
    }

    @Override
    public void interpolate(float alpha) {

    }

    @Override
    public void render() {

    }

    public String getTitle() {
        return gameTitle;
    }

    @Override
    public void cleanUp() {

    }
}
