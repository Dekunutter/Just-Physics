package com.base.engine.render;

import com.base.engine.FileLoader;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureLoader {
    private static TextureLoader instance;

    private final HashMap<String, Texture> table = new HashMap<>();
    private final ColorModel glAlphaColorModel, glColorModel;
    private final IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(1);

    private TextureLoader() {
        int[] bits = {8, 8, 8, 8};
        boolean hasAlpha = true;
        boolean alphaPermitted = false;

        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), bits, hasAlpha, alphaPermitted, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8, 8, 8, 0}, hasAlpha, alphaPermitted, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
    }

    public Texture getTexture(String resourceName) throws IOException {
        Texture texture = table.get(resourceName);
        if(texture != null) {
            return texture;
        }

        texture = getTexture(resourceName, GL_TEXTURE_2D, GL_RGBA, GL_NEAREST, GL_NEAREST);
        table.put(resourceName, texture);
        return texture;
    }

    public Texture getTexture(String resourceName, int target, int dstPixelFormat, int minFilter, int magFilter) throws IOException {
        int srcPixelFormat;
        int textureID = createTextureID();
        Texture texture = new Texture(textureID);

        glBindTexture(target, textureID);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        BufferedImage bufferedImage = FileLoader.loadImageFromResources(resourceName);
        texture.setWidth(bufferedImage.getWidth());
        texture.setHeight(bufferedImage.getHeight());

        if(bufferedImage.getColorModel().hasAlpha())
        {
            srcPixelFormat = GL_RGBA;
        }
        else
        {
            srcPixelFormat = GL_RGB;
        }

        if(target == GL_TEXTURE_2D)
        {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

        glTexImage2D(target, 0, dstPixelFormat, get2Fold(bufferedImage.getWidth()), get2Fold(bufferedImage.getHeight()), 0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        return texture;
    }

    private int createTextureID() {
        glGenTextures(textureIDBuffer);
        return textureIDBuffer.get(0);
    }

    private static int get2Fold(int fold)
    {
        int ret = 2;
        while(ret < fold)
        {
            ret *= 2;
        }
        return ret;
    }

    private ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture)
    {
        ByteBuffer imageBuffer;
        WritableRaster raster;
        BufferedImage textureImage;

        int textureWidth = 2;
        int textureHeight = 2;

        while(textureWidth < bufferedImage.getWidth())
        {
            textureWidth *= 2;
        }
        while(textureHeight < bufferedImage.getHeight())
        {
            textureHeight *= 2;
        }

        if(bufferedImage.getColorModel().hasAlpha())
        {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, textureWidth, textureHeight, 4, null);
            textureImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        }
        else
        {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, textureWidth, textureHeight, 3, null);
            textureImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
        }

        Graphics g = textureImage.getGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, textureWidth, textureHeight);
        g.drawImage(bufferedImage, 0, 0, null);

        byte[] data = ((DataBufferByte) textureImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }

    public static TextureLoader getInstance() {
        if(instance == null) {
            instance = new TextureLoader();
        }
        return instance;
    }
}
