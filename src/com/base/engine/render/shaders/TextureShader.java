package com.base.engine.render.shaders;

import com.base.engine.render.Shader;
import com.base.engine.render.ShaderLoader;

public class TextureShader extends Shader {
    private static TextureShader instance;

    private TextureShader() throws Exception {
        super();

        createVertexShader(ShaderLoader.load("texture_vertex_shader.glsl"));
        createFragmentShader(ShaderLoader.load("texture_fragment_shader.glsl"));
        link();

        shaderId = 1;
    }

    public static TextureShader getInstance() throws Exception {
        if(instance == null) {
            instance = new TextureShader();
        }
        return instance;
    }
}
