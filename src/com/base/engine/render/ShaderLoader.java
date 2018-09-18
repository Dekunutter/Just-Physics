package com.base.engine.render;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderLoader  {
    //TODO: Better handling for when shader files cannot be found instead of crashing with a NullPointerException
    public static String load(String fileName) throws Exception  {
        fileName = "com/base/engine/render/shaders/" + fileName;
        String result;
        ClassLoader classloader = new ShaderLoader().getClass().getClassLoader();
        try(InputStream in = classloader.getResourceAsStream(fileName);
            Scanner scanner = new Scanner(in, "UTF-8"))  {
                result = scanner.useDelimiter("\\A").next();
        } catch(Exception ex) {
            throw new ShaderException("Failed to load shader at " + fileName);
        }
        return result;
    }
}
