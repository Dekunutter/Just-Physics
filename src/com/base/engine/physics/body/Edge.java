package com.base.engine.physics.body;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public class Edge {
    private final Vector3f direction, pointA, pointB;
    private int faceAIndex, faceBIndex;

    public Edge(Vector3f pointA, Vector3f pointB) {
        direction = new Vector3f();
        pointB.sub(pointA, direction);

        this.pointA = pointA;
        this.pointB = pointB;
        faceAIndex = -1;
        faceBIndex = -1;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getTransformedDirection(Matrix4f transform) {
        Vector3f translatedDirection = new Vector3f();
        direction.mulDirection(transform, translatedDirection);
        return translatedDirection;
    }

    public Vector3f getPointA() {
        return pointA;
    }

    public Vector3f getPointB() {
        return pointB;
    }

    public boolean containsPoint(Vector3f point) {
        if(pointA.distance(point) < 0.0001f || pointB.distance(point) < 0.0001f) {
            return true;
        }
        return false;
    }

    public void addFace(int index) {
        if(faceAIndex == -1) {
            faceAIndex = index;
        }
        else if(faceBIndex == -1) {
            faceBIndex = index;
        }
    }

    public int getOtherFaceOnEdge(int index) {
        if(faceAIndex == index) {
            return faceBIndex;
        }
        else if(faceBIndex == index) {
            return faceAIndex;
        }
        return -1;
    }

    public int getFaceAIndex() {
        return faceAIndex;
    }

    public int getFaceBIndex() {
        return faceBIndex;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Edge)) {
            return false;
        }

        Edge otherEdge = (Edge) other;
        if(otherEdge.pointA.equals(pointA) && otherEdge.pointB.equals(pointB)) {
            return true;
        }
        else if(otherEdge.pointA.equals(pointB) && otherEdge.pointB.equals(pointA)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.direction);
        return hash;
    }

    @Override
    public String toString() {
        return "Edge: " + pointA.x + " " + pointA.y + " " + pointA.z + "   " + pointB.x + " " + pointB.y + " " + pointB.z;
    }
}
