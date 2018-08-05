package com.base.game;

import com.base.engine.GameState;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.physics.Integration;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Game extends GameState {
    private TestObject testObject;
    private float colour, colourDirection;

    public Game() {
        gameTitle = "Just Physics";
    }

    @Override
    public void start() {
        testObject = new TestObject();

        colour = 0.0f;
        colourDirection = 0.0f;
    }

    @Override
    public void getInput() {
        if(Keyboard.isKeyDown(Keys.getInstance().up)) {
            colourDirection = 1.0f;
        }
        if(Keyboard.isKeyDown(Keys.getInstance().down)) {
            colourDirection = -1.0f;
        }
        if(!Keyboard.isKeyDown(Keys.getInstance().up) && !Keyboard.isKeyDown(Keys.getInstance().down)){
            colourDirection = 0.0f;
        }
    }

    @Override
    public void update(Integration integrationType) {
        testObject.update(integrationType);

        colour += colourDirection * 0.01f;
        if(colour > 1.0f) {
            colour = 1.0f;
        } else if(colour < 0.0f) {
            colour = 0.0f;
        }
    }

    @Override
    public void render() {
        glClearColor(colour, colour, colour, 0.0f);

        testObject.render();
    }

    public void interpolate(float alpha) {
        testObject.interpolate(alpha);
    }

    @Override
    public void cleanUp() {
        testObject.cleanUp();
    }
}
