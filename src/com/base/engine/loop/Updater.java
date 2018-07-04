package com.base.engine.loop;

import com.base.engine.Engine;

public class Updater {
    public static Updater updater;

    public Updater() {

    }

    public void update() {
        switch(Engine.state) {
            case INTRO:
                break;
            case GAME:
                break;
        }
    }
}
