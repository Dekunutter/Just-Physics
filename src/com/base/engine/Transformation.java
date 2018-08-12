package com.base.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f worldMatrix;

    public Transformation() {
        worldMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fieldOfView, float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(fieldOfView, Engine.window.getAspectRatio(), zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
        worldMatrix.identity().translate(offset).rotateX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y)).rotateZ((float) Math.toRadians(rotation.z)).scale(scale);
        return worldMatrix;
    }
}
