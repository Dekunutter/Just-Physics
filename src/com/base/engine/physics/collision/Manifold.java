package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

import java.util.ArrayList;

//TODO: Abstract this and split it up into two different manifold types for face vs edge collisions
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
    public int edgeA, edgeB;
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

    public void addContactPoint(ContactPoint point) {
        points.add(point);
    }

    public void addContactPoints(ArrayList<ContactPoint> points) {
        this.points.addAll(points);
    }

    public int getEdgeA() {
        return edgeA;
    }

    public int getEdgeB() {
        return edgeB;
    }

    public void setEdgeA(int edgeA) {
        this.edgeA = edgeA;
    }

    public void setEdgeB(int edgeB) {
        this.edgeB = edgeB;
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
