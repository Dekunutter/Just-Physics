package com.base.engine.render.shaders;

import com.base.engine.render.Shader;
import com.base.engine.render.ShaderLoader;

public class LightShader extends Shader {
    private static LightShader instance;

    private LightShader() throws Exception {
        super();

        createVertexShader(ShaderLoader.load("lighting_vertex_shader.glsl"));
        createFragmentShader(ShaderLoader.load("lighting_fragment_shader.glsl"));
        link();

        shaderId = 2;
    }

    public static LightShader getInstance() {
        if(instance == null) {
            try {
                instance = new LightShader();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }
}
