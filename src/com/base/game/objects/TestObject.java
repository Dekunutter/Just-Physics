package com.base.game.objects;

import com.base.engine.GameObject;
import com.base.engine.OBJLoader;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Material;
import com.base.engine.render.Texture;
import com.base.engine.render.TextureLoader;
import com.base.engine.render.lighting.PointLight;
import com.base.engine.render.lighting.SpotLight;
import com.base.engine.render.shaders.LightShader;
import com.base.game.World;
import com.base.game.input.InputCommand;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestObject extends GameObject {
    private float[] vertices = new float[] {
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,
            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,
            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f,
    };
    private float[] textureCoords = new float[] {
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,
            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,
            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,
            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,
    };
    private float[] colours = new float[] {
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f
    };
    private int[] indices = new int[] {
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7,
    };
    private float scaleModifier;

    public TestObject(World world) throws Exception {
        super(world);

        body = new Body();
        body.setPosition(0, 0, -5);
        body.addForce(new Vector3f(1.0f, 0, 0));
        body.addTorque(new Vector3f(1.0f, 0, 0));
        body.setMass(1.0f);

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
    }

    @Override
    public void interpolate(float alpha) {
        body.interpolate(alpha);
    }

    @Override
    public void render() {
        shader.bind();

        shader.setUniform("projectionMatrix", world.getProjectionMatrix());

        applyLighting(world.getViewMatrix());

        Matrix4f modelViewMatrix = Renderer.transformation.getModelViewMatrix(body.getRenderPosition(), body.getRenderOrientation(), body.getRenderScale(), world.getViewMatrix());
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
