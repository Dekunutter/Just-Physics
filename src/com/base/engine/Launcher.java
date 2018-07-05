package com.base.engine;

import com.base.game.Game;

public class Launcher {
    public static void main(String[] args) {
        GameState game = new Game();

        Engine engine = Engine.getInstance();
        engine.start(game);
    }
}
