package com.base.engine.render.lighting;

import org.joml.Vector3f;

public class SpotLight {
    private PointLight pointLight;
    private Vector3f direction;
    private float cutOff;

    public SpotLight(PointLight pointLight, Vector3f direction, float cutOffAngle) {
        this.pointLight = pointLight;
        this.direction = new Vector3f(direction);
        setCutOffAngle(cutOffAngle);
    }

    public SpotLight(SpotLight other) {
        this(new PointLight(other.getPointLight()), other.direction, 0);
        cutOff = other.getCutOff();
    }

    public PointLight getPointLight() {
        return pointLight;
    }
    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }

    public float getCutOff() {
        return cutOff;
    }

    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }

    public final void setCutOffAngle(float cutOffAngle) {
        this.setCutOff((float) Math.cos(Math.toRadians(cutOffAngle)));
    }
}
