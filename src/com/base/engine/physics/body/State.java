package com.base.engine.physics.body;

import org.joml.Vector3f;

public class State {
    public Vector3f position, velocity, acceleration;

    //Previous position required for Verlet integration
    public Vector3f previousPosition;

    public State() {
        position = new Vector3f();
        velocity = new Vector3f();
        acceleration = new Vector3f();
        previousPosition = new Vector3f();
    }

    public State(State other) {
        copyState(other);
    }

    public void copyState(State other) {
        position = new Vector3f(other.position);
        velocity = new Vector3f(other.velocity);
        acceleration = new Vector3f(other.acceleration);
        previousPosition = new Vector3f(other.previousPosition);
    }

    public void mul(float value) {
        position.mul(value);
        velocity.mul(value);
        acceleration.mul(value);
        previousPosition.mul(value);
    }

    public void add(State other) {
        position.add(other.position);
        velocity.add(other.velocity);
        acceleration.add(other.acceleration);
        previousPosition.add(other.previousPosition);
    }
}
