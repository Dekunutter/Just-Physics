package com.base.engine.physics.body;

import org.joml.Vector3f;

public class State {
    public Vector3f position, velocity, acceleration, momentum;
    public Vector3f rotation;
    public float scale;

    //Previous position required for Verlet integration
    public Vector3f previousPosition;

    public State() {
        position = new Vector3f();
        velocity = new Vector3f();
        acceleration = new Vector3f();
        momentum = new Vector3f();
        previousPosition = new Vector3f();

        //TODO: Verifiy the inclusion of rotation and scale in the body state and how it affects physics?
        rotation = new Vector3f();
        scale = 1;
    }

    public State(State other) {
        copyState(other);
    }

    public void copyState(State other) {
        position = new Vector3f(other.position);
        velocity = new Vector3f(other.velocity);
        acceleration = new Vector3f(other.acceleration);
        momentum = new Vector3f(other.momentum);
        previousPosition = new Vector3f(other.previousPosition);

        rotation = new Vector3f(other.rotation);
        scale = other.scale;
    }

    public void mul(float value) {
        position.mul(value);
        velocity.mul(value);
        acceleration.mul(value);
        momentum.mul(value);
        previousPosition.mul(value);

        rotation.mul(rotation);
        scale *= value;
    }

    public void add(State other) {
        position.add(other.position);
        velocity.add(other.velocity);
        acceleration.add(other.acceleration);
        momentum.add(other.momentum);
        previousPosition.add(other.previousPosition);

        rotation.add(other.rotation);
        scale += other.scale;
    }

    public void recalculate(float mass) {
        momentum.mul(1.0f / mass, velocity);
    }
}
