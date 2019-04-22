package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;

import java.util.Objects;

public class CollisionIsland {
    private Body colliderA, colliderB;

    public CollisionIsland(Body colliderA, Body colliderB) {
        this.colliderA = colliderA;
        this.colliderB = colliderB;
    }

    public Body getColliderA() {
        return colliderA;
    }

    public Body getColliderB() {
        return colliderB;
    }

    @Override
    public boolean equals(Object other) {
        CollisionIsland otherIsland = (CollisionIsland) other;

        if(colliderA.equals(otherIsland.getColliderA()) && colliderB.equals(otherIsland.getColliderB())) {
            return true;
        }
        else if(colliderA.equals(otherIsland.getColliderB()) && colliderB.equals(otherIsland.getColliderA())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hash(colliderA, colliderB);
        return hash;
    }
}
