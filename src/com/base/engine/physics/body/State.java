package com.base.engine.physics.body;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class State {
    public Vector3f position, velocity, angularVelocity, acceleration, angularAcceleration, momentum, angularMomentum;
    public Quaternionf orientation, spin;
    public float scale;

    //Previous position required for Verlet integration
    public Vector3f previousPosition;

    public State() {
        position = new Vector3f();
        orientation = new Quaternionf();
        spin = new Quaternionf();
        velocity = new Vector3f();
        angularVelocity = new Vector3f();
        acceleration = new Vector3f();
        angularAcceleration = new Vector3f();
        momentum = new Vector3f();
        angularMomentum = new Vector3f();
        previousPosition = new Vector3f();

        //TODO: Verifiy the inclusion of scale in the body state and how it affects physics?
        scale = 1;
    }

    public State(State other) {
        copyState(other);
    }

    public void copyState(State other) {
        position = new Vector3f(other.position);
        orientation = new Quaternionf(other.orientation);
        spin = new Quaternionf(other.spin);
        velocity = new Vector3f(other.velocity);
        angularVelocity = new Vector3f(other.angularVelocity);
        acceleration = new Vector3f(other.acceleration);
        angularAcceleration = new Vector3f(other.angularAcceleration);
        momentum = new Vector3f(other.momentum);
        angularMomentum = new Vector3f(other.angularMomentum);
        previousPosition = new Vector3f(other.previousPosition);

        scale = other.scale;
    }

    public void interpolate(State current, State previous, float alpha) {
        copyState(current);
        position.lerp(previous.position, alpha);
        orientation.slerp(previous.orientation, alpha);
        spin.slerp(previous.spin, alpha);
        velocity.lerp(previous.velocity, alpha);
        angularVelocity.lerp(previous.angularVelocity, alpha);
        acceleration.lerp(previous.acceleration, alpha);
        angularAcceleration.lerp(previous.angularAcceleration, alpha);
        momentum.lerp(previous.momentum, alpha);
        angularMomentum.lerp(previous.angularMomentum, alpha);
        previousPosition.lerp(previous.previousPosition, alpha);

        scale += (previous.scale - scale) * alpha;
    }

    public void recalculate(float mass, float inertia) {
        momentum.mul(1.0f / mass, velocity);

        angularMomentum.mul(1.0f / inertia, angularVelocity);
        orientation.normalize();
    }
}
