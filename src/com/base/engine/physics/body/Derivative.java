package com.base.engine.physics.body;

import org.joml.Vector3f;

public class Derivative {
    private Vector3f position, velocity;

    public Derivative() {
        position = new Vector3f();
        velocity = new Vector3f();
    }

    public Derivative(Vector3f position, Vector3f velocity) {
        this.position = new Vector3f(position);
        this.velocity = new Vector3f(velocity);
    }

    public Derivative add(Derivative other) {
        position.add(other.position);
        velocity.add(other.velocity);
        return this;
    }

    public Derivative mul(float value) {
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
