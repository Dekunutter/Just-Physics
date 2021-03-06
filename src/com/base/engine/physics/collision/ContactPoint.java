package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

public class ContactPoint {
    private Vector3f position, normal, pointVelocity;
    private float penetration;
    private Body body, other;

    public ContactPoint(Vector3f position, Vector3f normal, float depth, Body body, Body other) {
        this.position = position;
        this.normal = normal;
        this.penetration = depth;
        this.body = body;
        this.other = other;
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

    public Vector3f getPosition() {
        return position;
    }
}
