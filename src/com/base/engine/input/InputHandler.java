package com.base.engine.input;

import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.mouse.Mouse;

import java.util.List;

public class InputHandler {
    public InputHandler() {

    }

    public boolean hold(List<Input> inputs) {
        for(int i = 0; i < inputs.size(); i++) {
            InputHardware deviceTouched = inputs.get(i).getHardware();
            switch(deviceTouched) {
                case KEYBOARD:
                    return Keyboard.isKeyHeld(inputs.get(i).getValue());
                case MOUSE:
                    return Mouse.isButtonHeld(inputs.get(i).getValue());
                default:
                    return Keyboard.isKeyHeld(inputs.get(i).getValue());
            }
        }
        return false;
    }

    public boolean pressed(List<Input> inputs) {
        for(int i = 0; i < inputs.size(); i++) {
            InputHardware deviceTouched = inputs.get(i).getHardware();
            switch(deviceTouched) {
                case KEYBOARD:
                    return Keyboard.isKeyPressed(inputs.get(i).getValue());
                case MOUSE:
                    return Mouse.isButtonPressed(inputs.get(i).getValue());
                default:
                    return Keyboard.isKeyPressed(inputs.get(i).getValue());
            }
        }
        return false;
    }

    public boolean scroll() {
        return Mouse.isScrolling();
    }

    public double getScroll() {
        return Mouse.getScroll();
    }
}
