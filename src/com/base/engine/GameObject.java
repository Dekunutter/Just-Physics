package com.base.engine;

import com.base.engine.loop.GameLoop;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Mesh;
import com.base.engine.render.Shader;
import com.base.game.World;
import org.joml.Matrix4f;

public abstract class GameObject implements GameLoop {
    protected World world;
    protected static Shader shader;
    protected Body body;
    protected Mesh mesh;

    protected GameObject(World world) {
        this.world = world;
    }

    protected void applyLighting(Matrix4f viewMatrix) {
        shader.setUniform("ambientLight", world.getLights().getAmbientLight().getIntensity());
        shader.setUniform("specularPower", world.getLights().getAmbientLight().getSpecularPower());

        for(int i = 0; i < world.getLights().getPointLightListSize(); i++) {
            shader.setUniform("pointLights", world.getLights().getPointLights()[i].getViewPosition(viewMatrix), i);
        }

        shader.setUniform("directionalLight", world.getLights().getDirectionalLight().getViewPosition(viewMatrix));

        for(int i = 0; i < world.getLights().getSpotLightListSize(); i++) {
            shader.setUniform("spotLights", world.getLights().getSpotLights()[i].getViewPosition(viewMatrix), i);
        }
    }
}
