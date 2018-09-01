package com.base.game;

import com.base.engine.Debug;
import com.base.engine.GameObject;
import com.base.engine.OBJLoader;
import com.base.engine.Time;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Material;
import com.base.engine.render.Shader;
import com.base.engine.render.Texture;
import com.base.engine.render.TextureLoader;
import com.base.engine.render.lighting.DirectionalLight;
import com.base.engine.render.lighting.PointLight;
import com.base.engine.render.lighting.SpotLight;
import com.base.engine.render.shaders.LightShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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
    //TODO: Need a better way of pass lights between the game world and each object they interact with
    private Vector3f ambientLight;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private SpotLight spotLight;
    private float specularPower;

    public TestObject() throws Exception {
        body = new Body();
        body.setPosition(0, 0, -5);
        //body.addForce(new Vector3f(1.0f, 0, 0));
        body.setMass(1.0f);

        shader = LightShader.getInstance();
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");
        shader.createPointLightUniform("pointLight");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createSpotLightUniform("spotLight");
        shader.assign(this);

        Texture texture = TextureLoader.getInstance().getTexture("res/textures/grassblock.png");
        Material material = new Material(texture, 1f);
        mesh = OBJLoader.loadMesh("res/models/cube.obj");
        mesh.setMaterial(material);

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
        //body.setRotation(rotation, rotation, rotation);

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
        Matrix4f projectionMatrix = Renderer.transformation.getProjectionMatrix(Renderer.FIELD_OF_VIEW, Renderer.Z_NEAR, Renderer.Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = Renderer.transformation.getViewMatrix();

        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("specularPower", specularPower);
        PointLight currentPointLight = new PointLight(pointLight);
        Vector3f lightPosition = currentPointLight.getPosition();
        Vector4f auxilary = new Vector4f(lightPosition, 1.0f);
        auxilary.mul(viewMatrix);
        lightPosition.x = auxilary.x;
        lightPosition.y = auxilary.y;
        lightPosition.z = auxilary.z;
        shader.setUniform("pointLight", currentPointLight);

        DirectionalLight currentDirectionalLight = new DirectionalLight(directionalLight);
        Vector4f direction = new Vector4f(currentDirectionalLight.getDirection(), 0);
        direction.mul(viewMatrix);
        currentDirectionalLight.setDirection(new Vector3f(direction.x, direction.y, direction.z));
        Debug.println("light is shining in %s", direction);
        shader.setUniform("directionalLight", currentDirectionalLight);

        SpotLight currentSpotLight = new SpotLight(spotLight);
        Vector4f spotDirection = new Vector4f(currentSpotLight.getDirection(), 0);
        spotDirection.mul(viewMatrix);
        currentSpotLight.setDirection(new Vector3f(spotDirection.x, spotDirection.y, spotDirection.z));
        Vector3f spotLightPosition = currentSpotLight.getPointLight().getPosition();
        Vector4f auxilarySpot = new Vector4f(spotLightPosition, 0);
        auxilarySpot.mul(viewMatrix);
        currentSpotLight.getPointLight().setPosition(new Vector3f(auxilarySpot.x, auxilarySpot.y, auxilarySpot.z));
        shader.setUniform("spotLight", currentSpotLight);

        Matrix4f modelViewMatrix = Renderer.transformation.getModelViewMatrix(body.getRenderPosition(), body.getRenderRotation(), body.getRenderScale(), viewMatrix);
        shader.setUniform("modelViewMatrix", modelViewMatrix);

        shader.setUniform("texture_sampler", 0);
        shader.setUniform("material", mesh.getMaterial());

        mesh.render();

        shader.unbind();
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public void setSpotLight(SpotLight spotLight) {
        this.spotLight = spotLight;
    }

    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }

    @Override
    public void cleanUp() {
        shader.unassign(this);
        mesh.cleanUp();
    }
}
