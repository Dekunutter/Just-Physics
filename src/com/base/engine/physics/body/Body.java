package com.base.engine.physics.body;

import com.base.engine.Debug;
import com.base.engine.Time;
import com.base.engine.physics.Integration;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Body {
    private State currentState, previousState, interpolatedState;
    private Vector3f force, torque;
    private float mass;
    private float momentOfInertia;

    public Body() {
        currentState = new State();
        previousState = new State();
        interpolatedState = new State();

        force = new Vector3f();
        torque = new Vector3f();
        mass = 1.0f;
        momentOfInertia = (1.0f / 6.0f) * (float) Math.pow(1, 2) * mass;
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
                //advanceSemiImplicitly();
                advanceSemiImplicitlyWithRotation();
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

    //A variation of semi-implicit Euler which calculates both linear and angular velocities using momentum instead of acceleration
    //Swapping to momentum calculations for linear velocity allows for some consistency between how velocities are calculated since angular velocity needs the momentum method
    private void advanceSemiImplicitlyWithRotation() {
        Debug.println("%s    %s    %s   %s", Time.getDelta(), currentState.position, currentState.velocity, currentState.momentum);

        Vector3f momentumOverTime = new Vector3f();
        Vector3f momentumToVelocity = new Vector3f();
        Vector3f velocityOverTime = new Vector3f();
        currentState.momentum.add(force.mul(Time.getDelta(), momentumOverTime));
        currentState.velocity.add(currentState.momentum.mul(1.0f / mass, momentumToVelocity));
        currentState.position.add(currentState.velocity.mul(Time.getDelta(), velocityOverTime));

        Vector3f angularMomentumOverTime = new Vector3f();
        Vector3f angularMomentumToVelocity = new Vector3f();
        currentState.angularMomentum.add(torque.mul(Time.getDelta(), angularMomentumOverTime));
        currentState.angularVelocity.add(currentState.angularMomentum.mul(1.0f / momentOfInertia, angularMomentumToVelocity));
        currentState.orientation.integrate(Time.getDelta(), currentState.angularVelocity.x, currentState.angularVelocity.y, currentState.angularVelocity.z);

        currentState.recalculate(mass, momentOfInertia);
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

    //Alternative implementation of velocity verlet
    //Has a different, more involved means of calculating the velocity
    private void advanceWithVelocityVerletAlternative() {
        Vector3f previousAcceleration = new Vector3f(currentState.acceleration);
        Vector3f halfAcceleration = new Vector3f();
        Vector3f halfAccelerationOverTime = new Vector3f();
        Vector3f averageAcceleration = new Vector3f();

        previousAcceleration.mul(0.5f, halfAcceleration);
        halfAcceleration.mul(Time.getDelta() * Time.getDelta(), halfAccelerationOverTime);
        Vector3f velocityOverTime = new Vector3f();
        currentState.velocity.mul(Time.getDelta(), velocityOverTime);
        currentState.position.add(velocityOverTime.add(halfAccelerationOverTime));

        force.div(mass, currentState.acceleration);

        previousAcceleration.add(currentState.acceleration, averageAcceleration);
        averageAcceleration.div(2);
        currentState.velocity.add(averageAcceleration.mul(Time.getDelta()));
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
        Derivative d1 = calculateDerivative(currentState.velocity, Time.getDelta() * 0.0f, d0);
        Derivative d2 = calculateDerivative(currentState.velocity, Time.getDelta() * 0.5f, d1);
        Derivative d3 = calculateDerivative(currentState.velocity, Time.getDelta() * 0.5f, d2);
        Derivative d4 = calculateDerivative(currentState.velocity, Time.getDelta() * 1.0f, d3);

        d2.add(d3).mul(2.0f);
        d4.add(d1).add(d2).mul(1.0f / 6.0f);

        currentState.position.add(d4.getVelocity().mul(Time.getDelta()));
        currentState.velocity.add(d4.getAcceleration().mul(Time.getDelta()));
    }

    //A variation of RK4 that calculates velocity from momentum instead of acceleration
    //This lines up more closely with how rotational velocities are calculated, though this method is just for linear velocities
    private void advanceWithRK4WithMomentum() {
        Debug.println("%s    %s    %s", Time.getDelta(), currentState.position, currentState.velocity);

        Derivative d0 = new Derivative();
        Derivative d1 = calculateDerivativeWithMomentum(currentState.velocity, Time.getDelta() * 0.0f, d0);
        Derivative d2 = calculateDerivativeWithMomentum(currentState.velocity, Time.getDelta() * 0.5f, d1);
        Derivative d3 = calculateDerivativeWithMomentum(currentState.velocity, Time.getDelta() * 0.5f, d2);
        Derivative d4 = calculateDerivativeWithMomentum(currentState.velocity, Time.getDelta() * 1.0f, d3);

        d2.add(d3).mul(2.0f);
        d4.add(d1).add(d2).mul(1.0f / 6.0f);

        currentState.position.add(d4.getVelocity().mul(Time.getDelta()));
        currentState.velocity.add(d4.getAcceleration().mul(Time.getDelta()));
    }

    private Derivative calculateDerivative(Vector3f velocity, float delta, Derivative source) {
        Vector3f newVelocity = new Vector3f();
        source.getAcceleration().mul(delta, newVelocity);
        newVelocity.add(velocity);

        Vector3f newAcceleration = new Vector3f();
        force.div(mass, newAcceleration);

        return new Derivative(newVelocity, newAcceleration);
    }

    private Derivative calculateDerivativeWithMomentum(Vector3f velocity, float delta, Derivative source) {
        Vector3f newVelocity = new Vector3f();
        source.getAcceleration().mul(delta, newVelocity);
        newVelocity.add(velocity);

        return new Derivative(newVelocity, force);
    }

    public void interpolate(float alpha) {
        interpolatedState.interpolate(currentState, previousState, alpha);
        //Debug.println("interpolated %s    %s    %s", alpha, interpolatedState.position, interpolatedState.velocity);
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void addForce(Vector3f force) {
        this.force.add(force);
    }

    public void addTorque(Vector3f torque) {
        this.torque.add(torque);
    }

    public Vector3f getPosition() {
        return currentState.position;
    }

    public void setPosition(float x, float y, float z) {
        currentState.position.set(x, y, z);
        currentState.previousPosition.set(x, y, z);
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

    public Vector3f getRenderPosition() {
        return interpolatedState.position;
    }

    public Quaternionf getRenderOrientation() {
        return interpolatedState.orientation;
    }

    public float getRenderScale() {
        return interpolatedState.scale;
    }
}
