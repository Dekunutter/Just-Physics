package com.base.engine.loop;

import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.Time;
import com.base.engine.render.Shader;
import com.base.engine.render.Transformation;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Renderer {
    public static final float FIELD_OF_VIEW = (float) Math.toRadians(60.0f);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR  = 1000.0f;

    public static Transformation transformation;
    public static Map<Integer, Shader> shaders;

    public Renderer() {
        transformation = new Transformation();
        shaders = new HashMap<>();
    }

    public void render(GameState game) {
        clear();
        Engine.window.resizeIfNeeded();
        game.render();
        Engine.window.update();
    }

    public void sync() {
        float endTime = Time.getCurrentTime() + Time.convertSecondsToNanoseconds(Time.getDelta());
        while(Time.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch(InterruptedException ex) {
                System.err.println("Renderer sync failed");
            }
        }
    }

    public static void activateShader(Shader shader) {
        if(!shaders.containsKey(shader.getId())) {
            shaders.put(shader.getId(), shader);
        }
    }

    public static void removeShader(Shader shader) {
        if(!shaders.containsKey(shader.getId())) {
            shaders.remove(shader.getId(), shader);
        }
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
