package com.base.engine.physics.body;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Face {
    private Vector3f normal;
    private final ArrayList<Integer> edgeIndices;

    public Face() {
        normal = new Vector3f();
        edgeIndices = new ArrayList<>();
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector3f getTransformedNormal(Matrix4f transform) {
        Vector3f rotatedNormal = new Vector3f();
        normal.mulDirection(transform, rotatedNormal);
        rotatedNormal.normalize();
        return rotatedNormal;
    }

    public void addEdgeIndex(int index) {
        edgeIndices.add(index);
    }

    public ArrayList<Integer> getEdgeIndices() {
        return edgeIndices;
    }
}
