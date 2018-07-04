package com.base.engine.loop;

import com.base.engine.Engine;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;

public class Inputter {
    public static Inputter inputter;

    public Inputter() {
        Engine.window.setupWindowInput();
        Keys.getInstance();
    }

    public void getInput() {
        switch(Engine.state) {
            case INTRO:
                break;
            case GAME:
                break;
        }
    }

    public void reset() {
        Keyboard.updateKeyEvents();
    }
}
