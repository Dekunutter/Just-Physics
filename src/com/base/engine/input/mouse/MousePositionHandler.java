package com.base.engine.input.mouse;

import com.base.engine.Engine;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MousePositionHandler extends GLFWCursorPosCallback {
    private final Vector3d position, previous, freezePosition, difference;
    private boolean isFrozen;

    public MousePositionHandler() {
        previous = new Vector3d(-1, -1, 0);
        position = new Vector3d();
        freezePosition = new Vector3d();
        difference = new Vector3d();
        isFrozen = false;
    }

    @Override
    public void invoke(long window, double x, double y) {
        position.x = x;
        position.y = Engine.window.getHeight() - y;
    }

    public Vector3d getPosition() {
        return new Vector3d(position).sub(difference);
    }

    public Vector3d getPreviousPosition() {
        return new Vector3d(previous).sub(difference);
    }

    public void setPreviousPosition() {
        previous.x = position.x;
        previous.y = position.y;
    }

    public void freeze() {
        if(!isFrozen) {
            freezePosition.x = getPosition().x;
            freezePosition.y = getPosition().y;
            isFrozen = true;
        }
    }

    public void unfreeze() {
        if(isFrozen) {
            difference.x = position.x - freezePosition.x;
            difference.y = position.y - freezePosition.y;
            isFrozen = false;
        }
    }

    public Vector3d getFreezePosition() {
        return freezePosition;
    }
}
