package com.base.engine.render.shaders;

import com.base.engine.render.Shader;
import com.base.engine.render.ShaderLoader;

public class BillboardShader extends Shader {
    private static BillboardShader instance;

    public BillboardShader() throws Exception {
        super();

        createVertexShader(ShaderLoader.load("billboard_vertex_shader.glsl"));
        createFragmentShader(ShaderLoader.load("billboard_fragment_shader.glsl"));
        link();

        shaderId = 3;
    }

    public static BillboardShader getInstance() throws Exception {
        if(instance == null) {
            instance = new BillboardShader();
        }
        return instance;
    }
}
