package com.base.engine.loop;

import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.Time;
import com.base.engine.render.Transformation;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Renderer {
    public static Transformation transformation;

    public Renderer() {
        transformation = new Transformation();
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

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
