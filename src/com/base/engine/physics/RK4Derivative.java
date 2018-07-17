package com.base.engine.physics;

import org.joml.Vector3f;

public class RK4Derivative {
    private Vector3f position, velocity;

    public RK4Derivative() {
        position = new Vector3f();
        velocity = new Vector3f();
    }

    public RK4Derivative(Vector3f position, Vector3f velocity) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(velocity);
    }

    public RK4Derivative add(RK4Derivative other) {
        position.add(other.position);
        velocity.add(other.velocity);
        return this;
    }

    public RK4Derivative mul(float value) {
        position.mul(value);
        velocity.mul(value);
        return this;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}
