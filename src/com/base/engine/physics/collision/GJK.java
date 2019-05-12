package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
                    getCollisionInformation(results);
                    return;
                }
                break;
            default:
                System.err.print("GJK evolved an invalid simplex. Simplex has " + simplex.getSize() + " lines, above the max of 4");
                return;
        }
        SimplexSupportPoint newPoint = getMinkowskiDifference(reference, incident, direction);
        if(addToSimplex(newPoint, direction)) {
            evolveSimplex(reference, incident, results);
        }
        return;
    }

    private SimplexSupportPoint getMinkowskiDifference(Body bodyA, Body bodyB, Vector3f direction) {
        Vector3f directionA = new Vector3f();
        Vector3f directionB = new Vector3f();
        direction.normalize(directionA);
        directionA.negate(directionB);

        Vector3f supportA = bodyA.getSupport(directionA);
        Vector3f supportB = bodyB.getSupport(directionB);
        return new SimplexSupportPoint(supportA, supportB);
    }

    private boolean addToSimplex(SimplexSupportPoint point, Vector3f direction) {
        if(!hasPassedOrigin(point.getPoint(), direction)) {
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

    private void getCollisionInformation(Manifold results) {
        List<SimplexTriangle> triangles = new ArrayList<>();
        List<SimplexEdge> edges = new ArrayList<>();

        triangles.add(new SimplexTriangle(simplex.getSupport(0), simplex.getSupport(1), simplex.getSupport(2)));
        triangles.add(new SimplexTriangle(simplex.getSupport(0), simplex.getSupport(2), simplex.getSupport(3)));
        triangles.add(new SimplexTriangle(simplex.getSupport(0), simplex.getSupport(3), simplex.getSupport(1)));
        triangles.add(new SimplexTriangle(simplex.getSupport(1), simplex.getSupport(3), simplex.getSupport(2)));

        while(true) {
            float minimumDistance = Float.MAX_VALUE;
            SimplexTriangle closestFace = null;
            for (int i = 0; i < triangles.size(); i++) {
                float distance = Math.abs(triangles.get(i).getNormal().dot(triangles.get(i).getPoint(0).getPoint()));
                if (distance < minimumDistance) {
                    closestFace = triangles.get(i);
                    minimumDistance = distance;
                }
            }

            SimplexSupportPoint furthestPoint = getMinkowskiDifference(results.getReferenceBody(), results.getIncidentBody(), closestFace.getNormal());
            float newDistance = closestFace.getNormal().dot(furthestPoint.getPoint());
            if(newDistance - minimumDistance < 0.000001f) {
                Vector3f contactNormal = new Vector3f();
                closestFace.getNormal().negate(contactNormal);
                float penetration = minimumDistance;

                results.setEnterNormal(contactNormal);
                results.setPenetration(penetration);
                break;
            }

            List<SimplexTriangle> toRemove = new ArrayList<>();
            for(int i = 0; i < triangles.size(); i++) {
                Vector3f distanceToTriangle = new Vector3f();
                furthestPoint.getPoint().sub(triangles.get(i).getPoint(0).getPoint(), distanceToTriangle);
                if(triangles.get(i).getNormal().dot(distanceToTriangle) > 0) {
                    addSimplexEdge(edges, triangles.get(i).getPoint(0), triangles.get(i).getPoint(1));
                    addSimplexEdge(edges, triangles.get(i).getPoint(1), triangles.get(i).getPoint(2));
                    addSimplexEdge(edges, triangles.get(i).getPoint(2), triangles.get(i).getPoint(0));
                    toRemove.add(triangles.get(i));
                }
            }
            triangles.removeAll(toRemove);

            for(int i = 0; i < edges.size(); i++) {
                triangles.add(new SimplexTriangle(furthestPoint, edges.get(i).getPoint(0), edges.get(i).getPoint(1)));
            }
            edges.clear();
        }
    }

    private void addSimplexEdge(List<SimplexEdge> edges, SimplexSupportPoint pointA, SimplexSupportPoint pointB) {
        SimplexEdge toRemove = null;
        for(int i = 0; i < edges.size(); i++) {
            if(edges.get(i).isOpposingEdge(pointA, pointB)) {
                toRemove = edges.get(i);
                break;
            }
        }
        if(toRemove == null) {
            edges.add(new SimplexEdge(pointA, pointB));
        } else {
            edges.remove(toRemove);
        }
    }
}
