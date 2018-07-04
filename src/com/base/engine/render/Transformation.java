package com.base.engine.render;

import org.joml.Matrix4f;

public class Transformation {
    private final Matrix4f modelViewMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;

    public Transformation() {
        modelViewMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
    }
}
