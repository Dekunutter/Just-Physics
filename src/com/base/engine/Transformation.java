package com.base.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f modelViewMatrix;

    //NOTE: Storing these as objects here so that they are reused and not recreated for each object (in the case of modelview) or each world (in the case of projection and view)
    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fieldOfView, float zNear, float zFar) {
        projectionMatrix.identity();
        projectionMatrix.perspective(fieldOfView, Engine.window.getAspectRatio(), zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraPosition = Camera.getInstance().getPosition();
        Vector3f cameraRotation = Camera.getInstance().getRotation();

        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.x), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(cameraRotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        return viewMatrix;
    }

    public Matrix4f getModelViewMatrix(Vector3f offset, Vector3f rotation, float scale, Matrix4f viewMatrix) {
        modelViewMatrix.identity().translate(offset).rotateX((float) Math.toRadians(-rotation.x)).rotateY((float) Math.toRadians(-rotation.y)).rotateZ((float) Math.toRadians(-rotation.z)).scale(scale);
        Matrix4f currentView = new Matrix4f(viewMatrix);
        return currentView.mul(modelViewMatrix);
    }
}
