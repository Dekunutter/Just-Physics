package com.base.game;

import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.Time;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.input.mouse.MouseCursor;
import com.base.engine.physics.Integration;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Game extends GameState {
    private float colour, colourDirection;
    private World world;

    public Game() {
        gameTitle = "Just Physics";
    }

    @Override
    public void start() throws Exception {
        colour = 0.0f;
        colourDirection = 0.0f;

        world = new World();
    }

    @Override
    public void getInput() {
        MouseCursor.getInput(Engine.window);

        if(Keyboard.isKeyDown(Keys.getInstance().up)) {
            colourDirection = 1.0f;
        }
        if(Keyboard.isKeyDown(Keys.getInstance().down)) {
            colourDirection = -1.0f;
        }
        if(!Keyboard.isKeyDown(Keys.getInstance().up) && !Keyboard.isKeyDown(Keys.getInstance().down)) {
            colourDirection = 0.0f;
        }

        world.getInput();
    }

    @Override
    public void update(Integration integrationType) {
        MouseCursor.update();

        world.update(integrationType);

        colour += colourDirection * Time.getDelta();
        if(colour > 1.0f) {
            colour = 1.0f;
        } else if(colour < 0.0f) {
            colour = 0.0f;
        }
    }

    @Override
    public void render() {
        glClearColor(colour, colour, colour, 0.0f);

        world.render();
    }

    public void interpolate(float alpha) {
        world.interpolate(alpha);
    }

    @Override
    public void cleanUp() {
        world.cleanUp();
    }
}
