package com.base.engine;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Debug {
    private static boolean enabled = false;
    private static boolean polygonMode = false;

    public static NumberFormat formatter = new DecimalFormat("0.0000000000");

    public static void enableDebug() {
        enabled = true;
    }

    public static void disableDebug() {
        enabled = false;
    }

    public static void enablePolygonMode() {
        polygonMode = true;
    }

    public static void disablePolygonMode() {
        polygonMode = false;
    }

    public static boolean isDebuggingEnabled() {
        return enabled;
    }

    public static boolean isPolygonModeEnabled() {
        return polygonMode;
    }

    public static void println(String line) {
        if(!enabled) {
            return;
        }

        System.out.println(line);
    }

    public static void println(String line, Object... args) {
        if(!enabled) {
            return;
        }

        for(int i = 0; i < args.length; i++) {
            if (args[i] instanceof Vector3f) {
                args[i] = ((Vector3f) args[i]).toString(formatter);
            } else if (args[i] instanceof Vector4f) {
                args[i] = ((Vector4f) args[i]).toString(formatter);
            } else {
            }
        }
        System.out.println(String.format(line, args));
    }
}
