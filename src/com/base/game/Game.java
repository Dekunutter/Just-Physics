package com.base.game;

import com.base.engine.Camera;
import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.input.mouse.MouseCursor;
import com.base.engine.physics.Integration;
import com.base.engine.render.Attenuation;
import com.base.engine.render.lighting.PointLight;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Game extends GameState {
    private TestObject testObject;
    private float colour, colourDirection;
    private Vector3f ambientLight;
    private PointLight pointLight;
    private float specularPower;

    public Game() {
        gameTitle = "Just Physics";
    }

    @Override
    public void start() throws Exception {
        testObject = new TestObject();

        colour = 0.0f;
        colourDirection = 0.0f;

        ambientLight = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f lightColour = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f lightPosition = new Vector3f(0.0f, 0.0f, 1.0f);
        float lightIntensity = 10.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        Attenuation pointAttenuation = new Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(pointAttenuation);
        specularPower = 10f;
    }

    @Override
    public void getInput() {
        MouseCursor.getInput(Engine.window);

        Camera.getInstance().getInput();

        if(Keyboard.isKeyDown(Keys.getInstance().up)) {
            colourDirection = 1.0f;
        }
        if(Keyboard.isKeyDown(Keys.getInstance().down)) {
            colourDirection = -1.0f;
        }
        if(!Keyboard.isKeyDown(Keys.getInstance().up) && !Keyboard.isKeyDown(Keys.getInstance().down)){
            colourDirection = 0.0f;
        }

        testObject.getInput();
    }

    @Override
    public void update(Integration integrationType) {
        MouseCursor.update();

        Camera.getInstance().update();

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

        testObject.setAmbientLight(ambientLight);
        testObject.setPointLight(pointLight);
        testObject.setSpecularPower(specularPower);
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
