package com.base.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class FileLoader {
    public static BufferedImage loadImageFromResources(String reference) throws IOException {
        URL url = FileLoader.class.getClassLoader().getResource(reference);
        if(url == null) {
            throw new IOException("Cannot find image file: " + reference);
        }

        Image image = new ImageIcon(url).getImage();
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bufferedImage;
    }
}
