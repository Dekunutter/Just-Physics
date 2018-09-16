package com.base.game.objects;

import com.base.engine.Camera;
import com.base.engine.GameObject;
import com.base.engine.input.mouse.Mouse;
import com.base.engine.physics.Integration;
import com.base.engine.physics.body.Body;
import com.base.game.World;
import com.base.game.input.InputCommand;
import com.base.game.input.InputParser;
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

        if(controller.hold(InputCommand.MOVEMENT_UP))
        {
            movement.y = 1;
        }
        else if(controller.hold(InputCommand.MOVEMENT_DOWN))
        {
            movement.y = -1;
        }

        if(controller.hold(InputCommand.MOVEMENT_LEFT))
        {
            movement.x = -1;
        }
        else if(controller.hold(InputCommand.MOVEMENT_RIGHT))
        {
            movement.x = 1;
        }

        if(controller.hold(InputCommand.MOVEMENT_IN))
        {
            movement.z = -1;
        }
        else if(controller.hold(InputCommand.MOVEMENT_OUT))
        {
            movement.z = 1;
        }

        if(controller.hold(InputCommand.FREEZE_CAMERA))
        {
            InputParser.freezeMouse();
            rotating = true;
        }
        else
        {
            InputParser.unfreezeMouse();
            rotating = false;
        }

        if(controller.scroll(InputCommand.ZOOM_CAMERA))
        {
            zoomState = (float) Mouse.getScroll() * -1.0f;
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
