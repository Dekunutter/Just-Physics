package com.base.engine.render.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SpotLight extends Light {
    public static final int MAX_SPOT_LIGHTS = 10;

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

    public void setDirection(float x, float y, float z) {
        direction.set(x, y, z);
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

    public SpotLight getViewPosition(Matrix4f viewMatrix) {
        SpotLight viewedLight = new SpotLight(this);
        Vector4f spotDirection = new Vector4f(viewedLight.getDirection(), 0);
        spotDirection.mul(viewMatrix);
        viewedLight.setDirection(spotDirection.x, spotDirection.y, spotDirection.z);

        Vector3f spotLightPosition = viewedLight.getPointLight().getPosition();
        Vector4f auxiliaryPosition = new Vector4f(spotLightPosition, 0);
        auxiliaryPosition.mul(viewMatrix);
        viewedLight.getPointLight().setPosition(auxiliaryPosition.x, auxiliaryPosition.y, auxiliaryPosition.z);

        return viewedLight;
    }
}
