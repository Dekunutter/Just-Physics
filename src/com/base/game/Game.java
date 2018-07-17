package com.base.game;

import com.base.engine.GameState;
import com.base.engine.physics.Body;
import com.base.engine.physics.Integration;
import org.joml.Vector3f;

public class Game extends GameState {
    private Body testBody;

    public Game() {
        gameTitle = "Just Physics";

        testBody = new Body();
        testBody.addForce(new Vector3f(10.0f, 0, 0));
        testBody.setMass(1.0f);
    }

    public void update(Integration integrationType) {
        testBody.advancePhysics(integrationType);
    }
}
