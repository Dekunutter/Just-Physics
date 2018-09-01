package com.base.game;

import com.base.engine.Camera;
import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.Time;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.input.mouse.MouseCursor;
import com.base.engine.physics.Integration;
import com.base.engine.render.Attenuation;
import com.base.engine.render.lighting.DirectionalLight;
import com.base.engine.render.lighting.PointLight;
import com.base.engine.render.lighting.SpotLight;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Game extends GameState {
    private TestObject testObject;
    private float colour, colourDirection;
    private Vector3f ambientLight;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private SpotLight spotLight;
    private float spotLightDirection;
    private float lightAngle, spotAngle, spotInc;
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
        float lightIntensity = 10;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        Attenuation pointAttenuation = new Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(pointAttenuation);

        directionalLight = new DirectionalLight(lightColour, new Vector3f(-1, 0, 0), 1);

        PointLight spotPoint = new PointLight(ambientLight, new Vector3f(0.0f, 0.0f, 8.0f), lightIntensity);
        spotPoint.setAttenuation(new Attenuation(0.0f, 0.0f, 0.2f));
        spotLight = new SpotLight(spotPoint, new Vector3f(0, 0, -1), (float) Math.cos(Math.toRadians(140)));
        spotAngle = 0;
        spotInc = 1;
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
        if(!Keyboard.isKeyDown(Keys.getInstance().up) && !Keyboard.isKeyDown(Keys.getInstance().down)) {
            colourDirection = 0.0f;
        }

        if(Keyboard.isKeyDown(Keys.getInstance().right)) {
            spotLightDirection = 5.0f;
        }
        if(Keyboard.isKeyDown(Keys.getInstance().left)) {
            spotLightDirection = -5.0f;
        }
        if(!Keyboard.isKeyDown(Keys.getInstance().left) && !Keyboard.isKeyDown(Keys.getInstance().right)) {
            colourDirection = 0.0f;
        }

        testObject.getInput();
    }

    @Override
    public void update(Integration integrationType) {
        MouseCursor.update();

        Camera.getInstance().update();

        testObject.update(integrationType);

        colour += colourDirection * Time.getDelta();
        if(colour > 1.0f) {
            colour = 1.0f;
        } else if(colour < 0.0f) {
            colour = 0.0f;
        }

        lightAngle += (1.1f * Time.getDelta());
        if(lightAngle > 90) {
            directionalLight.setIntensity(0);
            if(lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if(lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.setColourG(Math.max(factor, 0.9f));
            directionalLight.setColourB(Math.max(factor, 0.5f));
        } else {
            directionalLight.setIntensity(1);
            directionalLight.setColourR(1);
            directionalLight.setColourG(1);
            directionalLight.setColourB(1);
        }
        float angleRadians = (float) Math.toRadians(lightAngle);
        directionalLight.setDirectionX((float) Math.sin(angleRadians));
        directionalLight.setDirectionY((float) Math.cos(angleRadians));

        spotLight.getPointLight().setPosition(new Vector3f(spotLight.getPointLight().getPosition().x, spotLight.getPointLight().getPosition().y, spotLight.getPointLight().getPosition().z + (spotLightDirection * Time.getDelta())));

        spotAngle += spotInc * 0.05f;
        if(spotAngle > 2) {
            spotInc = -1;
        } else if(spotAngle < -2) {
            spotInc = 1;
        }
        float spotAngleRadians = (float) Math.toRadians(spotAngle);
        Vector3f coneDirection = spotLight.getDirection();
        spotLight.setDirection(new Vector3f(coneDirection.x, (float) Math.sin(spotAngleRadians), coneDirection.z));
    }

    @Override
    public void render() {
        glClearColor(colour, colour, colour, 0.0f);

        testObject.setAmbientLight(ambientLight);
        testObject.setPointLight(pointLight);
        testObject.setDirectionalLight(directionalLight);
        testObject.setSpotLight(spotLight);
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
