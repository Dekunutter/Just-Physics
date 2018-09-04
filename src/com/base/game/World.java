package com.base.game;

import com.base.engine.GameObject;
import com.base.engine.loop.GameLoop;
import com.base.engine.physics.Integration;
import com.base.engine.render.Attenuation;
import com.base.engine.render.lighting.*;
import org.joml.Vector3f;

import java.util.ArrayList;

public class World implements GameLoop {
    private LightMap lights;
    private ArrayList<GameObject> worldObjects;

    public World() throws Exception {
        worldObjects = new ArrayList<>();
        GameObject testObject = new TestObject(this);
        worldObjects.add(testObject);

        lights = new LightMap();

        AmbientLight ambientLight = new AmbientLight(new Vector3f(0.1f, 0.1f, 0.1f), 10f);
        lights.put(ambientLight);

        Vector3f lightColour = new Vector3f(1.0f, 1.0f, 1.0f);
        Vector3f lightPosition = new Vector3f(0.0f, 0.0f, 1.0f);
        float lightIntensity = 10;
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        Attenuation pointAttenuation = new Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(pointAttenuation);
        lights.put(pointLight);

        DirectionalLight directionalLight = new DirectionalLight(lightColour, new Vector3f(-1, 0, 0), 1);
        lights.put(directionalLight);

        PointLight spotPoint = new PointLight(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 0.0f, 8.0f), lightIntensity);
        spotPoint.setAttenuation(new Attenuation(0.0f, 0.0f, 0.2f));
        SpotLight spotLight = new SpotLight(spotPoint, new Vector3f(0, 0, -1), (float) Math.cos(Math.toRadians(140)));
        lights.put(spotLight);
    }


    @Override
    public void getInput() {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).getInput();
        }
    }

    @Override
    public void update(Integration integrationType) {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).update(integrationType);
        }
    }

    @Override
    public void interpolate(float alpha) {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).interpolate(alpha);
        }
    }

    @Override
    public void render() {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).render();
        }
    }

    public LightMap getLights() {
        return lights;
    }

    @Override
    public void cleanUp() {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).cleanUp();
        }
    }
}
