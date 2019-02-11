package com.base.game;

import com.base.engine.Camera;
import com.base.engine.Debug;
import com.base.engine.GameObject;
import com.base.engine.loop.GameLoop;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.collision.CollisionDetection;
import com.base.engine.physics.collision.Manifold;
import com.base.engine.render.Attenuation;
import com.base.engine.render.lighting.*;
import com.base.game.objects.CameraObject;
import com.base.game.objects.TestObject;
import org.joml.Vector3f;

import java.util.ArrayList;

public class World implements GameLoop {
    private LightMap lights;
    private ArrayList<Camera> cameras;
    private ArrayList<GameObject> worldObjects;
    private CollisionDetection collider;
    private TestObject testObject, testObjectB;

    public World() throws Exception {
        lights = new LightMap();
        cameras = new ArrayList<>();

        collider = new CollisionDetection();

        initObjects();
    }

    private void initObjects() throws Exception {
        worldObjects = new ArrayList<>();
        testObject = new TestObject(new Vector3f(0, 0, -5));
        testObject.setController(Game.getInstance().getPlayerInput());
        testObject.getBody().addForce(new Vector3f(0, 10.0f, 0));
        testObject.getBody().addTorque(new Vector3f(100.0f, 0, 0));
        worldObjects.add(testObject);
        CameraObject cameraObject = new CameraObject(this);
        cameraObject.setController(Game.getInstance().getPlayerInput());
        worldObjects.add(cameraObject);
        testObjectB = new TestObject(new Vector3f(0, 3, -5));
        testObjectB.setController(Game.getInstance().getPlayerInput());
        worldObjects.add(testObjectB);

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
            if(!worldObjects.get(i).isControlled()) {
                worldObjects.get(i).getInput();
            }
        }
    }

    @Override
    public void update(Integration integrationType) {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).update(integrationType);
        }
        Manifold collision = collider.separatingAxisTheorem(testObject.getBody(), testObjectB.getBody());
    }

    @Override
    public void interpolate(float alpha) {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).interpolate(alpha);
        }
    }

    @Override
    public void render() {
        //TODO: I think I am getting this confused. I should only have one active camera and any given time that is rendering to the screen but multiple viewports. Complex subject though. Should look into more in the future
        for(int i = 0; i < cameras.size(); i++) {
            calculateProjectionAndView(cameras.get(i));

            for (int j = 0; j < worldObjects.size(); j++) {
                worldObjects.get(j).render(lights);
            }

            Debug.renderContactPoints();
        }
    }

    public LightMap getLights() {
        return lights;
    }

    public void addLight(Light light) throws Exception {
        lights.put(light);
    }

    public ArrayList<Camera> getCameras() {
        return cameras;
    }

    public void addCamera(Camera camera) {
        cameras.add(camera);
    }

    public void calculateProjectionAndView(Camera camera) {
        Renderer.transformation.calculateProjectionMatrix(Renderer.FIELD_OF_VIEW, Renderer.Z_NEAR, Renderer.Z_FAR);
        Renderer.transformation.calculateViewMatrix(camera);
    }

    @Override
    public void cleanUp() {
        for(int i = 0; i < worldObjects.size(); i++) {
            worldObjects.get(i).cleanUp();
        }
    }
}
