package com.base.engine.render.lighting;

import com.base.engine.render.Attenuation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointLight extends Light {
    public static final int MAX_POINT_LIGHTS = 10;

    private Vector3f colour, position;
    protected float intensity;
    private Attenuation attenuation;

    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.colour = new Vector3f(colour);
        this.position = new Vector3f(position);
        this.intensity = intensity;
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity, Attenuation attenuation)
    {
        this(colour, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight)
    {
        this(pointLight.getColour(), pointLight.getPosition(), pointLight.getIntensity(), pointLight.getAttenuation());
    }

    public Vector3f getColour()
    {
        return colour;
    }

    public void setColour(Vector3f colour)
    {
        this.colour = colour;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public float getIntensity()
    {
        return intensity;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    public Attenuation getAttenuation()
    {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation)
    {
        this.attenuation = attenuation;
    }

    public PointLight getViewPosition(Matrix4f viewMatrix) {
        PointLight viewedLight = new PointLight(this);
        Vector3f lightPosition = viewedLight.getPosition();
        Vector4f auxiliaryPosition = new Vector4f(lightPosition, 1.0f);
        auxiliaryPosition.mul(viewMatrix);
        viewedLight.setPosition(auxiliaryPosition.x, auxiliaryPosition.y, auxiliaryPosition.z);

        return viewedLight;
    }
}
