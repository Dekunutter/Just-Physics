package com.base.engine.loop;

import com.base.engine.physics.Integration;

public interface GameLoop {
    void getInput();
    void update(Integration integrationType);
    void interpolate(float alpha);
    void render();
}
