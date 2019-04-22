package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

public class CollisionResolver {
    //TODO: Implement sequential solver for multiple contact point collisions. Current code is a lazy solution
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

            applyImpulses(colliderA, colliderA, impulse, point, data.getEnterNormal());
        }
    }

    //TODO: Implement sequential solver for multiple contact point collisions here too
    public void resolveCollisions(CollisionIsland island, Manifold data) {
        Body colliderA = island.getColliderA();
        Body colliderB = island.getColliderB();

        for (int i = 0; i < data.getContactPoints().size(); i++) {
            ContactPoint point = data.getContactPoints().get(i);
            point.calculateData();

            Vector3f pointBodySpaceA = new Vector3f();
            Vector3f pointBodySpaceB = new Vector3f();
            Vector3f pointXNormalA = new Vector3f();
            Vector3f pointXNormalB = new Vector3f();
            Vector3f pointXNormalXPointA = new Vector3f();
            Vector3f pointXNormalXPointB = new Vector3f();
            Vector3f inertiaImpulseA = new Vector3f();
            Vector3f inertiaImpulseB = new Vector3f();

            Vector3f pointVelocity = getRelativePointVelocity(colliderA, colliderB, point);
            point.getPosition().mulPosition(colliderA.getLocalTransform(), pointBodySpaceA);
            point.getPosition().mulPosition(colliderB.getLocalTransform(), pointBodySpaceB);
            pointBodySpaceA.cross(data.getEnterNormal(), pointXNormalA);
            pointBodySpaceB.cross(data.getEnterNormal(), pointXNormalB);

            float angularPointVelocityA = pointXNormalA.dot(colliderA.getAngularVelocity());
            float angularPointVelocityB = pointXNormalB.dot(colliderB.getAngularVelocity());

            float impulseTerm1 = pointVelocity.dot(data.getEnterNormal());

            pointXNormalA.cross(pointBodySpaceA, pointXNormalXPointA);
            pointXNormalB.cross(pointBodySpaceB, pointXNormalXPointB);
            pointXNormalXPointA.mul(colliderA.getInverseInertia(), inertiaImpulseA);
            pointXNormalXPointB.mul(colliderB.getInverseInertia(), inertiaImpulseB);

            float impulseTerm2 = colliderA.getInverseMass() + colliderB.getInverseMass() + inertiaImpulseA.add(inertiaImpulseB).dot(data.getEnterNormal());

            float bounciness = getImpulseBounce(colliderA, colliderB);
            float impulse = bounciness * (impulseTerm1 / impulseTerm2);

            applyImpulses(colliderA, colliderB, impulse, point, data.getEnterNormal());
        }
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
        Vector3f invertedNormal = new Vector3f();
        Vector3f directedImpulseA = new Vector3f();
        Vector3f directedImpulseB = new Vector3f();

        normal.negate(invertedNormal);
        normal.mul(impulse / colliderA.getMass(), directedImpulseA);
        invertedNormal.mul(impulse / colliderB.getMass(), directedImpulseB);

        colliderA.addImpulse(directedImpulseA, point.getPosition());
        colliderB.addImpulse(directedImpulseB, point.getPosition());
    }
}
