package com.base.engine.render.lighting;

import org.joml.Vector3f;

public class AmbientLight extends Light {
    private Vector3f intensity;
    private float specularPower;

    public AmbientLight(Vector3f intensity, float specularPower) {
        this.intensity = new Vector3f(intensity);
        this.specularPower = specularPower;
    }

    public Vector3f getIntensity() {
        return intensity;
    }

    public void setIntensity(Vector3f intensity) {
        this.intensity.set(intensity);
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public void setSpecularPower(float value) {
        specularPower = value;
    }
}
