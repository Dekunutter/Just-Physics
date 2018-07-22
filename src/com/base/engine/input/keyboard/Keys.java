package com.base.engine.input.keyboard;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Keys {
    private static Keys keys;

    public ArrayList<Integer> up, down, left, right;
    public ArrayList<Integer> enter, space, shift, control, escape;

    //TODO: Refactor this code with key arrays moved into game package as they are game specific, not engine code
    private Keys() {
        up = new ArrayList<>();
        down = new ArrayList<>();
        left = new ArrayList<>();
        right = new ArrayList<>();
        enter = new ArrayList<>();
        space = new ArrayList<>();
        shift = new ArrayList<>();
        control = new ArrayList<>();
        escape = new ArrayList<>();

        up.add(GLFW_KEY_W);
        up.add(GLFW_KEY_UP);

        down.add(GLFW_KEY_S);
        down.add(GLFW_KEY_DOWN);

        left.add(GLFW_KEY_A);
        left.add(GLFW_KEY_LEFT);

        right.add(GLFW_KEY_D);
        right.add(GLFW_KEY_RIGHT);

        enter.add(GLFW_KEY_ENTER);

        space.add(GLFW_KEY_SPACE);

        shift.add(GLFW_KEY_LEFT_SHIFT);
        shift.add(GLFW_KEY_RIGHT_SHIFT);

        control.add(GLFW_KEY_LEFT_CONTROL);
        control.add(GLFW_KEY_RIGHT_CONTROL);

        escape.add(GLFW_KEY_ESCAPE);
    }

    public static Keys getInstance()
    {
        if(keys == null)
        {
            keys = new Keys();
        }
        return keys;
    }
}
