package com.base.game;

import com.base.engine.GameObject;
import com.base.engine.Time;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Mesh;
import com.base.engine.render.Shader;
import com.base.engine.render.Texture;
import com.base.engine.render.TextureLoader;
import com.base.engine.render.shaders.BasicShader;
import com.base.engine.render.shaders.TextureShader;
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
    private static Shader shader;
    private float scaleModifier;

    public TestObject() throws Exception {
        body = new Body();
        body.setPosition(0, 0, -2);
        body.addForce(new Vector3f(1.0f, 0, 0));
        body.setMass(1.0f);

        //shader = BasicShader.getInstance();
        shader = TextureShader.getInstance();
        shader.createUniform("projectionMatrix");
        shader.createUniform("worldMatrix");
        shader.createUniform("texture_sampler");
        shader.assign(this);

        Texture texture = TextureLoader.getInstance().getTexture("res/textures/grassblock.png");
        mesh = new Mesh(vertices, textureCoords, colours, indices, texture);

        scaleModifier = 0.01f;
    }

    @Override
    public void getInput() {
        if(Keyboard.isKeyDown(Keys.getInstance().shift)) {
            scaleModifier = -0.01f;
        } else {
            scaleModifier = 0.01f;
        }
    }

    @Override
    public void update(Integration integrationType) {
        body.updatePreviousState();
        body.advancePhysics(integrationType);

        float rotation = body.getRotation().x + (1.5f * Time.getDelta());
        if(rotation > 360) {
            rotation = 0;
        }
        body.setRotation(rotation, rotation, rotation);

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

        //TODO: Could be moved out of individual item renders since it is used on all meshes that accept it
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(Renderer.FIELD_OF_VIEW, Renderer.Z_NEAR, Renderer.Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f worldMatrix = transformation.getWorldMatrix(body.getRenderPosition(), body.getRenderRotation(), body.getRenderScale());
        shader.setUniform("worldMatrix", worldMatrix);

        shader.setUniform("texture_sampler", 0);

        mesh.render();

        shader.unbind();
    }

    @Override
    public void cleanUp() {
        shader.unassign(this);
        mesh.cleanUp();
    }
}
