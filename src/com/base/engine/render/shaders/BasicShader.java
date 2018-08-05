package com.base.engine.render.shaders;

import com.base.engine.render.Shader;
import com.base.engine.render.ShaderLoader;

public class BasicShader extends Shader {
    private static BasicShader instance;


    private BasicShader() throws Exception {
        super();

        createVertexShader(ShaderLoader.load("vertex_shader.glsl"));
        createFragmentShader(ShaderLoader.load("fragment_shader.glsl"));
        link();

        shaderId = 0;
    }

    public static BasicShader getInstance() {
        if(instance == null) {
            try {
                instance = new BasicShader();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }
}
