package com.base.engine.loop;

import com.base.engine.GameState;

public class Interpolater {
    public static Interpolater interpolater;

    public Interpolater() {

    }

    public void interpolate(GameState game, float alpha) {
        game.interpolate(alpha);
    }
}
