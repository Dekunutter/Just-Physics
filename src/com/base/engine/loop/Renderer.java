package com.base.engine.loop;

import com.base.engine.Engine;
import com.base.engine.GameState;
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
        game.render();
        Engine.window.update();
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
