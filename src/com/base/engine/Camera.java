package com.base.engine;

import com.base.engine.input.mouse.Mouse;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f position, rotation;

    public Camera() {
        this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    public Camera(Vector3f startPosition) {
        this(startPosition, new Vector3f(0, 0, 0));
    }

    public Camera(Vector3f startPosition, Vector3f startRotation) {
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
            position.y += (float) Math.sin(Math.toRadians(rotation.x)) * offsetZ;
        }
        if(offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
            position.y += (float) Math.sin(Math.toRadians(rotation.x - 90)) * offsetZ;
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
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    public void update(Vector3f movement, boolean rotating, float zoomState)
    {
        movePosition(movement.x * Time.getDelta(), movement.y * Time.getDelta(), movement.z * Time.getDelta());
        if(rotating) {
            Vector2f mouseRotation = Mouse.getDisplayVector();
            moveRotation(mouseRotation.x * Time.getDelta(), mouseRotation.y * Time.getDelta(), 0);
        }
        movePosition(0, 0, zoomState * Time.getDelta());
    }
}
