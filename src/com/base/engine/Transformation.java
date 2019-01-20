package com.base.engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f modelViewMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
    }

    public final void calculateProjectionMatrix(float fieldOfView, float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(fieldOfView, Engine.window.getAspectRatio(), zNear, zFar);
    }

    public void calculateViewMatrix(Camera camera) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f cameraRotation = camera.getRotation();

        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
    }

    public final Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public final Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(Vector3f offset, Vector3f rotation, float scale, Matrix4f viewMatrix) {
        modelViewMatrix.identity().translate(offset).rotateX((float) Math.toRadians(-rotation.x)).rotateY((float) Math.toRadians(-rotation.y)).rotateZ((float) Math.toRadians(-rotation.z)).scale(scale);
        Matrix4f currentView = new Matrix4f(viewMatrix);
        return currentView.mul(modelViewMatrix);
    }

    public Matrix4f getModelViewMatrix(Vector3f position, Quaternionf rotation, float scale, Matrix4f viewMatrix)
    {
        modelViewMatrix.identity().translate(position).rotate(rotation).scale(scale);
        Matrix4f viewCurrent = new Matrix4f(viewMatrix);
        return viewCurrent.mul(modelViewMatrix);
    }
}
