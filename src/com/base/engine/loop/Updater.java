package com.base.engine.loop;

import com.base.engine.GameState;

public class Updater {
    public static Updater updater;

    public Updater() {

    }

    public void update(GameState game)
    {
        game.update();
    }
}
