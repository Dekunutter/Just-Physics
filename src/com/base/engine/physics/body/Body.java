package com.base.engine.physics.body;

import com.base.engine.Debug;
import com.base.engine.Time;
import com.base.engine.physics.Integration;
import org.joml.Vector3f;

public class Body {
    private State currentState, previousState, interpolatedState;
    private Vector3f force;
    private float mass;

    public Body() {
        currentState = new State();
        previousState = new State();
        interpolatedState = new State();

        force = new Vector3f();
        mass = 1.0f;
    }

    public void updatePreviousState() {
        previousState.copyState(currentState);
    }

    public void advancePhysics(Integration integration) {
        switch(integration) {
            case EXPLICIT:
                advanceExplicitly();
                break;
            case SEMI_IMPLICIT:
                advanceSemiImplicitly();
                break;
            case IMPLICIT:
                advanceImplicitly();
                break;
            case VERLET:
                advanceWithVerlet();
                break;
            case VELOCITY_VERLET:
                advanceWithVelocityVerlet();
                break;
            case TCV:
                advanceWithTCV();
                break;
            case RK4:
                advanceWithRK4();
                break;
            default:
                advanceExplicitly();
        }
    }

    //Not good for variable acceleration but works great for constant acceleration
    //Will cause issues with spring damper systems making them gain energy over time instead of lose it!
    private void advanceExplicitly() {
        Debug.println("%s    %s    %s", Time.getDelta(), currentState.position, currentState.velocity);

        Vector3f velocityOverTime = new Vector3f();
        Vector3f accelerationOverTime = new Vector3f();

        currentState.position.add(currentState.velocity.mul(Time.getDelta(), velocityOverTime));
        force.div(mass, currentState.acceleration);
        currentState.velocity.add(currentState.acceleration.mul(Time.getDelta(), accelerationOverTime));
    }

    //What makes the difference between explicit and semi-explicity Euler is simply the ordering
    //The ordering is important and solves spring dampening issues
    //Fixes the spring damper system issue that explicit euler has but has some issues with stiff equations
    private void advanceSemiImplicitly() {
        Debug.println("%s    %s    %s", Time.getDelta(), currentState.position, currentState.velocity);

        Vector3f velocityOverTime = new Vector3f();
        Vector3f accelerationOverTime = new Vector3f();

        force.div(mass, currentState.acceleration);
        currentState.velocity.add(currentState.acceleration.mul(Time.getDelta(), accelerationOverTime));
        currentState.position.add(currentState.velocity.mul(Time.getDelta(), velocityOverTime));
    }

    //Fixes the issues with stiff equations that other euler methods have at the cost of requiring more object state and more complex math
    private void advanceImplicitly() {
        Debug.println("%s    %s    %s", Time.getDelta(), currentState.position, currentState.velocity);

        force.div(mass, currentState.acceleration);

        Vector3f velocityOverTime = new Vector3f(currentState.velocity).mul(Time.getDelta());
        Vector3f accelerationOverTime = new Vector3f(currentState.acceleration).mul(Time.getDelta());
        Vector3f changeDueToAcceleration = new Vector3f(currentState.acceleration).mul(0.5f).mul(Time.getDelta() * Time.getDelta());

        currentState.position.add(velocityOverTime).add(changeDueToAcceleration);
        currentState.velocity.add(accelerationOverTime);
    }

    //greater accuracy than implicit euler and less memory usage. 2nd order symplectic integrator
    //Great for multiple bodies and such where I don't really care about velocities and can presume them
    //Requires fixed time steps to retain accuracy
    //TODO: Fix bug in verlet that takes current position and develops velocity off of that without any positional change (aka: declared to spawn at a position that is not (0, 0, 0) and now moving infinitely in the direction of all non-zero values
    //TODO: Verify all other integration methods work as expected at non-zero starting values
    private void advanceWithVerlet() {
        Debug.println("%s    %s     %s      %s", Time.getDelta(), currentState.position, currentState.velocity, currentState.previousPosition);

        Vector3f tempPosition = new Vector3f(currentState.position);

        force.div(mass, currentState.acceleration);

        Vector3f changeDueToAcceleration = new Vector3f(currentState.acceleration).mul(Time.getDelta() * Time.getDelta());
        Vector3f positionDifference = new Vector3f(currentState.position).sub(currentState.previousPosition);
        Vector3f positionAfterForce = new Vector3f(positionDifference).add(changeDueToAcceleration);

        currentState.position.add(positionAfterForce);
        currentState.velocity = new Vector3f(positionDifference).div(Time.getDelta());

        currentState.previousPosition = new Vector3f(tempPosition);
    }

