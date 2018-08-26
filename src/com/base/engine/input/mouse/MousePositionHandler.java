package com.base.engine.input.mouse;

import com.base.engine.Engine;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MousePositionHandler extends GLFWCursorPosCallback
{
    private final Vector2d position, previous, freezePosition, difference;
    private boolean frozen;

    public MousePositionHandler()
    {
        previous = new Vector2d(-1, -1);
        position = new Vector2d();
        freezePosition = new Vector2d();
        difference = new Vector2d();
        frozen = false;
    }

    @Override
    public void invoke(long window, double xpos, double ypos)
    {
        position.x = xpos;
        position.y = Engine.window.getHeight() - ypos;
    }

    public Vector2d getPosition()
    {
        return new Vector2d(position).sub(difference);
    }

    public Vector2d getPreviousPosition()
    {
        return new Vector2d(previous).sub(difference);
    }

    public void setPreviousPosition()
    {
        previous.x = position.x;
        previous.y = position.y;
    }

    public void freeze()
    {
        if(!frozen)
        {
            freezePosition.x = getPosition().x;
            freezePosition.y = getPosition().y;
            frozen = true;
        }
    }

    public void unfreeze()
    {
        if(frozen)
        {
            difference.x = position.x - freezePosition.x;
            difference.y = position.y - freezePosition.y;
            frozen = false;
        }
    }

    public Vector2d getFreezePosition()
    {
        return freezePosition;
    }
}
