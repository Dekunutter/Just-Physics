package com.base.engine;

import com.base.engine.loop.GameLoop;
import com.base.engine.physics.body.Body;

public abstract class GameObject implements GameLoop {
    protected Body body;
}
