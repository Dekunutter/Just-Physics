package com.base.engine.physics.body;

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

    public void addEdgeIndex(int index) {
        edgeIndices.add(index);
    }

    public ArrayList<Integer> getEdgeIndices() {
        return edgeIndices;
    }
}
