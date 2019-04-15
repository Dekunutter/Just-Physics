package com.base.engine.physics.collision;

import com.base.engine.physics.body.Body;

public interface CollisionAlgorithm {
    Manifold detect(Body bodyA, Body bodyB);
}
