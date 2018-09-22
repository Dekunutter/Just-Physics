package com.base.game.input;

import com.base.engine.input.Input;
import com.base.engine.input.InputHandler;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.input.mouse.MouseButton;
import com.base.engine.input.mouse.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputParser {
    private static Map<InputCommand, List<Input>> bindings;
    private static InputHandler handler;

    public InputParser() {
        bindings = new HashMap<>();
        handler = new InputHandler();

        List<Input> movementUpBindings = new ArrayList<>();
        movementUpBindings.add(Keys.W);
        movementUpBindings.add(Keys.UP);
        bindings.put(InputCommand.MOVEMENT_UP, movementUpBindings);

        List<Input> movementDownBindings = new ArrayList<>();
        movementDownBindings.add(Keys.S);
        movementDownBindings.add(Keys.DOWN);
        bindings.put(InputCommand.MOVEMENT_DOWN, movementDownBindings);

        List<Input> movementLeftBindings = new ArrayList<>();
        movementLeftBindings.add(Keys.A);
        movementLeftBindings.add(Keys.LEFT);
        bindings.put(InputCommand.MOVEMENT_LEFT, movementLeftBindings);

        List<Input> movementRightBindings = new ArrayList<>();
        movementRightBindings.add(Keys.D);
        movementRightBindings.add(Keys.RIGHT);
        bindings.put(InputCommand.MOVEMENT_RIGHT, movementRightBindings);

        List<Input> movementInBindings = new ArrayList<>();
        movementInBindings.add(Keys.Z);
        bindings.put(InputCommand.MOVEMENT_IN, movementInBindings);

        List<Input> movementOutBindings = new ArrayList<>();
        movementOutBindings.add(Keys.X);
        bindings.put(InputCommand.MOVEMENT_OUT, movementOutBindings);

        List<Input> freezeCameraBindings = new ArrayList<>();
        freezeCameraBindings.add(MouseButton.RIGHT);
        bindings.put(InputCommand.FREEZE_CAMERA, freezeCameraBindings);

        List<Input> zoomCameraBindings = new ArrayList<>();
        bindings.put(InputCommand.ZOOM_CAMERA, zoomCameraBindings);

        List<Input> colourUpBindings = new ArrayList<>();
        colourUpBindings.add(Keys.W);
        colourUpBindings.add(Keys.UP);
        bindings.put(InputCommand.COLOUR_UP, colourUpBindings);

        List<Input> colourDownBindings = new ArrayList<>();
        colourDownBindings.add(Keys.S);
        colourDownBindings.add(Keys.DOWN);
        bindings.put(InputCommand.COLOUR_DOWN, colourDownBindings);

        List<Input> scaleObjectBindings = new ArrayList<>();
        scaleObjectBindings.add(Keys.LEFT_SHIFT);
        bindings.put(InputCommand.SCALE_OBJECT, scaleObjectBindings);
    }

    public static boolean hold(InputCommand command) {
        return handler.hold(bindings.get(command));
    }

    public static boolean scroll() {
        return handler.scroll();
    }

    public static double getScroll() {
        return handler.getScroll();
    }

    public static void freezeMouse() {
        Mouse.freeze();
    }

    public static void unfreezeMouse() {
        Mouse.unfreeze();
    }
}
