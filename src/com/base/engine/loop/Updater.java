package com.base.engine.loop;

import com.base.engine.GameState;
import com.base.engine.physics.Integration;

public class Updater {
    public static Updater updater;

    public Updater() {

    }

    public void update(GameState game, Integration integrationType)
    {
        game.update(integrationType);
    }
}
