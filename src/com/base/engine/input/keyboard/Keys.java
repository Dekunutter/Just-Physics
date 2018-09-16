package com.base.engine.input.keyboard;

import com.base.engine.input.Input;

import static org.lwjgl.glfw.GLFW.*;

public class Keys {
    public static Input W = new Input(GLFW_KEY_W);
    public static Input A = new Input(GLFW_KEY_A);
    public static Input S = new Input(GLFW_KEY_S);
    public static Input D = new Input(GLFW_KEY_D);
    public static Input Z = new Input(GLFW_KEY_Z);
    public static Input X = new Input(GLFW_KEY_X);
    public static Input UP = new Input(GLFW_KEY_UP);
    public static Input LEFT = new Input(GLFW_KEY_LEFT);
    public static Input DOWN = new Input(GLFW_KEY_DOWN);
    public static Input RIGHT = new Input(GLFW_KEY_RIGHT);
    public static Input SPACE = new Input(GLFW_KEY_SPACE);
    public static Input ESCAPE = new Input(GLFW_KEY_ESCAPE);
    public static Input LEFT_CONTROL = new Input(GLFW_KEY_LEFT_CONTROL);
    public static Input RIGHT_CONTROL = new Input(GLFW_KEY_RIGHT_CONTROL);
    public static Input LEFT_ALT = new Input(GLFW_KEY_LEFT_ALT);
    public static Input RIGHT_ALT = new Input(GLFW_KEY_RIGHT_ALT);
    public static Input ENTER = new Input(GLFW_KEY_ENTER);
    public static Input LEFT_SHIFT = new Input(GLFW_KEY_LEFT_SHIFT);
}
