package com.base.game;

import com.base.engine.GameObject;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Mesh;
import com.base.engine.render.Shader;
import com.base.engine.render.shaders.BasicShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TestObject extends GameObject {
    private float[] vertices = new float[] {
        -0.5f, 0.5f, -1.05f,
        -0.5f, -0.5f, -1.05f,
        0.5f, -0.5f, -1.05f,
        0.5f, 0.5f, -1.05f
    };
    private float[] colours = new float[] {
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f
    };
    private int[] indices = new int[] {
            0, 1, 3, 3, 1, 2
    };
    private static Shader shader;
    private float scaleModifier;

    public TestObject() throws Exception {
        body = new Body();
        body.setPosition(0, 0, -2);
        body.addForce(new Vector3f(1.0f, 0, 0));
        body.setMass(1.0f);

        shader = BasicShader.getInstance();
        shader.createUniform("projectionMatrix");
        shader.createUniform("worldMatrix");
        shader.assign(this);

        mesh = new Mesh(vertices, colours, indices);

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

        mesh.render();

        shader.unbind();
    }

    @Override
    public void cleanUp() {
        shader.unassign(this);
        mesh.cleanUp();
    }
}
