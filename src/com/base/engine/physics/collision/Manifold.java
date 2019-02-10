package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Manifold {
    private enum State {
        SEPARETED, COLLIDING, OVERLAPPING;
    }

    private Body bodyA, bodyB;
    private float penetration;
    private Vector3f normal;
    private State collisionState;
    private int type;
    private final ArrayList<ContactPoint> points;
    public Vector3f edgeDirectionA, edgeDirectionB;
    public int referenceFace, incidentFace;
    public Vector3f supportA, supportB;

    public Manifold(Body bodyA, Body bodyB) {
        this.bodyA = bodyA;
        this.bodyB = bodyB;
        collisionState = State.SEPARETED;

        penetration = -Float.MAX_VALUE;
        normal = new Vector3f();
        type = -1;
        points = new ArrayList<>();

        supportA = null;
        supportB = null;
        referenceFace = 0;
        incidentFace = 0;
    }

    public Body getReferenceBody() {
        return bodyA;
    }

    public Body getIncidentBody() {
        return bodyB;
    }

    public void setReferenceBody(Body body) {
        bodyA = body;
    }

    public void setIncidentBody(Body body) {
        bodyB = body;
    }

    public float getPenetration() {
        return penetration;
    }

    public void setPenetration(float value) {
        penetration = value;
    }

    public Vector3f getEnterNormal() {
        return normal;
    }

    public void setEnterNormal(Vector3f normal) {
        this.normal = normal;
    }

    public void setOverlapped() {
        collisionState = State.OVERLAPPING;
    }

    public void setCollided() {
        collisionState = State.COLLIDING;
    }

    public boolean isColliding() {
        return collisionState != State.SEPARETED;
    }

    public int getType() {
        return type;
    }

    public void setType(int value) {
        type = value;
    }

    public ArrayList<ContactPoint> getContactPoints() {
        return points;
    }

    public void addContactPoint(Vector3f point, Body body) {
        points.add(new ContactPoint(point, body));
    }

    public void addContactPoint(Vector3f point, Vector3f normal, Body body, Body other) {
        points.add(new ContactPoint(point, normal, body, other));
    }

    public void addContactPoint(ContactPoint point) {
        points.add(point);
    }

    public void addContactPoints(ArrayList<ContactPoint> points) {
        this.points.addAll(points);
    }

    public Vector3f getEdgeDirectionA() {
        return edgeDirectionA;
    }

    public Vector3f getEdgeDirectionB() {
        return edgeDirectionB;
    }

    public void setEdgeDirectionA(Vector3f edgeNormal) {
        edgeDirectionA = edgeNormal;
    }

    public void setEdgeDirectionB(Vector3f edgeNormal) {
        edgeDirectionB = edgeNormal;
    }

    public Vector3f getSupportA() {
        return supportA;
    }

    public Vector3f getSupportB() {
        return supportB;
    }

    public void setSupportA(Vector3f support) {
        supportA = support;
    }

    public void setSupportB(Vector3f support) {
        supportB = support;
    }

    public void setReferenceFace(int face) {
        referenceFace = face;
    }

    public int getReferenceFace() {
        return referenceFace;
    }
}