    //Verlet that also calculates a velocity at the cost of some performance and a little bit of accuracy
    //Requires fixed time steps like standard Verlet
    private void advanceWithVelocityVerlet() {
        Debug.println("%s    %s     %s", Time.getDelta(), currentState.position, currentState.velocity);

        Vector3f changeDueToAcceleration = new Vector3f(currentState.acceleration).mul(Time.getDelta() / 2.0f);
        Vector3f halfVelocity = new Vector3f(currentState.velocity);
        halfVelocity.add(changeDueToAcceleration);

        Vector3f halfVelocityOverTime = new Vector3f(halfVelocity).mul(Time.getDelta());
        currentState.position.add(halfVelocityOverTime);

        force.div(mass, currentState.acceleration);

        changeDueToAcceleration = new Vector3f(currentState.acceleration).mul(Time.getDelta() / 2.0f);
        currentState.velocity = new Vector3f(halfVelocity).add(changeDueToAcceleration);
    }

    //A time-corrected version of Verlet which grants higher stability for non-fixed time steps at slightly most cost
    //Offers the same level of accuracy for fixed time-step loops as standard Verlet
    private void advanceWithTCV() {
        Debug.println("%s    %s", Time.getDelta(), currentState.position);

        Vector3f tempPosition = new Vector3f(currentState.position);

        force.div(mass, currentState.acceleration);

        Vector3f changeDueToAcceleration = new Vector3f(currentState.acceleration).mul(Time.getDelta() * Time.getDelta());
        Vector3f positionDifference = new Vector3f(currentState.position).sub(currentState.previousPosition);
        Vector3f timeCorrectedDifference = new Vector3f(positionDifference).mul(Time.getDelta() / Time.getPreviousDelta());
        Vector3f positionAfterForce = new Vector3f(timeCorrectedDifference).add(changeDueToAcceleration);

        currentState.position.add(positionAfterForce);

        currentState.previousPosition = new Vector3f(tempPosition);
    }

    //A more accurate but computationally expensive integrator that loses slight amounts of energy as it goes
    private void advanceWithRK4() {
        Debug.println("%s    %s    %s", Time.getDelta(), currentState.position, currentState.velocity);

        Derivative d0 = new Derivative();
        Derivative d1 = calculateDerivative(currentState.position, currentState.velocity, Time.getDelta() * 0.0f, d0);
        Derivative d2 = calculateDerivative(currentState.position, currentState.velocity, Time.getDelta() * 0.5f, d1);
        Derivative d3 = calculateDerivative(currentState.position, currentState.velocity, Time.getDelta() * 0.5f, d2);
        Derivative d4 = calculateDerivative(currentState.position, currentState.velocity, Time.getDelta() * 1.0f, d3);

        d2.add(d3).mul(2.0f);
        d4.add(d1).add(d2).mul(1.0f / 6.0f);

        currentState.position.add(d4.getPosition().mul(Time.getDelta()));
        currentState.velocity.add(d4.getVelocity().mul(Time.getDelta()));
    }

    private Derivative calculateDerivative(Vector3f position, Vector3f velocity, float delta, Derivative source) {
        Vector3f newPosition = new Vector3f();
        source.getPosition().mul(delta, newPosition);
        newPosition.add(position);
        Vector3f newVelocity = new Vector3f();
        source.getVelocity().mul(delta, newVelocity);
        newVelocity.add(velocity);

        force.div(mass, currentState.acceleration);

        return new Derivative(newVelocity, currentState.acceleration);
    }

    public void interpolate(float alpha) {
        interpolatedState.copyState(currentState);
        interpolatedState.mul(alpha);

        State tempPreviousState = new State(previousState);
        tempPreviousState.mul(1.0f - alpha);

        interpolatedState.add(tempPreviousState);

        //Debug.println("interpolated %s    %s    %s", alpha, interpolatedState.position, interpolatedState.velocity);
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void addForce(Vector3f force) {
        this.force.add(force);
    }

    public Vector3f getPosition() {
        return currentState.position;
    }

    public void setPosition(float x, float y, float z) {
        currentState.position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return currentState.rotation;
    }

    public float getScale() {
        return currentState.scale;
    }

    public void alterScale(float value) {
        currentState.scale += value;
    }

    public void setScale(float newScale) {
        currentState.scale = newScale;
    }
}
