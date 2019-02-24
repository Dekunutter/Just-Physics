package com.base.engine.physics.body;

import org.joml.Vector3f;

public class Plane {
    private final Vector3f normal;
    private final float distance;

    public Plane(Vector3f normal, float distance) {
        this.normal = new Vector3f(normal).normalize();
        this.distance = distance;
    }

    public Plane(Vector3f normal, Vector3f point) {
        this.normal = new Vector3f(normal).normalize();
        this.distance = normal.dot(point);
    }

    public float getDistance() {
        return distance;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float distanceToPoint(Vector3f point) {
        return normal.dot(point) - distance;
    }

    public boolean isPointBehindPlane(Vector3f point) {
        float pointDistance = distanceToPoint(point) / (float)Math.sqrt(normal.dot(normal));
        return pointDistance <= 0;
    }

    private Plane normalize() {
        float magnitude = normal.length();
        return new Plane(new Vector3f(normal).div(magnitude), distance * magnitude);
    }
}
