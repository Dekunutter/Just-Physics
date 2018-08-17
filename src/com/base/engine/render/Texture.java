package com.base.engine.render;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class Texture {
    private final int textureID;
    private int width, height;

    public Texture(int textureID)
    {
        this.textureID = textureID;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getId()
    {
        return textureID;
    }

    public void cleanUp()
    {
        glDeleteTextures(textureID);
    }
}
