package com.base.engine.physics;

import com.base.engine.Debug;
import com.base.engine.Time;
import org.joml.Vector3f;

public class Body {
    private Vector3f position, velocity, acceleration, force;
    private float mass;
    private Vector3f previousPosition;

    public Body() {
        position = new Vector3f();
        velocity = new Vector3f();
        previousPosition = new Vector3f();
        acceleration = new Vector3f();
        force = new Vector3f();

        mass = 1.0f;
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
        Debug.println("%s    %s    %s", Time.getDelta(), position, velocity);

        Vector3f velocityOverTime = new Vector3f();
        Vector3f accelerationOverTime = new Vector3f();

        position.add(velocity.mul(Time.getDelta(), velocityOverTime));
        force.div(mass, acceleration);
        velocity.add(acceleration.mul(Time.getDelta(), accelerationOverTime));
    }

    //What makes the difference between explicit and semi-explicity Euler is simply the ordering
    //The ordering is important and solves spring dampening issues
    //Fixes the spring damper system issue that explicit euler has but has some issues with stiff equations
    private void advanceSemiImplicitly() {
        Debug.println("%s    %s    %s", Time.getDelta(), position, velocity);

        Vector3f velocityOverTime = new Vector3f();
        Vector3f accelerationOverTime = new Vector3f();

        force.div(mass, acceleration);
        velocity.add(acceleration.mul(Time.getDelta(), accelerationOverTime));
        position.add(velocity.mul(Time.getDelta(), velocityOverTime));
    }

    //Fixes the issues with stiff equations that other euler methods have at the cost of requiring more object state and more complex math
    private void advanceImplicitly() {
        Debug.println("%s    %s    %s", Time.getDelta(), position, velocity);

        force.div(mass, acceleration);

        Vector3f velocityOverTime = new Vector3f(velocity).mul(Time.getDelta());
        Vector3f accelerationOverTime = new Vector3f(acceleration).mul(Time.getDelta());
        Vector3f changeDueToAcceleration = new Vector3f(acceleration).mul(0.5f).mul(Time.getDelta() * Time.getDelta());

        position.add(velocityOverTime).add(changeDueToAcceleration);
        velocity.add(accelerationOverTime);
    }

    //greater accuracy than implicit euler and less memory usage. 2nd order symplectic integrator
    //Great for multiple bodies and such where I don't really care about velocities and can presume them
    //Requires fixed time steps to retain accuracy
    private void advanceWithVerlet() {
        Debug.println("%s    %s     %s      %s", Time.getDelta(), position, velocity, previousPosition);

        Vector3f tempPosition = new Vector3f(position);

        force.div(mass, acceleration);

        Vector3f changeDueToAcceleration = new Vector3f(acceleration).mul(Time.getDelta() * Time.getDelta());
        Vector3f positionDifference = new Vector3f(position).sub(previousPosition);
        Vector3f positionAfterForce = new Vector3f(positionDifference).add(changeDueToAcceleration);

        position.add(positionAfterForce);
        velocity = new Vector3f(positionDifference).div(Time.getDelta());

        previousPosition = new Vector3f(tempPosition);
    }

    //Verlet that also calculates a velocity at the cost of some performance and a little bit of accuracy
    //Requires fixed time steps like standard Verlet
    private void advanceWithVelocityVerlet() {
        Debug.println("%s    %s     %s", Time.getDelta(), position, velocity);

        Vector3f changeDueToAcceleration = new Vector3f(acceleration).mul(Time.getDelta() / 2.0f);
        Vector3f halfVelocity = new Vector3f(velocity);
        halfVelocity.add(changeDueToAcceleration);

        Vector3f halfVelocityOverTime = new Vector3f(halfVelocity).mul(Time.getDelta());
        position.add(halfVelocityOverTime);

        force.div(mass, acceleration);

        changeDueToAcceleration = new Vector3f(acceleration).mul(Time.getDelta() / 2.0f);
        velocity = new Vector3f(halfVelocity).add(changeDueToAcceleration);
    }

    //A time-corrected version of Verlet which grants higher stability for non-fixed time steps at slightly most cost
    //Offers the same level of accuracy for fixed time-step loops as standard Verlet
    private void advanceWithTCV() {
        Debug.println("%s    %s", Time.getDelta(), position);

        Vector3f tempPosition = new Vector3f(position);

        force.div(mass, acceleration);

        Vector3f changeDueToAcceleration = new Vector3f(acceleration).mul(Time.getDelta() * Time.getDelta());
        Vector3f positionDifference = new Vector3f(position).sub(previousPosition);
        Vector3f timeCorrectedDifference = new Vector3f(positionDifference).mul(Time.getDelta() / Time.getPreviousDelta());
        Vector3f positionAfterForce = new Vector3f(timeCorrectedDifference).add(changeDueToAcceleration);

        position.add(positionAfterForce);

        previousPosition = new Vector3f(tempPosition);
    }

    //A more accurate but computationally expensive integrator that loses slight amounts of energy as it goes
    private void advanceWithRK4() {
        Debug.println("%s    %s    %s", Time.getDelta(), position, velocity);

        RK4Derivative d0 = new RK4Derivative();
        RK4Derivative d1 = calculateDerivative(position, velocity, Time.getDelta() * 0.0f, d0);
        RK4Derivative d2 = calculateDerivative(position, velocity, Time.getDelta() * 0.5f, d1);
        RK4Derivative d3 = calculateDerivative(position, velocity, Time.getDelta() * 0.5f, d2);
        RK4Derivative d4 = calculateDerivative(position, velocity, Time.getDelta() * 1.0f, d3);

        d2.add(d3).mul(2.0f);
        d4.add(d1).add(d2).mul(1.0f / 6.0f);

        position.add(d4.getPosition().mul(Time.getDelta()));
        velocity.add(d4.getVelocity().mul(Time.getDelta()));
    }

    private RK4Derivative calculateDerivative(Vector3f position, Vector3f velocity, float delta, RK4Derivative source) {
        Vector3f newPosition = new Vector3f();
        source.getPosition().mul(delta, newPosition);
        newPosition.add(position);
        Vector3f newVelocity = new Vector3f();
        source.getVelocity().mul(delta, newVelocity);
        newVelocity.add(velocity);

        force.div(mass, acceleration);

        return new RK4Derivative(newVelocity, acceleration);
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void addForce(Vector3f force) {
        this.force.add(force);
    }
}
