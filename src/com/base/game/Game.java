package com.base.game;

import com.base.engine.GameState;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.physics.body.Body;
import com.base.engine.physics.Integration;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Game extends GameState {
    private Body testBody;
    private float colour, colourDirection;

    public Game() {
        gameTitle = "Just Physics";

        testBody = new Body();
        testBody.addForce(new Vector3f(10.0f, 0, 0));
        testBody.setMass(1.0f);

        colour = 0.0f;
        colourDirection = 0.0f;
    }

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

    public void update(Integration integrationType) {
        testBody.updatePreviousState();
        testBody.advancePhysics(integrationType);

        colour += colourDirection * 0.01f;
        if(colour > 1.0f) {
            colour = 1.0f;
        } else if(colour < 0.0f) {
            colour = 0.0f;
        }
    }

    public void render() {
        glClearColor(colour, colour, colour, 0.0f);
    }

    public void interpolate(float alpha) {
        testBody.interpolate(alpha);
    }
}
