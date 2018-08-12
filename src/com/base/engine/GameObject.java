package com.base.engine;

import com.base.engine.loop.GameLoop;
import com.base.engine.physics.body.Body;
import com.base.engine.render.Mesh;

public abstract class GameObject implements GameLoop {
    protected Body body;
    protected Mesh mesh;
    protected Transformation transformation = new Transformation();
}
