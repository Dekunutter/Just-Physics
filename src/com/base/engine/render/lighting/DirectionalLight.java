package com.base.engine.render.lighting;

import org.joml.Vector3f;

public class DirectionalLight {
    private Vector3f colour, direction;
    private float intensity;

    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        this.colour = new Vector3f(colour);
        this.direction = new Vector3f(direction);
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight other) {
        this(other.getColour(), other.getDirection(), other.getIntensity());
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour.set(colour);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }

    public float getIntensity () {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setColourR(float value) {
        colour.x = value;
    }

    public void setColourG(float value) {
        colour.y = value;
    }

    public void setColourB(float value) {
        colour.z = value;
    }

    public void setDirectionX(float value) {
        direction.x = value;
    }

    public void setDirectionY(float value) {
        direction.y = value;
    }
}
