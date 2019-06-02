package com.base.engine.physics.collision;

import com.base.engine.Debug;
import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GJK implements CollisionAlgorithm {
    private static final int MAX_ITERATIONS = 50;

    private Simplex simplex;
    private Vector3f direction;

    @Override
    public Manifold detect(CollisionIsland island) {
        simplex = new Simplex();
        direction = new Vector3f(1, 1, 1);
        Manifold results = new Manifold(island.getColliderA(), island.getColliderB());

        simplex.setPoint(2, support(island.getColliderA(), island.getColliderB(), direction));
        simplex.getPointCoordinates(2).negate(direction);

        simplex.setPoint(1, support(island.getColliderA(), island.getColliderB(), direction));
        if(simplex.getPointCoordinates(1).dot(direction) < 0) {
            return results;
        }

        Vector3f cXb = new Vector3f();
        Vector3f negatedB = new Vector3f();
        simplex.getPointCoordinates(1).negate(negatedB);
        Vector3f cToB = simplex.getLine(2, 1);
        cToB.cross(negatedB, cXb);
        cXb.cross(cToB, direction);

        int iterationCount = 0;
        while(iterationCount < MAX_ITERATIONS) {
            simplex.setPoint(0, support(island.getColliderA(), island.getColliderB(), direction));
            if(simplex.getPointCoordinates(0).dot(direction) < 0) {
                return results;
            } else {
                if(containsOrigin()) {
                    results.setOverlapped();
                    getCollisionInformation(results);
                    return results;
                }
            }
            iterationCount++;
        }
        return results;
    }

    private SimplexSupportPoint support(Body reference, Body incident, Vector3f direction) {
        Vector3f supportA = reference.getSupport(direction);
        Vector3f negatedDirection = new Vector3f();
        direction.negate(negatedDirection);
        Vector3f supportB = incident.getSupport(negatedDirection);
        SimplexSupportPoint support = new SimplexSupportPoint(supportA, supportB);
        return support;
    }

    private boolean containsOrigin() {
        Vector3f lineOrigin = new Vector3f();
        simplex.getPointCoordinates(0).negate(lineOrigin);

        if(simplex.getSize() == 2) {
            return doesLineContainOrigin(lineOrigin);
        } else if(simplex.getSize() == 3) {
            return doesTriangleContainOrigin(lineOrigin);
        } else if(simplex.getSize() == 4) {
            return doesTetrahedronContainOrigin(lineOrigin);
        } else {
            simplex.devolve();
        }
        return false;
    }

    private boolean doesLineContainOrigin(Vector3f lineOrigin) {
        Vector3f lineAB = simplex.getLine(1, 0);

        Vector3f lineDirection = new Vector3f();
        lineAB.cross(lineOrigin, lineDirection);
        lineDirection.cross(lineAB, direction);

        simplex.copyPoint(2, 1);
        simplex.copyPoint(1, 0);

        return false;
    }

    private boolean doesTriangleContainOrigin(Vector3f lineOrigin) {
        Vector3f faceDirection = new Vector3f();

        Vector3f lineAB = simplex.getLine(1, 0);
        Vector3f lineAC = simplex.getLine(2, 0);
        lineAB.cross(lineAC, faceDirection);

        Vector3f directionToAB = new Vector3f();
        lineAB.cross(faceDirection, directionToAB);
        if(directionToAB.dot(lineOrigin) > 0) {
            simplex.copyPoint(2, 1);
            simplex.copyPoint(1, 0);
            Vector3f lineDirection = new Vector3f();
            lineAB.cross(lineOrigin, lineDirection);
            lineDirection.cross(lineAB, direction);
            simplex.devolve();
            return false;
        }

        Vector3f directionFromAC = new Vector3f();
        faceDirection.cross(lineAC, directionFromAC);
        if(directionFromAC.dot(lineOrigin) > 0) {
            simplex.copyPoint(1, 0);
            Vector3f lineDirection = new Vector3f();
            lineAC.cross(lineOrigin, lineDirection);
            lineDirection.cross(lineAC, direction);
            simplex.devolve();
            return false;
        }

        if(faceDirection.dot(lineOrigin) > 0) {
            simplex.copyPoint(3, 2);
            simplex.copyPoint(2, 1);
            simplex.copyPoint(1, 0);
            direction = new Vector3f(faceDirection);
        } else {
            simplex.copyPoint(3, 1);
            simplex.copyPoint(1, 0);
            direction = new Vector3f(faceDirection.negate());
        }
        return false;
    }

    private boolean doesTetrahedronContainOrigin(Vector3f lineOrigin) {
        Vector3f lineAB = simplex.getLine(1, 0);
        Vector3f lineAC = simplex.getLine(2, 0);

        Vector3f faceDirectionABC = new Vector3f();
        lineAB.cross(lineAC, faceDirectionABC);
        if(faceDirectionABC.dot(lineOrigin) > 0) {
            return checkTetrahedronFace(lineOrigin, lineAB, lineAC, faceDirectionABC);
        }

        Vector3f faceDirectionACD = new Vector3f();
        Vector3f lineAD = simplex.getLine(3, 0);
        lineAC.cross(lineAD, faceDirectionACD);
        if(faceDirectionACD.dot(lineOrigin) > 0) {
            simplex.copyPoint(1, 2);
            simplex.copyPoint(2, 3);
            lineAB = new Vector3f(lineAC);
            lineAC = new Vector3f(lineAD);
            faceDirectionABC = new Vector3f(faceDirectionACD);
            return checkTetrahedronFace(lineOrigin, lineAB, lineAC, faceDirectionABC);
        }

        Vector3f faceDirectionADB = new Vector3f();
        lineAD.cross(lineAB, faceDirectionADB);
        if(faceDirectionADB.dot(lineOrigin) > 0) {
            simplex.copyPoint(2, 1);
            simplex.copyPoint(3, 1);
            lineAC = new Vector3f(lineAB);
            lineAB = new Vector3f(lineAD);
            faceDirectionABC = new Vector3f(faceDirectionADB);
            return checkTetrahedronFace(lineOrigin, lineAB, lineAC, faceDirectionABC);
        }

        return true;
    }

    private boolean checkTetrahedronFace(Vector3f lineOrigin, Vector3f lineAB, Vector3f lineAC, Vector3f faceDirection) {
        Vector3f directionFromAB = new Vector3f();

        lineAB.cross(faceDirection, directionFromAB);
        if(directionFromAB.dot(lineOrigin) > 0) {
            simplex.copyPoint(2, 1);
            simplex.copyPoint(1, 0);
            Vector3f lineDirection = new Vector3f();
            lineAB.cross(lineOrigin, lineDirection);
            lineDirection.cross(lineAB, direction);
            simplex.devolve();
            simplex.devolve();
            return false;
        }

        Vector3f abcac = new Vector3f();
        faceDirection.cross(lineAC, abcac);
        if(abcac.dot(lineOrigin) > 0) {
            simplex.copyPoint(1, 0);
            Vector3f lineDirection = new Vector3f();
            lineAC.cross(lineOrigin, lineDirection);
            lineDirection.cross(lineAC, direction);
            simplex.devolve();
            simplex.devolve();
            return false;
        }

        simplex.copyPoint(3, 2);
        simplex.copyPoint(2, 1);
        simplex.copyPoint(1, 0);
        direction = new Vector3f(faceDirection);
        simplex.devolve();
        return false;
    }

    private void getCollisionInformation(Manifold results) {
        List<SimplexTriangle> triangles = new ArrayList<>();
        List<SimplexEdge> edges = new ArrayList<>();

        triangles.add(new SimplexTriangle(simplex.getPoint(0), simplex.getPoint(1), simplex.getPoint(2)));
        triangles.add(new SimplexTriangle(simplex.getPoint(0), simplex.getPoint(2), simplex.getPoint(3)));
        triangles.add(new SimplexTriangle(simplex.getPoint(0), simplex.getPoint(3), simplex.getPoint(1)));
        triangles.add(new SimplexTriangle(simplex.getPoint(1), simplex.getPoint(3), simplex.getPoint(2)));

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

            SimplexSupportPoint furthestPoint = support(results.getReferenceBody(), results.getIncidentBody(), closestFace.getNormal());
            float newDistance = closestFace.getNormal().dot(furthestPoint.getPoint());
            if(newDistance - minimumDistance < 0.000001f) {
                Vector3f barycentre = closestFace.getBarycentricCoordinate();
                Vector3f worldSupportA = new Vector3f(closestFace.getPoint(0).getSupportA());
                Vector3f worldSupportB = new Vector3f(closestFace.getPoint(1).getSupportA());
                Vector3f worldSupportC = new Vector3f(closestFace.getPoint(2).getSupportA());
                Vector3f contactPoint = new Vector3f(worldSupportA.mul(barycentre.x).add(worldSupportB.mul(barycentre.y)).add(worldSupportC.mul(barycentre.z)));
                Vector3f contactNormal = new Vector3f();
                closestFace.getNormal().negate(contactNormal);
                float penetration = minimumDistance;

                ContactPoint trueContactPoint = new ContactPoint(contactPoint, contactNormal, penetration, results.getReferenceBody(), results.getIncidentBody());

                results.addContactPoint(trueContactPoint);
                results.setEnterNormal(contactNormal);
                results.setPenetration(penetration);

                Debug.addContactPoint(trueContactPoint);
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
