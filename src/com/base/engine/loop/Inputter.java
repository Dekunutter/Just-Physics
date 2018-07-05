package com.base.engine.loop;

import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.input.keyboard.Keyboard;

public class Inputter {
    public static Inputter inputter;

    public Inputter() {
        Engine.window.setupWindowInput();
    }

    public void getInput(GameState game) {
        game.getInput();
    }

    public void reset()
    {
        Keyboard.updateKeyEvents();
    }
}
