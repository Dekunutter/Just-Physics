package com.base.engine.physics.collision;

public interface CollisionAlgorithm {
    Manifold detect(CollisionIsland island);
}
