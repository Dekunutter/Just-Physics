package com.base.engine.input.mouse;

import com.base.engine.input.Input;
import com.base.engine.input.InputHardware;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class MouseButton {
    private static InputHardware hardware = InputHardware.MOUSE;

    public static Input LEFT = new Input(GLFW_MOUSE_BUTTON_1, hardware);
    public static Input RIGHT = new Input(GLFW_MOUSE_BUTTON_2, hardware);
}
