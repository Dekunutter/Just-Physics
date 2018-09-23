package com.base.engine.physics.body;

import org.joml.Vector3f;

//stores the derivatives of position and velocity (which are velocity(position/delta) and acceleration(velocity/delta) respectively)
//In the case of momentum based physics, the acceleration holds onto the momentum instead
public class Derivative {
    private Vector3f velocity, acceleration;

    public Derivative() {
        velocity = new Vector3f();
        acceleration = new Vector3f();
    }

    public Derivative(Vector3f velocity, Vector3f acceleration) {
        this.velocity = new Vector3f(velocity);
        this.acceleration = new Vector3f(acceleration);
    }

    public Derivative add(Derivative other) {
        velocity.add(other.velocity);
        acceleration.add(other.acceleration);
        return this;
    }

    public Derivative mul(float value) {
        velocity.mul(value);
        acceleration.mul(value);
        return this;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getAcceleration() {
        return acceleration;
    }
}
