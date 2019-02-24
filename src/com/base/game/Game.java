package com.base.game;

import com.base.engine.Debug;
import com.base.engine.Engine;
import com.base.engine.GameState;
import com.base.engine.Time;
import com.base.engine.input.mouse.Mouse;
import com.base.engine.physics.Integration;
import com.base.game.input.InputCommand;
import com.base.game.input.InputParser;

import static org.lwjgl.opengl.GL11.glClearColor;

public class Game extends GameState {
    private static Game instance;

    private float colour, colourDirection;
    private World world;

    private InputParser playerInput;
    private boolean isPausePressed;
    private boolean paused;

    public static Game getInstance() {
        if(instance == null) {
            instance = new Game();
        }
        return instance;
    }

    private Game() {
        gameTitle = "Just Physics";

        isPausePressed = false;
        paused = false;
    }

    @Override
    public void start() throws Exception {
        colour = 0.0f;
        colourDirection = 0.0f;

        world = new World();

        playerInput = new InputParser();
    }

    @Override
    public void getInput() {
        Mouse.getInput(Engine.window);
        if(playerInput.press(InputCommand.PAUSE)) {
            isPausePressed = true;
        } else {
            isPausePressed = false;
        }
        Debug.listenForStepInput(playerInput);

        if(playerInput.hold(InputCommand.COLOUR_UP)) {
            colourDirection = 1.0f;
        }
        if(playerInput.hold(InputCommand.COLOUR_DOWN)) {
            colourDirection = -1.0f;
        }
        if(!playerInput.hold(InputCommand.COLOUR_UP) && !playerInput.hold(InputCommand.COLOUR_DOWN)) {
            colourDirection = 0.0f;
        }

        if(!paused) {
            world.getInput();
        }
    }

    @Override
    public void update(Integration integrationType) {
        Mouse.update();

        updatePauseState();

        Debug.activateStepUpdate(paused);
        if(!paused || Debug.willStepUpdate()) {
            Debug.update();
            world.update(integrationType);
            Debug.resetStepUpdate();
        }

        colour += colourDirection * Time.getDelta();
        if(colour > 1.0f) {
            colour = 1.0f;
        } else if(colour < 0.0f) {
            colour = 0.0f;
        }
    }

    private void updatePauseState() {
        if (!paused && isPausePressed) {
            paused = true;
        } else if(paused && isPausePressed){
            paused = false;
        }
    }

    @Override
    public void render() {
        glClearColor(colour, colour, colour, 0.0f);

        world.render();
    }

    public void interpolate(float alpha) {
        world.interpolate(alpha);
    }

    public InputParser getPlayerInput() {
        return playerInput;
    }

    @Override
    public void cleanUp() {
        world.cleanUp();
    }
}
