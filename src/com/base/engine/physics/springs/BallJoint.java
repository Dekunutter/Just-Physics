package com.base.engine.physics.springs;

import com.base.engine.GameObject;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.game.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

//TODO: Should be a different abstract type entirely and be stored separately from objects in the world, might be useful when updating to have springs update first, then objects
public class BallJoint extends GameObject {
    private Vector3f position, attachedAt;
    private Matrix4f transform;
    private float elasticity, damping;
    private Body attachedTo;

    public BallJoint(World world, Vector3f position, Vector3f attachedPosition, float elasticity, float damping, Body attachTo) {
        super(world);

        this.position = position;
        transform = new Matrix4f();
        this.attachedAt = attachedPosition;
        this.elasticity = elasticity;
        this.damping = damping;
        attach(attachTo);
    }

    public void attach(Body attachTo) {
        attachedTo = attachTo;
    }

    @Override
    public void getInput() {

    }

    //TODO: Fix issues with rotational velocity building up instead of dampening like linear when a rotation is applied and the spring is pulling the object simultaneously
    @Override
    public void update(Integration integrationType) {
        Vector3f worldPoint = new Vector3f();
        Vector3f difference = new Vector3f();
        Vector3f force = new Vector3f();

        attachedAt.mulPosition(attachedTo.getWorldTransform(), worldPoint);
        position.sub(worldPoint, difference);
        difference.mul(elasticity);

        Vector3f pointVelocity = attachedTo.getVelocityAtPoint(worldPoint);
        pointVelocity.mul(damping);

        difference.sub(pointVelocity, force);

        attachedTo.addForce(force, worldPoint);

        updateMatrix();
    }

    public void updateMatrix() {
        transform.identity();
        transform.translate(position);
        transform.scale(1);
        transform.rotate(new Quaternionf());
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
