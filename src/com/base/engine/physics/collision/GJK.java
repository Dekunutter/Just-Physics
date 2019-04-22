package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

public class GJK implements CollisionAlgorithm {
    private Simplex simplex;

    public Manifold detect(CollisionIsland island) {
        simplex = new Simplex();
        Manifold results = new Manifold(island.getColliderA(), island.getColliderB());
        evolveSimplex(island.getColliderA(), island.getColliderB(), results);
        return results;
    }

    public void evolveSimplex(Body reference, Body incident, Manifold results) {
        Vector3f referenceCenter = new Vector3f();
        Vector3f incidentCenter = new Vector3f();
        Vector3f direction = new Vector3f();
        Vector3f lineAB;
        Vector3f lineOrigin = new Vector3f();
        Vector3f lineAC;
        switch(simplex.getSize()) {
            case 0:
                reference.getPosition().mulPosition(reference.getWorldTransform(), referenceCenter);
                incident.getPosition().mulPosition(incident.getWorldTransform(), incidentCenter);
                incidentCenter.sub(referenceCenter, direction);
                break;
            case 1:
                reference.getPosition().mulPosition(reference.getWorldTransform(), referenceCenter);
                incident.getPosition().mulPosition(incident.getWorldTransform(), incidentCenter);
                incidentCenter.sub(referenceCenter, direction);
                direction.negate();
                break;
            case 2:
                lineAB = simplex.getFirstLine();
                simplex.getPoint(0).negate(lineOrigin);
                Vector3f abXao = new Vector3f();
                lineAB.cross(lineOrigin, abXao);
                abXao.cross(lineAB, direction);
                break;
            case 3:
                lineAC = simplex.getSecondLine();
                lineAB = simplex.getFirstLine();
                lineAC.cross(lineAB, direction);
                simplex.getPoint(0).negate(lineOrigin);
                if(direction.dot(lineOrigin) < 0) {
                    direction.negate();
                }
                break;
            case 4:
                Vector3f lineDA = simplex.getEdgeA();
                Vector3f lineDB = simplex.getEdgeB();
                Vector3f lineDC = simplex.getEdgeC();
                Vector3f dOrigin = new Vector3f();
                simplex.getPoint(3).negate(dOrigin);

                Vector3f abd = new Vector3f();
                Vector3f bcd = new Vector3f();
                Vector3f cad = new Vector3f();
                lineDA.cross(lineDB, abd);
                lineDB.cross(lineDC, bcd);
                lineDC.cross(lineDA, cad);

                if(abd.dot(dOrigin) > 0) {
                    simplex.removeVertex(2);
                    direction = new Vector3f(abd);
                } else if(bcd.dot(dOrigin) > 0) {
                    simplex.removeVertex(0);
                    direction = new Vector3f(bcd);
                } else if(cad.dot(dOrigin) > 0) {
                    simplex.removeVertex(1);
                    direction = new Vector3f(cad);
                } else {
                    results.setOverlapped();
                    return;
                }
                break;
            default:
                System.err.print("GJK evolved an invalid simplex. Simplex has " + simplex.getSize() + " lines, above the max of 4");
                return;
        }
        Vector3f newPoint = getMinkowskiDifference(reference, incident, direction);
        if(addToSimplex(newPoint, direction)) {
            evolveSimplex(reference, incident, results);
        }
        return;
    }

    private Vector3f getMinkowskiDifference(Body bodyA, Body bodyB, Vector3f direction) {
        Vector3f directionA = new Vector3f();
        Vector3f directionB = new Vector3f();
        direction.normalize(directionA);
        directionA.negate(directionB);

        Vector3f result = new Vector3f();
        bodyA.getSupport(directionA).sub(bodyB.getSupport(directionB), result);
        return result;
    }

    private boolean addToSimplex(Vector3f point, Vector3f direction) {
        if(!hasPassedOrigin(point, direction)) {
            return false;
        }
        simplex.setPoint(point);
        return true;
    }

    private boolean hasPassedOrigin(Vector3f point, Vector3f direction) {
        if(point.dot(direction) < 0) {
            return false;
        }
        return true;
    }
}
