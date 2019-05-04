package com.base.engine.physics.collision;

import com.base.engine.Debug;
import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

public class CollisionResolver {
    //TODO: Implement sequential solver for multiple contact point collisions. Current code is a lazy solution
    // Only solves for one contact point using a break point, so the system won't build velocity
    public void resolveCollisionsLinearly(CollisionIsland island, Manifold data) {
        Body colliderA = island.getColliderA();
        Body colliderB = island.getColliderB();

        for (int i = 0; i < data.getContactPoints().size(); i++) {
            ContactPoint point = data.getContactPoints().get(i);
            point.calculateData();

            Vector3f pointVelocity = getRelativePointVelocity(colliderA, colliderB, point);

            float impulseTerm1 = pointVelocity.dot(data.getEnterNormal());
            float impulseTerm2 = colliderA.getInverseMass() + colliderB.getInverseMass();
            float bounciness = getImpulseBounce(colliderA, colliderB);
            float impulse = bounciness * (impulseTerm1 / impulseTerm2);

            applyImpulses(colliderA, colliderB, impulse, point, data.getEnterNormal(), false);
            break;
        }
    }

    //TODO: Implement sequential solver for multiple contact point collisions here too
    // Current system builds velocity since it solves for each contact point
    public void resolveCollisions(CollisionIsland island, Manifold data) {
        Body colliderA = island.getColliderA();
        Body colliderB = island.getColliderB();

        for (int i = 0; i < data.getContactPoints().size(); i++) {
            ContactPoint point = data.getContactPoints().get(i);
            point.calculateData();

            Vector3f pointBodySpaceA = new Vector3f();
            Vector3f pointBodySpaceB = new Vector3f();
            Vector3f perpendicularNormalA = new Vector3f();
            Vector3f perpendicularNormalB = new Vector3f();
            Vector3f inertiaNormalA = new Vector3f();
            Vector3f inertiaNormalB = new Vector3f();

            Vector3f pointVelocity = getRelativePointVelocity(colliderA, colliderB, point);
            point.getPosition().mulPosition(colliderA.getLocalTransform(), pointBodySpaceA);
            point.getPosition().mulPosition(colliderB.getLocalTransform(), pointBodySpaceB);
            pointBodySpaceA.cross(data.getEnterNormal(), perpendicularNormalA);
            pointBodySpaceB.cross(data.getEnterNormal(), perpendicularNormalB);

            float angularPointVelocityA = perpendicularNormalA.dot(colliderA.getAngularVelocity());
            float angularPointVelocityB = perpendicularNormalB.dot(colliderB.getAngularVelocity());

            float relativeVelocity = pointVelocity.dot(data.getEnterNormal()) + angularPointVelocityA - angularPointVelocityB;

            colliderA.transformInverseInertiaToWorld().transform(perpendicularNormalA, inertiaNormalA);
            colliderB.transformInverseInertiaToWorld().transform(perpendicularNormalB, inertiaNormalB);
            float inertiaImpulseA = perpendicularNormalA.dot(inertiaNormalA);
            float inertiaImpulseB = perpendicularNormalB.dot(inertiaNormalB);

            float kValue = colliderA.getInverseMass() + colliderB.getInverseMass() + inertiaImpulseA + inertiaImpulseB;

            float bounciness = getImpulseBounce(colliderA, colliderB);
            float impulse = bounciness * (relativeVelocity / kValue);

            applyImpulses(colliderA, colliderB, impulse, point, data.getEnterNormal());
            break;
        }
    }

    public void correctPositions(CollisionIsland island, Manifold collisionData) {
        float allowance = 0.01f;
        float correctionAmount = 0.8f;
        Body colliderA = island.getColliderA();
        Body colliderB = island.getColliderB();

        Vector3f correction = new Vector3f();
        collisionData.getEnterNormal().mul(Math.min(collisionData.getPenetration() - allowance, 0.0f) / (colliderA.getInverseMass() + colliderB.getInverseMass()) * correctionAmount, correction);
        colliderA.setPosition(colliderA.getPosition().sub(correction.mul(colliderA.getInverseMass())));
        colliderB.setPosition(colliderB.getPosition().add(correction.mul(colliderB.getInverseMass())));
    }

    private Vector3f getRelativePointVelocity(Body colliderA, Body colliderB, ContactPoint point) {
        Vector3f pointVelocity = new Vector3f();
        colliderA.getVelocityAtPoint(point.getPosition()).sub(colliderB.getVelocityAtPoint(point.getPosition()), pointVelocity);
        return pointVelocity;
    }

    private float getImpulseBounce(Body colliderA, Body colliderB) {
        return -(1.0f + Math.min(colliderA.getRestitution(), colliderB.getRestitution()));
    }

    private void applyImpulses(Body colliderA, Body colliderB, float impulse, ContactPoint point, Vector3f normal) {
        applyImpulses(colliderA, colliderB, impulse, point, normal, true);
    }

    private void applyImpulses(Body colliderA, Body colliderB, float impulse, ContactPoint point, Vector3f normal, boolean enableRotation) {
        Vector3f invertedNormal = new Vector3f();
        normal.negate(invertedNormal);

        float impulseA = impulse * colliderA.getInverseMass();
        float impulseB = impulse * colliderB.getInverseMass();
        Debug.println("Impulses are %s and %s", impulseA, impulseB);
        applyLinearCollisionImpulse(colliderA, impulseA, normal);
        applyLinearCollisionImpulse(colliderB, impulseB, invertedNormal);
        if(enableRotation) {
            applyAngularCollisionImpulse(colliderA, impulseA, point, normal);
            applyAngularCollisionImpulse(colliderB, impulseB, point, invertedNormal);
        }
    }

    private void applyLinearCollisionImpulse(Body collider, float impulse, Vector3f normal) {
        Vector3f linearImpulse = new Vector3f();

        normal.mul(impulse, linearImpulse);
        collider.addImpulse(linearImpulse);
    }

    private void applyAngularCollisionImpulse(Body collider, float impulse, ContactPoint point, Vector3f normal) {
        Vector3f relativePosition = new Vector3f();
        Vector3f positionalImpulse = new Vector3f();
        Vector3f angularImpulse = new Vector3f();
        Vector3f normalImpulse = new Vector3f();

        point.getPosition().mulPosition(collider.getLocalTransform(), relativePosition);
        normal.mul(impulse, normalImpulse);
        normalImpulse.cross(relativePosition, positionalImpulse);
        collider.transformInverseInertiaToWorld().transform(positionalImpulse, angularImpulse);
        collider.addAngularImpulse(angularImpulse);
    }
}
