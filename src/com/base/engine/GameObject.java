package com.base.engine;

import com.base.engine.loop.GameLoop;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Mesh;
import com.base.engine.render.Shader;
import com.base.engine.render.lighting.LightMap;
import com.base.game.input.InputParser;

public abstract class GameObject implements GameLoop {
    protected static Shader shader;
    protected Body body;
    protected Mesh mesh;
    protected InputParser controller;

    protected GameObject() {

    }

    protected void applyLighting(LightMap lights) {
        shader.setUniform("ambientLight", lights.getAmbientLight().getIntensity());
        shader.setUniform("specularPower", lights.getAmbientLight().getSpecularPower());

        for(int i = 0; i < lights.getPointLightListSize(); i++) {
            shader.setUniform("pointLights", lights.getPointLights()[i].getViewPosition(Renderer.transformation.getViewMatrix()), i);
        }

        shader.setUniform("directionalLight", lights.getDirectionalLight().getViewPosition(Renderer.transformation.getViewMatrix()));

        for(int i = 0; i < lights.getSpotLightListSize(); i++) {
            shader.setUniform("spotLights", lights.getSpotLights()[i].getViewPosition(Renderer.transformation.getViewMatrix()), i);
        }
    }

    public boolean isControlled() {
        if(controller == null) {
            return false;
        }
        return true;
    }

    public void setController(InputParser playerInput) {
        controller = playerInput;
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void render() {
        render(new LightMap());
    }

    public abstract void render(LightMap lights);
}
