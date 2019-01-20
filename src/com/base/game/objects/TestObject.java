package com.base.game.objects;

import com.base.engine.GameObject;
import com.base.engine.OBJLoader;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Material;
import com.base.engine.render.Texture;
import com.base.engine.render.TextureLoader;
import com.base.engine.render.lighting.LightMap;
import com.base.engine.render.lighting.PointLight;
import com.base.engine.render.lighting.SpotLight;
import com.base.engine.render.shaders.LightShader;
import com.base.game.input.InputCommand;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestObject extends GameObject {
    private float scaleModifier;

    public TestObject(Vector3f position) throws Exception {
        super();

        body = new Body();
        body.loadCollisionData("cube_collision.obj");
        body.setPosition(position.x, position.y, position.z);
        //body.addForce(new Vector3f(1.0f, 0, 0));
        //body.addTorque(new Vector3f(1.0f, 0, 0));
        //body.addForce(new Vector3f(1.0f, 0, 0), new Vector3f(body.getPosition()).sub(0, 0.5f, -0.5f));
        body.setMass(1.0f);
        body.updateMatrix();

        shader = LightShader.getInstance();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createPointLightListUniform("pointLights", PointLight.MAX_POINT_LIGHTS);
        shader.createDirectionalLightUniform("directionalLight");
        shader.createSpotLightListUniform("spotLights", SpotLight.MAX_SPOT_LIGHTS);
        shader.assign(this);

        Texture texture = TextureLoader.getInstance().getTexture("res/textures/grassblock.png");
        Material material = new Material(texture, 1f);
        mesh = OBJLoader.loadMesh("res/models/cube.obj");
        mesh.setMaterial(material);

        scaleModifier = 0.01f;
    }

    @Override
    public void getInput() {
        if(controller.hold(InputCommand.SCALE_OBJECT)) {
            scaleModifier = -0.01f;
        } else {
            scaleModifier = 0.01f;
        }
    }

    @Override
    public void update(Integration integrationType) {
        body.updatePreviousState();
        body.advancePhysics(integrationType);

        body.alterScale(scaleModifier);
        if(body.getScale() > 1) {
            body.setScale(1);
        }

        body.updateMatrix();
        body.clearForces();
    }

    @Override
    public void interpolate(float alpha) {
        body.interpolate(alpha);
    }

    @Override
    public void render(LightMap lights) {
        shader.bind();

        shader.setUniform("projectionMatrix", Renderer.transformation.getProjectionMatrix());

        applyLighting(lights);

        Matrix4f modelViewMatrix = Renderer.transformation.getModelViewMatrix(body.getRenderPosition(), body.getRenderOrientation(), body.getRenderScale(), Renderer.transformation.getViewMatrix());
        shader.setUniform("modelViewMatrix", modelViewMatrix);

        shader.setUniform("texture_sampler", 0);
        shader.setUniform("material", mesh.getMaterial());

        mesh.render();

        shader.unbind();
    }

    @Override
    public void cleanUp() {
        shader.unassign(this);
        mesh.cleanUp();
    }
}
