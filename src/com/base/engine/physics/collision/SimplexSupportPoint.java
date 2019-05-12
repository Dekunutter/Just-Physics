package com.base.engine.physics.collision;

import org.joml.Vector3f;

import java.util.Objects;

public class SimplexSupportPoint {
    private Vector3f supportA, supportB, minkowskiDifference;

    public SimplexSupportPoint(Vector3f supportA, Vector3f supportB) {
        this.supportA = supportA;
        this.supportB = supportB;
        minkowskiDifference = new Vector3f();
        supportA.sub(supportB, minkowskiDifference);
    }

    public SimplexSupportPoint(Vector3f supportA, Vector3f supportB, Vector3f minkowskiDifference) {
        this.supportA = supportA;
        this.supportB = supportB;
        this.minkowskiDifference = minkowskiDifference;
    }

    public Vector3f getPoint() {
        return minkowskiDifference;
    }

    public Vector3f getSupportA() {
        return supportA;
    }

    public Vector3f getSupportB() {
        return supportB;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof SimplexSupportPoint)) {
            return false;
        }

        SimplexSupportPoint otherPoint = (SimplexSupportPoint) other;
        if(otherPoint.supportA.equals(supportA) && otherPoint.supportB.equals(supportB) && otherPoint.minkowskiDifference.equals(minkowskiDifference)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 6;
        hash = 79 * hash + Objects.hashCode(this.minkowskiDifference);
        return hash;
    }
}
