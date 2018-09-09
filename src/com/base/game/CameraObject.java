package com.base.game;

import com.base.engine.Camera;
import com.base.engine.GameObject;
import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.keyboard.Keys;
import com.base.engine.input.mouse.MouseCursor;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import org.joml.Vector3f;

public class CameraObject extends GameObject {
    private Camera camera;

    private final Vector3f movement;
    private boolean rotating;
    private float zoomState;

    public CameraObject(World world) throws Exception {
        super(world);

        camera = new Camera();
        movement = new Vector3f(0, 0, 0);
        rotating = false;
        zoomState = 0;

        world.addCamera(camera);

        body = new Body();
        body.setPosition(0, 0, 0);
    }

    @Override
    public void getInput() {
        movement.zero();
        zoomState = 0;

        if(Keyboard.isKeyDown(Keys.getInstance().up))
        {
            movement.y = 1;
        }
        else if(Keyboard.isKeyDown(Keys.getInstance().down))
        {
            movement.y = -1;
        }

        if(Keyboard.isKeyDown(Keys.getInstance().left))
        {
            movement.x = -1;
        }
        else if(Keyboard.isKeyDown(Keys.getInstance().right))
        {
            movement.x = 1;
        }

        if(Keyboard.isKeyDown(Keys.getInstance().cameraForwards))
        {
            movement.z = -1;
        }
        else if(Keyboard.isKeyDown(Keys.getInstance().cameraBackwards))
        {
            movement.z = 1;
        }

        if(MouseCursor.isRightDown())
        {
            MouseCursor.freeze();
            rotating = true;
        }
        else
        {
            MouseCursor.unfreeze();
            rotating = false;
        }

        if(MouseCursor.isScrolling())
        {
            zoomState = (float) MouseCursor.getScroll() * -1.0f;
        }
    }

    @Override
    public void update(Integration integrationType) {
        body.updatePreviousState();
        body.advancePhysics(integrationType);

        camera.update(movement, rotating, zoomState);
    }

    @Override
    public void interpolate(float alpha) {
        body.interpolate(alpha);
    }

    @Override
    public void render() {

    }

    @Override
    public void cleanUp() {

    }
}
