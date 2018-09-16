package com.base.engine.input;

public class Input {
    private int value;
    private InputHardware hardware;

    public Input(int value) {
        this.value = value;
        hardware = InputHardware.KEYBOARD;
    }

    public Input(int value, InputHardware hardware) {
        this.value = value;
        this.hardware = hardware;
    }

    public int getValue() {
        return value;
    }

    public InputHardware getHardware() {
        return hardware;
    }
}
