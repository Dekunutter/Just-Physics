package com.base.engine.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    private final int vertexArrayId, vertexBufferId, vertexCount;
    private final int colourBufferId;
    private final int indexBufferId;

    public Mesh(float[] positions, float[] colours, int[] indices) {
        FloatBuffer verticesBuffer = null;
        FloatBuffer colourBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;

            vertexArrayId = glGenVertexArrays();
            glBindVertexArray(vertexArrayId);

            vertexBufferId = glGenBuffers();
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            colourBufferId = glGenBuffers();
            colourBuffer = MemoryUtil.memAllocFloat(colours.length);
            colourBuffer.put(colours).flip();
            glBindBuffer(GL_ARRAY_BUFFER, colourBufferId);
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            indexBufferId = glGenBuffers();
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if(verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
            if(colourBuffer != null) {
                MemoryUtil.memFree(colourBuffer);
            }
            if(indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public int getVertexArrayId() {
        return vertexArrayId;
    }

    public int getVertexBufferId() {
        return vertexBufferId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
        glBindVertexArray(getVertexArrayId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vertexBufferId);
        glDeleteBuffers(colourBufferId);
        glDeleteBuffers(indexBufferId);

        glBindVertexArray(0);
        glDeleteVertexArrays(vertexArrayId);
    }
}
