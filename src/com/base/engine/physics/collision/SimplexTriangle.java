package com.base.engine.physics.collision;

import org.joml.Vector3f;

public class SimplexTriangle {
    private SimplexSupportPoint[] points = new SimplexSupportPoint[3];
    private Vector3f normal;

    public SimplexTriangle(SimplexSupportPoint supportA, SimplexSupportPoint supportB, SimplexSupportPoint supportC) {
        points[0] = supportA;
        points[1] = supportB;
        points[2] = supportC;
        normal = new Vector3f();

        Vector3f distanceBA = new Vector3f();
        Vector3f distanceCA = new Vector3f();

        supportB.getPoint().sub(supportA.getPoint(), distanceBA);
        supportC.getPoint().sub(supportA.getPoint(), distanceCA);
        distanceBA.cross(distanceCA, normal);
        normal.normalize();
    }

    public Vector3f getNormal() {
        return normal;
    }

    public SimplexSupportPoint getPoint(int index) {
        return points[index];
    }

    public Vector3f getBarycentricCoordinate() {
        Vector3f direction = new Vector3f();
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();

        float distance = normal.dot(points[0].getPoint());
        normal.mul(distance, direction);
        points[1].getPoint().sub(points[0].getPoint(), v0);
        points[2].getPoint().sub(points[0].getPoint(), v1);
        direction.sub(points[0].getPoint(), v2);
        float d00 = v0.dot(v0);
        float d01 = v0.dot(v1);
        float d11 = v1.dot(v1);
        float d20 = v2.dot(v0);
        float d21 = v2.dot(v1);
        float demoninator = (d00 * d11) - (d01 * d01);

        float v = ((d11 * d20) - (d01 * d21)) / demoninator;
        float w = ((d00 * d21) - (d01 * d20)) / demoninator;
        float u = 1.0f - v - w;
        return new Vector3f(u, v, w);
    }
}
