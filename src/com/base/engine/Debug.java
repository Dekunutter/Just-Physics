package com.base.engine;

import com.base.engine.input.Input;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.loop.Renderer;
import com.base.engine.physics.collision.ContactPoint;
import com.base.engine.render.Colour;
import com.base.engine.render.Material;
import com.base.engine.render.Mesh;
import com.base.engine.render.Shader;
import com.base.engine.render.shaders.BillboardShader;
import com.base.game.input.InputCommand;
import com.base.game.input.InputParser;
import org.joml.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Debug {
    private static boolean enabled = false;
    private static boolean polygonMode = false;
    private static boolean isStepPressed = false;
    private static boolean isStepActive = false;
    private static Shader billboardShader;
    private static ArrayList<ContactPoint> contactPoints = new ArrayList<>();

    public static NumberFormat formatter = new DecimalFormat("0.0000000000");

    public static void enableDebug() {
        enabled = true;
    }

    public static void disableDebug() {
        enabled = false;
    }

    public static void enablePolygonMode() {
        polygonMode = true;
    }

    public static void disablePolygonMode() {
        polygonMode = false;
    }

    public static boolean isDebuggingEnabled() {
        return enabled;
    }

    public static boolean isPolygonModeEnabled() {
        return polygonMode;
    }

    public static void addStepInputBindings(Map<InputCommand, List<Input>> bindings) {
        List<Input> stepDebugBindings = new ArrayList<>();
        stepDebugBindings.add(Keys.O);
        bindings.put(InputCommand.STEP_DEBUG, stepDebugBindings);
    }

    public static void listenForStepInput(InputParser input) {
        if(input.press(InputCommand.STEP_DEBUG)) {
            isStepPressed = true;
        } else {
            isStepPressed = false;
        }
    }

    public static void activateStepUpdate(boolean paused) {
        if(paused && isStepPressed) {
            isStepActive = true;
        }
    }

    public static boolean willStepUpdate() {
        return isStepActive;
    }

    public static void resetStepUpdate() {
        isStepActive = false;
    }

    public static void println(String line) {
        if(!enabled) {
            return;
        }

        System.out.println(line);
    }

    public static void println(String line, Object... args) {
        if(!enabled) {
            return;
        }

        for(int i = 0; i < args.length; i++) {
            if (args[i] instanceof Vector3f) {
                args[i] = ((Vector3f) args[i]).toString(formatter);
            } else if (args[i] instanceof Vector4f) {
                args[i] = ((Vector4f) args[i]).toString(formatter);
            } else if (args[i] instanceof Matrix3f) {
                args[i] = ((Matrix3f) args[i]).toString(formatter);
            } else if (args[i] instanceof Matrix4f) {
                args[i] = ((Matrix4f) args[i]).toString(formatter);
            } else {
            }
        }
        System.out.println(String.format(line, args));
    }

    public static void update() {
        Debug.contactPoints.clear();
    }

    //TODO: Change this to intake contact manifold objects instead so that I can create debug rendering for more than just the contact points
    //TODO: Find a better means of storing contact points since the update runs multiple times and we don't want to build them up so we clear them here before we add them, but this means we will only store the points of the last contact of the last object pairing here
    public static void addContactPoints(ArrayList<ContactPoint> contactPoints) {
        Debug.contactPoints.clear();
        Debug.contactPoints.addAll(contactPoints);
    }

    public static void renderContactPoints() {
        try {
            billboardShader = BillboardShader.getInstance();
            billboardShader.createUniform("projectionMatrix");
            billboardShader.createUniform("modelViewMatrix");
            billboardShader.createUniform("colour");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        float[] vertices = {-0.05f, 0.05f, 0, -0.05f, -0.05f, 0, 0.05f, -0.05f, 0, 0.05f, 0.05f, 0};
        int[] indices = {0, 1, 2, 2, 3, 0};
        Colour colour = new Colour(1, 0, 0);

        for(int i = 0; i < contactPoints.size(); i++) {
            ContactPoint current = contactPoints.get(i);
            billboardShader.assign(current);
            billboardShader.bind();

            Mesh contactMesh = new Mesh(vertices, new float[] {}, new float[] {}, indices);
            Material material = new Material();
            contactMesh.setMaterial(material);

            billboardShader.setUniform("projectionMatrix", Renderer.transformation.getProjectionMatrix());

            Matrix4f modelViewMatrix = Renderer.transformation.getModelViewMatrix(current.getPosition(), new Quaternionf(1, 0, 0, 0), 1, Renderer.transformation.getViewMatrix());
            billboardShader.setUniform("modelViewMatrix", modelViewMatrix);
            billboardShader.setUniform("colour", colour.toVector4f());
            contactMesh.render();
            billboardShader.unbind();
        }
    }
}
