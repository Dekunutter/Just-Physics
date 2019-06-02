package com.base.engine.physics.collision;

import org.joml.Vector3f;

public class Simplex {
    private SimplexSupportPoint[] points;
    private int pointsFilled;

    public Simplex() {
        points = new SimplexSupportPoint[4];
        for(int i = 0; i < points.length; i++) {
            points[i] = new SimplexSupportPoint();
        }
        pointsFilled = 0;
    }

    public void setPoint(int index, SimplexSupportPoint newPoint) {
        points[index] = newPoint;
        pointsFilled++;
    }

    public SimplexSupportPoint getPoint(int index) {
        return points[index];
    }

    public Vector3f getPointCoordinates(int index) {
        return points[index].getPoint();
    }

    public void copyPoint(int indexA, int indexB) {
        points[indexA] = new SimplexSupportPoint(points[indexB]);
    }

    public Vector3f getLine(int indexA, int indexB) {
        Vector3f line = new Vector3f();
        points[indexA].getPoint().sub(points[indexB].getPoint(), line);
        return line;
    }

    public void devolve() {
        pointsFilled--;
    }

    public int getSize() {
        return pointsFilled;
    }
}
