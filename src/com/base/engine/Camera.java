package com.base.engine;

import org.joml.Vector3f;

public class Camera {
    //TODO: I don't want camera to be a singleton but I need to alter the renderer code to take in a current camera object as a view matrix and render all objects in THAT camera. Or maybe render each object in the eyes of each camera object by object instead of camera by camera?
    private static Camera instance;

    private final Vector3f position, rotation;

    private Camera() {
        this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    private Camera(Vector3f startPosition) {
        this(startPosition, new Vector3f(0, 0, 0));
    }

    private Camera(Vector3f startPosition, Vector3f startRotation) {
        position = new Vector3f(startPosition);
        rotation = new Vector3f(startRotation);
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if(offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if(offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x = offsetX;
        rotation.y = offsetY;
        rotation.z = offsetZ;
    }

    public static Camera getInstance() {
        if(instance == null) {
            instance = new Camera();
        }
        return instance;
    }
}
