package com.base.engine.physics.collision;

public class SimplexEdge {
    private SimplexSupportPoint[] points = new SimplexSupportPoint[2];

    public SimplexEdge(SimplexSupportPoint supportA, SimplexSupportPoint supportB) {
        points[0] = supportA;
        points[1] = supportB;
    }

    public SimplexSupportPoint getPoint(int index) {
        return points[index];
    }

    public boolean isOpposingEdge(SimplexSupportPoint pointA, SimplexSupportPoint pointB) {
        if(points[0].getPoint().equals(pointB.getPoint()) && points[1].getPoint().equals(pointA.getPoint())) {
            return true;
        }
        return false;
    }
}
