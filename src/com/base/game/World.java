package com.base.game;

import com.base.engine.Camera;
import com.base.engine.Debug;
import com.base.engine.GameObject;
import com.base.engine.loop.GameLoop;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.engine.physics.collision.*;
import com.base.engine.render.Attenuation;
import com.base.engine.render.lighting.*;
import com.base.game.objects.CameraObject;
import com.base.game.objects.TestObject;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class World implements GameLoop {
    private LightMap lights;
    private ArrayList<Camera> cameras;
    private ArrayList<GameObject> worldObjects;
    private CollisionAlgorithm collider;
    private CollisionResolver resolver;
    private TestObject testObject, testObjectB;
    private CollisionDetection collisionType;

    public World(CollisionDetection collisionType) throws Exception {
        this.collisionType = collisionType;

        lights = new LightMap();
        cameras = new ArrayList<>();

        switch(this.collisionType) {
            case BASIC_SAT:
                collider = new SeparatingAxisTheorem();
                break;
            case SWEPT_SAT:
                collider = new SeparatingAxisTheorem();
                break;
            case GJK:
                collider = new GJK();
                break;
        }
        resolver = new CollisionResolver();

        initObjects();
    }

    private void initObjects() throws Exception {
        worldObjects = new ArrayList<>();
        testObject = new TestObject(new Vector3f(0.0f, -5, -5f));
        testObject.getBody().setOrientation(0, 0, 0, 1);
        testObject.setController(Game.getInstance().getPlayerInput());
        testObject.getBody().addForce(new Vector3f(0, 100.0f, 0));
        //testObject.getBody().addTorque(new Vector3f(100.0f, 0, 0));
        worldObjects.add(testObject);
        CameraObject cameraObject = new CameraObject(this);
        cameraObject.setController(Game.getInstance().getPlayerInput());
        worldObjects.add(cameraObject);
        testObjectB = new TestObject(new Vector3f(0, 0, -5));
        testObjectB.getBody().setOrientation(0, 0, 0, 1);
        testObjectB.setController(Game.getInstance().getPlayerInput());
        worldObjects.add(testObjectB);

        TestObject testObjectC = new TestObject(new Vector3f(0, 5, -5));
        worldObjects.add(testObjectC);
        TestObject testObjectD = new TestObject(new Vector3f(-5, -5, -5));
        worldObjects.add(testObjectD);
        TestObject testObjectE = new TestObject(new Vector3f(-5, 0, -5));
        worldObjects.add(testObjectE);
        TestObject testObjectF = new TestObject(new Vector3f(-5, 5, -5));
        worldObjects.add(testObjectF);
        TestObject testObjectG = new TestObject(new Vector3f(5, -5, -5));
        worldObjects.add(testObjectG);
        TestObject testObjectH = new TestObject(new Vector3f(5, 0, -5));
        worldObjects.add(testObjectH);
        TestObject testObjectI = new TestObject(new Vector3f(5, 5, -5));
        worldObjects.add(testObjectI);

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
        Manifold collisionData;
        switch (collisionType) {
            case BASIC_SAT:
                for(int i = 0; i < worldObjects.size(); i++) {
                    worldObjects.get(i).update(integrationType);
                }

                List<CollisionIsland> collisions = new ArrayList<>();
                for(int i = 0; i < worldObjects.size(); i++) {
                    Body colliderA = worldObjects.get(i).getBody();
                    if(!colliderA.isSolid()) {
                        continue;
                    }
                    for(int j = 0; j < worldObjects.size(); j++) {
                        Body colliderB = worldObjects.get(j).getBody();
                        if(!colliderB.isSolid()) {
                            continue;
                        }
                        if(i == j) {
                            continue;
                        }

                        CollisionIsland island = new CollisionIsland(colliderA, colliderB);
                        if(collisions.contains(island)) {
                            continue;
                        }
                        collisions.add(island);

                        collisionData = collider.detect(island);
                        if(collisionData.isColliding()) {
                            resolver.resolveCollisions(island, collisionData);
                            resolver.correctPositions(island, collisionData);
                        }
                    }
                }
                break;
            case SWEPT_SAT:
                //TODO: Need to decide on a continuous solution as this will not stop tunneling from occurring in its current setup
                //copy body contents into new body objects
                //then expend body shapes by linear velocity over timestep to make them swept bodies
                //make sure faces and edges are expanded too if doing a direct copy
                //Run SAT on swept bodies before physic increments. Shouldn't need positional corrections this way since it is predictive

                for(int i = 0; i < worldObjects.size(); i++) {
                    worldObjects.get(i).update(integrationType);
                }
                break;
            case GJK:
                for(int i = 0; i < worldObjects.size(); i++) {
                    worldObjects.get(i).update(integrationType);
                }

                CollisionIsland island = new CollisionIsland(testObject.getBody(), testObjectB.getBody());

                GJK collider2 = new GJK();
                collisionData = collider2.detect(island);
                if(collisionData.isColliding()) {
                    System.out.println("COLLIDING NOW");
                }
                break;
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
        //TODO: I think I am getting this confused. I should only have one active camera and any given time that is rendering to the screen but multiple viewports. Complex subject though. Should look into more in the future
        for(int i = 0; i < cameras.size(); i++) {
            calculateProjectionAndView(cameras.get(i));

            for (int j = 0; j < worldObjects.size(); j++) {
                worldObjects.get(j).render(lights);
            }

            Debug.renderClipPoints();
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
