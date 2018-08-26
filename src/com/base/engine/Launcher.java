package com.base.engine;

import com.base.engine.loop.LoopType;
import com.base.engine.physics.Integration;
import com.base.game.Game;

public class Launcher {
    public static void main(String[] args) {
        GameState game = new Game();

        Debug.enableDebug();
        //Debug.enablePolygonMode();

        Engine engine = Engine.getInstance();
        engine.start(game, LoopType.INTERPOLATED, Integration.SEMI_IMPLICIT);
    }
}
