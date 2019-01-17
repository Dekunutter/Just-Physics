package com.base.engine.physics.springs;

import com.base.engine.GameObject;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.game.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DistanceJoint extends GameObject {
    private Vector3f attachedAtA, attachedAtB;
    private Matrix4f transform;
    private float restLength, elasticity, damping;
    private Body attachedToA, attachedToB;

    public DistanceJoint(World world, float restLength, Vector3f attachedPositionA, Vector3f attachedPositionB, float elasticity, float damping, Body attachToA, Body attachToB) {
        super(world);

        this.restLength = restLength;
        transform = new Matrix4f();
        this.attachedAtA = attachedPositionA;
        this.attachedAtB = attachedPositionB;
        this.elasticity = elasticity;
        this.damping = damping;
        attachA(attachToA);
        attachB(attachToB);
    }

    public void attachA(Body attachTo) {
        attachedToA = attachTo;
    }

    public void attachB(Body attachTo) {
        attachedToB = attachTo;
    }

    @Override
    public void getInput() {

    }

    @Override
    public void update(Integration integrationType) {
        Vector3f worldPointA = new Vector3f();
        Vector3f worldPointB = new Vector3f();
        Vector3f currentLengthA = new Vector3f();
        Vector3f currentLengthB = new Vector3f();
        float lengthA = 0, lengthB = 0;
        Vector3f directionA = new Vector3f();
        Vector3f directionB = new Vector3f();
        Vector3f forceA = new Vector3f();
        Vector3f forceB = new Vector3f();

        attachedAtA.mulPosition(attachedToA.getWorldTransform(), worldPointA);
        attachedAtB.mulPosition(attachedToB.getWorldTransform(), worldPointB);
        worldPointA.sub(worldPointB, currentLengthA);
        worldPointB.sub(worldPointA, currentLengthB);
        lengthA = currentLengthA.length();
        lengthB = currentLengthB.length();

        lengthA -= restLength;
        lengthB -= restLength;

        currentLengthA.normalize(directionA);
        currentLengthB.normalize(directionB);
        directionA.mul(lengthA);
        directionA.mul(-elasticity);
        directionB.mul(lengthB);
        directionB.mul(-elasticity);

        Vector3f pointVelocityA = attachedToA.getVelocityAtPoint(worldPointA);
        pointVelocityA.mul(damping);
        Vector3f pointVelocityB = attachedToB.getVelocityAtPoint(worldPointB);
        pointVelocityB.mul(damping);

        directionA.sub(pointVelocityA, forceA);
        directionB.sub(pointVelocityB, forceB);

        attachedToA.addForce(forceA, worldPointA);
        attachedToB.addForce(forceB, worldPointB);
    }

    @Override
    public void interpolate(float alpha) {

    }

    @Override
    public void render() {

    }

    @Override
    public void cleanUp() {

    }
}
