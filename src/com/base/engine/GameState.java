package com.base.engine;

import com.base.engine.loop.GameLoop;
import com.base.engine.physics.Integration;
import com.base.engine.physics.collision.CollisionDetection;

public abstract class GameState implements GameLoop {
    protected String gameTitle = "My Game";
    protected CollisionDetection collisionType = CollisionDetection.BASIC_SAT;

    public abstract void start() throws Exception;

    @Override
    public void getInput() {

    }

    @Override
    public void update(Integration integrationType) {

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

    public void setCollisionDetectionType(CollisionDetection collisionType) {
        this.collisionType = collisionType;
    }

    @Override
    public void cleanUp() {

    }
}
