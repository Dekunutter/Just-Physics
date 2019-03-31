package com.base.engine.physics.collision;

import com.base.engine.Debug;
import com.base.engine.physics.body.Body;
import com.base.engine.physics.body.Edge;
import com.base.engine.physics.body.Face;
import com.base.engine.physics.body.Plane;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CollisionDetection {
    private enum Type {
        FACE_OF_A, FACE_OF_B, EDGE
    }

    //TODO: Try out collision detection in bodyA space instead of world space
    public Manifold separatingAxisTheorem(Body bodyA, Body bodyB) {
        Manifold results = new Manifold(bodyA, bodyB);
        if(!queryFaceCollisions(bodyA, bodyB, results, Type.FACE_OF_A)) {
            return results;
        }
        if(!queryFaceCollisions(bodyB, bodyA, results, Type.FACE_OF_B)) {
            return results;
        }
        if(!queryEdgeCollisions(bodyA, bodyB, results, Type.EDGE)) {
            return results;
        }

        if(results.getType() == Type.FACE_OF_A.ordinal()) {
            getFaceContactPoints(bodyA, bodyB, results);
        } else if(results.getType() == Type.FACE_OF_B.ordinal()) {
            getFaceContactPoints(bodyB, bodyA, results);
        } else {
            getEdgeContactPoint(bodyA, bodyB, results);
        }

        Vector3f offset = new Vector3f();
        bodyB.getPosition().sub(bodyA.getPosition(), offset);
        if(results.getEnterNormal().dot(offset) > 0) {
            results.getEnterNormal().negate();
        }

        results.setCollided();

        return results;
    }

    private boolean queryFaceCollisions(Body reference, Body incident, Manifold results, Type collisionType) {
        for(int i = 0; i < reference.getFaceCount(); i++) {
            Vector3f axis = reference.getFace(i).getTransformedNormal(reference.getWorldTransform());
            Vector3f planePoint = reference.getSupport(axis);
            Plane plane = new Plane(axis, planePoint);

            Vector3f negatedNormal = new Vector3f();
            plane.getNormal().negate(negatedNormal);
            Vector3f support = incident.getSupport(negatedNormal);

            float distance = plane.distanceToPoint(support);
            if(distance > 0) {
                return false;
            }
            if(distance > results.getPenetration()) {
                results.setPenetration(distance);
                results.setType(collisionType.ordinal());
                results.setEnterNormal(axis);
                results.setReferenceFace(i);
            }
        }
        return true;
    }

    //TODO: This has been fixed (typo was throwing off the calculations but the theory was right)
    // BUT it needs further testing against more edge-edge collisions to verify fully. For my current test its good though.
    private boolean queryEdgeCollisions(Body reference, Body incident, Manifold results, Type collisionType)
    {
        ArrayList<Edge> edgesA = reference.getEdges();
        ArrayList<Edge> edgesB = incident.getEdges();
        for(int i = 0; i < edgesA.size(); i++) {
            Face referenceFaceA = reference.getFace(edgesA.get(i).getFaceAIndex());
            Face referenceFaceB = reference.getFace(edgesA.get(i).getFaceBIndex());
            Vector3f edgeAFaceANormal = referenceFaceA.getTransformedNormal(reference.getWorldTransform());
            Vector3f edgeAFaceBNormal = referenceFaceB.getTransformedNormal(reference.getWorldTransform());
            for(int j = 0; j < edgesB.size(); j++) {
                Face incidentFaceA = incident.getFace(edgesB.get(j).getFaceAIndex());
                Vector3f edgeBFaceANormal = incidentFaceA.getTransformedNormal(incident.getWorldTransform());
                edgeBFaceANormal.negate();
                Face incidentFaceB = incident.getFace(edgesB.get(j).getFaceBIndex());
                Vector3f edgeBFaceBNormal = incidentFaceB.getTransformedNormal(incident.getWorldTransform());
                edgeBFaceBNormal.negate();

                Vector3f edgeADirection = edgesA.get(i).getTransformedDirection(reference.getWorldTransform()).normalize();
                Vector3f edgeBDirection = edgesB.get(j).getTransformedDirection(incident.getWorldTransform()).normalize();

                if(isMinkowskiFace(edgeAFaceANormal, edgeAFaceBNormal, edgeADirection, edgeBFaceANormal, edgeBFaceBNormal, edgeBDirection)) {
                    Vector3f axis = new Vector3f();
                    edgeADirection.cross(edgeBDirection, axis);
                    if(axis.length() < 0.00000001f) {
                        continue;
                    }
                    axis.normalize();
                    if(axis.dot(edgesA.get(i).getPointA()) < 0) {
                        axis.negate();
                    }

                    Vector3f planePoint = reference.getSupport(axis);

                    Plane plane = new Plane(axis, planePoint);
                    Vector3f negatedNormal = new Vector3f();
                    plane.getNormal().negate(negatedNormal);
                    Vector3f support = incident.getSupport(negatedNormal);

                    float distance = plane.distanceToPoint(support);
                    if(distance > 0) {
                        return false;
                    }
                    if(distance > results.getPenetration()) {
                        results.setPenetration(distance);
                        results.setType(collisionType.ordinal());
                        results.setEnterNormal(axis);
                        results.setEdgeA(i);
                        results.setEdgeB(j);
                        results.setSupportA(planePoint);
                        results.setSupportB(support);
                    }
                }
            }
        }
        return true;
    }

    //TODO: Might be able to use edge directions instead of these expensive cross products as equivalents in checking minkowski stuff
    // But need to verify before I make such a change
    private boolean isMinkowskiFace(Vector3f edgeAFaceANormal, Vector3f edgeAFaceBNormal, Vector3f edgeADirection, Vector3f edgeBFaceANormal, Vector3f edgeBFaceBNormal, Vector3f edgeBDirection) {
        Vector3f edgeACross = new Vector3f();
        edgeAFaceBNormal.cross(edgeAFaceANormal, edgeACross);
        Vector3f edgeBCross = new Vector3f();
        edgeBFaceBNormal.cross(edgeBFaceANormal, edgeBCross);

        float edgeBFaceADirection = edgeBFaceANormal.dot(edgeACross);
        float edgeBFaceBDirection = edgeBFaceBNormal.dot(edgeACross);
        float edgeAFaceADirection = edgeAFaceANormal.dot(edgeBCross);
        float edgeAFaceBDirection = edgeAFaceBNormal.dot(edgeBCross);

        return ((edgeBFaceADirection * edgeBFaceBDirection < 0) && (edgeAFaceADirection * edgeAFaceBDirection < 0) && (edgeBFaceADirection * edgeAFaceBDirection > 0));
    }

    private void getFaceContactPoints(Body reference, Body incident, Manifold results) {
        int incidentFace = -1;
        float minDot = Float.MAX_VALUE;
        for(int i = 0; i < incident.getFaceCount(); i++) {
            float dot = results.getEnterNormal().dot(incident.getFace(i).getTransformedNormal(incident.getWorldTransform()));
            if(dot < minDot) {
                minDot = dot;
                incidentFace = i;
            }
        }

        Set<Plane> planes = getSidePlanes(reference, results.getReferenceFace());

        ArrayList<Vector3f> faceVertices = incident.getVerticesOfFace(incidentFace);
        ArrayList<Vector3f> transformedVertices = new ArrayList<>();
        for(int i = 0; i < faceVertices.size(); i++) {
            Vector3f transformedVertex = new Vector3f();
            faceVertices.get(i).mulPosition(incident.getWorldTransform(), transformedVertex);
            transformedVertices.add(transformedVertex);
        }

        Iterator<Plane> iterator = planes.iterator();
        while(iterator.hasNext()) {
            transformedVertices = clipFace(iterator.next(), transformedVertices);
        }
        Debug.addClipPoints(transformedVertices);

        ArrayList<ContactPoint> points = getDeepestPoints(reference, results.getReferenceFace(), incident, incidentFace, results.getType(), transformedVertices);
        results.addContactPoints(points);
        Debug.addContactPoints(points);
    }

    //TODO: Test more thoroughly, but the point of contact is looking pretty good in the test scenario
    private void getEdgeContactPoint(Body reference, Body incident, Manifold results) {
        Edge referenceEdge = reference.getEdge(results.getEdgeA());
        Edge incidentEdge = incident.getEdge(results.getEdgeB());

        Edge referenceEdgeTransformed = new Edge(referenceEdge.getPointA().mulPosition(reference.getWorldTransform(), new Vector3f()), referenceEdge.getPointB().mulPosition(reference.getWorldTransform(), new Vector3f()));
        Edge incidentEdgeTransformed = new Edge(incidentEdge.getPointA().mulPosition(incident.getWorldTransform(), new Vector3f()), incidentEdge.getPointB().mulPosition(incident.getWorldTransform(), new Vector3f()));

        if(referenceEdgeTransformed == null || incidentEdgeTransformed == null) {
            return;
        }

        ArrayList<Vector3f> clipPoints = new ArrayList<>();
        clipPoints.add(referenceEdgeTransformed.getPointA());
        clipPoints.add(referenceEdgeTransformed.getPointB());
        clipPoints.add(incidentEdgeTransformed.getPointA());
        clipPoints.add(incidentEdgeTransformed.getPointB());
        Debug.addClipPoints(clipPoints);

        Vector3f edgeDirectionA = new Vector3f(referenceEdgeTransformed.getDirection());
        Vector3f edgeDirectionB = new Vector3f(incidentEdgeTransformed.getDirection());
        Vector3f directionAToB = new Vector3f();
        referenceEdgeTransformed.getPointA().sub(incidentEdgeTransformed.getPointA(), directionAToB);
        float edgeADotEdgeA = edgeDirectionA.dot(edgeDirectionA);
        float edgeADotEdgeB = edgeDirectionA.dot(edgeDirectionB);
        float edgeBDotEdgeB = edgeDirectionB.dot(edgeDirectionB);
        float edgeADotDirectionAB = edgeDirectionA.dot(directionAToB);
        float edgeDirectionBDotDirectionAB = edgeDirectionB.dot(directionAToB);
        float directionTotal = edgeADotEdgeA * edgeBDotEdgeB - edgeADotEdgeB * edgeADotEdgeB;

        float referenceNormal = 0, incidentNormal = 0;
        float referenceDirection = directionTotal, incidentDirection = directionTotal;
        if(directionTotal < 0.0001f) {
            referenceNormal = 0.0f;
            referenceDirection = 1.0f;
            incidentNormal = edgeDirectionBDotDirectionAB;
            incidentDirection = edgeBDotEdgeB;
        }
        else {
            referenceNormal = edgeADotEdgeB * edgeDirectionBDotDirectionAB - edgeBDotEdgeB * edgeADotDirectionAB;
            incidentNormal = edgeADotEdgeA * edgeDirectionBDotDirectionAB - edgeADotEdgeB * edgeADotDirectionAB;
            if(referenceNormal < 0.0f) {
                referenceNormal = 0.0f;
                incidentNormal = edgeDirectionBDotDirectionAB;
                incidentDirection = edgeBDotEdgeB;
            }
            else if(referenceNormal > referenceDirection) {
                referenceNormal = referenceDirection;
                incidentNormal = edgeDirectionBDotDirectionAB + edgeADotEdgeB;
                incidentDirection = edgeBDotEdgeB;
            }
        }
        if(incidentNormal < 0.0f) {
            incidentNormal = 0.0f;
            if(-edgeADotDirectionAB < 0.0f) {
                referenceNormal = 0.0f;
            }
            else if(-edgeADotDirectionAB > edgeADotEdgeA) {
                referenceNormal = referenceDirection;
            }
            else {
                referenceNormal = -edgeADotDirectionAB;
                referenceDirection = edgeADotEdgeA;
            }
        }
        else if(incidentNormal > incidentDirection) {
            incidentNormal = incidentDirection;

            if((-edgeADotDirectionAB + edgeADotEdgeB) < 0.0f) {
                referenceNormal = 0.0f;
            }
            else if((-edgeADotDirectionAB + edgeADotEdgeB) > edgeADotEdgeA) {
                referenceNormal = referenceDirection;
            }
            else {
                referenceNormal = (-edgeADotDirectionAB + edgeADotEdgeB);
                referenceDirection = edgeADotEdgeA;
            }
        }

        float referencePointPosition = Math.abs(referenceNormal) < 0.0001f ? 0 : referenceNormal / referenceDirection;
        float incidentPointPosition = Math.abs(incidentNormal) < 0.0001f ? 0 : incidentNormal / incidentDirection;
        Vector3f referenceSupportPoint = new Vector3f();
        Vector3f incidentSupportPoint = new Vector3f();
        edgeDirectionA.mul(referencePointPosition, referenceSupportPoint);
        edgeDirectionB.mul(incidentPointPosition, incidentSupportPoint);

        referenceSupportPoint.add(referenceEdge.getPointA());
        referenceSupportPoint.mulPosition(reference.getWorldTransform());
        incidentSupportPoint.add(incidentEdge.getPointA());
        incidentSupportPoint.mulPosition(incident.getWorldTransform());

        //TODO: Should it be w (sc * u) - (tc * v) or is this somehow equivalent?
        Vector3f contact = new Vector3f();
        referenceSupportPoint.add(incidentSupportPoint, contact);
        contact.div(2);

        Vector3f contactNormal = new Vector3f();
        referenceEdgeTransformed.getDirection().cross(incidentEdgeTransformed.getDirection(), contactNormal);

        ContactPoint point = new ContactPoint(contact, contactNormal, referenceSupportPoint.distance(incidentSupportPoint), reference, incident);
        results.addContactPoint(point);
        Debug.addContactPoint(point);
    }

    private Set<Plane> getSidePlanes(Body object, int face) {
        Set<Plane> planes = new HashSet<>();

        for(int j = 0; j < object.getEdgesOfFace(face).size(); j++) {
            Edge edge = object.getEdgesOfFace(face).get(j);

            int other = edge.getOtherFaceOnEdge(face);
            Vector3f normal = object.getFace(other).getTransformedNormal(object.getWorldTransform());

            Vector3f point = new Vector3f();
            edge.getPointB().mulPosition(object.getWorldTransform(), point);
            Plane plane = new Plane(normal, point);
            planes.add(plane);
        }
        return planes;
    }

    private ArrayList<Vector3f> clipFace(Plane plane, ArrayList<Vector3f> input) {
        ArrayList<Vector3f> safe = new ArrayList<>();
        int j = 1;
        for(int i = 0; i < input.size(); i++) {
            if(j >= input.size()) {
                j = 0;
            }
            Vector3f pointA = new Vector3f(input.get(i));
            Vector3f pointB = new Vector3f(input.get(j));
            boolean contactedA = plane.isPointBehindPlane(pointA);
            boolean contactedB = plane.isPointBehindPlane(pointB);
            if(contactedA && contactedB) {
                if(!safe.contains(pointA)) {
                    safe.add(pointA);
                }
                if(!safe.contains(pointB)) {
                    safe.add(pointB);
                }
            }
            else if(!contactedA && contactedB) {
                Vector3f newPointA = getIntersectionPoint(plane, pointA, pointB);
                if(!safe.contains(newPointA)) {
                    safe.add(newPointA);
                }
                if(!safe.contains(pointB)) {
                    safe.add(pointB);
                }
            }
            else if(contactedA && !contactedB) {
                Vector3f newPointB = getIntersectionPoint(plane, pointA, pointB);
                if(!safe.contains(pointA)) {
                    safe.add(pointA);
                }
                if(!safe.contains(newPointB)) {
                    safe.add(newPointB);
                }
            }
            j++;
        }
        return safe;
    }

    private Vector3f getIntersectionPoint(Plane plane, Vector3f pointA, Vector3f pointB) {
        float distanceA = plane.distanceToPoint(pointA);
        float distanceB = plane.distanceToPoint(pointB);
        Vector3f direction = new Vector3f();
        pointB.sub(pointA, direction);
        float alpha = distanceA / (distanceA - distanceB);
        Vector3f intersection = new Vector3f();
        direction.mul(alpha, intersection);
        return intersection.add(pointA);
    }

    //TODO: Verify that the contact point objects themselves are being created properly. Are the
    // references to body and other being stored the correct way around?
    private ArrayList<ContactPoint> getDeepestPoints(Body object, int face, Body other, int otherFace, int type, ArrayList<Vector3f> clipPoints)
    {
        ArrayList<ContactPoint> safe = new ArrayList<>();

        Vector3f pointOnPlane = new Vector3f();
        object.getVerticesOfFace(face).get(0).mulPosition(object.getWorldTransform(), pointOnPlane);
        Vector3f axis = new Vector3f();
        object.getFace(face).getNormal().mulDirection(object.getWorldTransform(), axis);
        axis.normalize();
        Plane plane = new Plane(axis, pointOnPlane);

        for(int j = 0; j < clipPoints.size(); j++) {
            Vector3f point = new Vector3f(clipPoints.get(j));
            float current = plane.distanceToPoint(point);

            if(plane.isPointBehindPlane(point)) {
                if(type == Type.FACE_OF_B.ordinal()) {
                    safe.add(new ContactPoint(point, axis, current, other, object));
                }
                else {
                    safe.add(new ContactPoint(point, other.getFace(otherFace).getTransformedNormal(other.getWorldTransform()), current, object, other));
                }
            }
        }
        return safe;
    }
}
