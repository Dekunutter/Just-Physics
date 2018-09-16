package com.base.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public static BufferedReader loadFileFromResources(String reference) throws IOException {
        URL url = FileLoader.class.getClassLoader().getResource(reference);

        if(url == null) {
            throw new IOException("Cannot find: " + reference);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br;
    }

    public static boolean createFileIfMissing(String reference) throws IOException {
        File requestedFile = new File(reference);

        if(!requestedFile.getParentFile().exists()) {
            requestedFile.getParentFile().mkdirs();
        }

        if(!requestedFile.exists()) {
            requestedFile.createNewFile();
            return true;
        }
        return false;
    }

    public static BufferedReader openFileForReading(String reference) throws IOException {
        File requestedFile = new File(reference);

        FileReader fr = new FileReader(requestedFile);
        BufferedReader br = new BufferedReader(fr);
        return br;
    }

    public static BufferedWriter openFileForWriting(String reference, boolean appendMode) throws IOException {
        File requestedFile = new File(reference);

        FileWriter fw = new FileWriter(requestedFile, appendMode);
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }
}
