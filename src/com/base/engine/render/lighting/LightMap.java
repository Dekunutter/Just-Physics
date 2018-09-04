package com.base.engine.render.lighting;

public class LightMap {
    private AmbientLight ambience;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;
    private DirectionalLight directionalLight;
    private int addedPointLights, addedSpotLights;

    public LightMap() {
        addedPointLights = 0;
        pointLights = new PointLight[PointLight.MAX_POINT_LIGHTS];
        addedSpotLights = 0;
        spotLights = new SpotLight[SpotLight.MAX_SPOT_LIGHTS];
    }

    public void put(AmbientLight ambience) {
        this.ambience = ambience;
    }

    public void put(PointLight pointLight) {
        pointLights[addedPointLights] = pointLight;
        addedPointLights++;
    }

    public void put(SpotLight spotLight) {
        spotLights[addedSpotLights] = spotLight;
        addedSpotLights++;
    }

    public void put(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public AmbientLight getAmbientLight() {
        return ambience;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public int getPointLightListSize() {
        return addedPointLights;
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public int getSpotLightListSize() {
        return addedSpotLights;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }
}
