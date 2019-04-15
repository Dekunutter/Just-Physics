package com.base.engine.physics.collision;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Simplex {
    private List<Vector3f> points;

    public Simplex() {
        points = new ArrayList<>();
    }

    public void setPoint(Vector3f newPoint) {
        points.add(newPoint);
    }

    public int getSize() {
        return points.size();
    }

    public Vector3f getPoint(int index) {
        return points.get(index);
    }

    public Vector3f getFirstLine() {
        return getLine(points.get(0), points.get(1));
    }

    public Vector3f getSecondLine() {
        return getLine(points.get(0), points.get(2));
    }

    public Vector3f getEdgeA() {
        return getLine(points.get(0), points.get(3));
    }

    public Vector3f getEdgeB() {
        return getLine(points.get(1), points.get(3));
    }

    public Vector3f getEdgeC() {
        return getLine(points.get(2), points.get(3));
    }

    public void removeVertex(int index) {
        points.remove(index);
    }

    private Vector3f getLine(Vector3f firstPoint, Vector3f secondPoint) {
        Vector3f line = new Vector3f();
        secondPoint.sub(firstPoint, line);
        return line;
    }
}
