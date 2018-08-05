package com.base.engine.render;

import com.base.engine.Debug;
import com.base.engine.loop.Renderer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader {
    protected int shaderId;
    protected static Set<Object> owners;
    private final int programId;
    private int vertexId, geometryId, fragmentId;
    private final Map<String, Integer> uniforms;

    public Shader() throws Exception {
        owners = new HashSet<>();

        programId = glCreateProgram();
        if(programId == 0) {
            throw new Exception("Could not create shader");
        }
        uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createGeometryShader(String shaderCode) throws Exception {
        geometryId = createShader(shaderCode, GL_GEOMETRY_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if(shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(programId);
        if(glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if(vertexId != 0) {
            glDetachShader(programId, vertexId);
        }
        if(geometryId != 0) {
            glDetachShader(programId, geometryId);
        }
        if(fragmentId != 0) {
            glDetachShader(programId, fragmentId);
        }

        if(Debug.isDebuggingEnabled()) {
            glValidateProgram(programId);
            if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
                System.err.println("Warning validing Shader code: " + glGetProgramInfoLog(programId, 1024));
            }
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getId() {
        return shaderId;
    }

    public void assign(Object owner) {
        if(owners.isEmpty()) {
            Renderer.activateShader(this);
        }
        owners.add(owner);
    }

    public void unassign(Object owner) {
        owners.remove(owner);
        if(owners.isEmpty()) {
            Renderer.removeShader(this);
            cleanUp();
        }
    }

    public void cleanUp() {
        unbind();

        if(programId != 0) {
            glDeleteProgram(programId);
        }
    }
}
