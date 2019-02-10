package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import com.base.engine.render.Mesh;
import org.joml.Vector3f;

public class ContactPoint {
    private Vector3f position, normal, pointVelocity;
    private float penetration;
    private Body body, other;

    private Mesh mesh;
    private float[] vertices = {-0.05f, 0.05f, 0, -0.05f, -0.05f, 0, 0.05f, -0.05f, 0, 0.05f, 0.05f, 0};
    private int[] indices = {0, 1, 2, 2, 3, 0};

    public ContactPoint(Vector3f position, Body body) {
        this.position = position;
        this.body = body;

        mesh = new Mesh(vertices, new float[] {}, new float[] {}, indices);
    }

    public ContactPoint(Vector3f position, float depth) {
        this.position = position;
        this.penetration = depth;

        mesh = new Mesh(vertices, new float[] {}, new float[] {}, indices);
    }

    public ContactPoint(Vector3f position, Vector3f normal, Body body, Body other) {
        this.position = position;
        this.normal = normal;
        this.body = body;
        this.other = other;

        mesh = new Mesh(vertices, new float[] {}, new float[] {}, indices);
    }

    public ContactPoint(Vector3f position, Vector3f normal, float depth, Body body, Body other) {
        this.position = position;
        this.normal = normal;
        this.penetration = depth;
        this.body = body;
        this.other = other;

        mesh = new Mesh(vertices, new float[] {}, new float[] {}, indices);
    }

    public void swapBodies() {
        Body temp = body;
        body = other;
        other = temp;
    }

    public void calculateData() {
        /*relativePosition = new Vector3f(position);
        relativePosition.sub(body.getPosition());

        relativePositionOther = new Vector3f(position);
        relativePositionOther.sub(other.getPosition());

        velocity = new Vector3f(body.getAngularVelocity());
        velocity.cross(relativePosition);
        velocity.add(body.getLinearVelocity());*/

        pointVelocity = new Vector3f();

        Vector3f bodyVelocity = body.getVelocityAtPoint(position);
        Vector3f otherVelocity = other.getVelocityAtPoint(position);
        bodyVelocity.sub(otherVelocity, pointVelocity);
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector3f getVelocity() {
        return pointVelocity;
    }
}
